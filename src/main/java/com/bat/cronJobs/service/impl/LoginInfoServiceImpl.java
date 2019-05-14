package com.bat.cronJobs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bat.cronJobs.model.*;
import com.bat.cronJobs.service.LoginInfoService;
import com.bat.cronJobs.util.LockUtil;
import com.bat.cronJobs.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.*;


@Service(value = "loginInfoService")
public class LoginInfoServiceImpl implements LoginInfoService {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Scheduled(cron="0 0/10 * * * ?")
    public int loginDetailToDB() {
        System.out.println("new loginDetailToDB -------- now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        redisToMysql(0);
        System.gc();
        return 0;
    }

    /**
     * 新的持久化方法   redisToMysql
     * @param redisType
     * @return
     */
    @Override
    public int redisToMysql(int redisType) {
        Jedis jedis = null;
        Map<String,String> map;
        Map<String,Integer> loginInfoMap;
        List<LoginInfo> loginInfoList;
        try{
            jedis = jedisPool.getResource();
            String requestid = UUID.randomUUID().toString();
            int expireTime = 200000;
            String lockKey = "loginDetailLock";
            if (LockUtil.addLock(jedis,requestid,lockKey,expireTime) != 1)
                return 0;
            String key = "loginDetail:all";
            map = jedis.hgetAll(key);
            Long length = jedis.hlen(key);
            List<LoginDetailDate> LoginDetailDateList;
            if (length> 0 && length<=100000)
                LoginDetailDateList = new ArrayList<>(length.intValue());
            else if(length>100000)
                LoginDetailDateList = new ArrayList<>(100000);
            else
                LoginDetailDateList = new ArrayList<>();
            System.out.println("-------redisToMysql------loginDetail:all------length:"+length);
            Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            LoginDetailDate loginDetailDate;
            LoginInfo loginInfo;
            Integer user;
            Date loginTime;
            String loginDateStr;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            loginInfoMap = new HashMap<>();
            int listIndex = 0;
            int limit = 0;
            loginInfoList = new ArrayList<>();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                String loginDetailStr = entry.getValue();
                loginDetailDate = new LoginDetailDate();
                loginInfo = new LoginInfo();
                JSONObject loginDetailJson = JSONObject.parseObject(loginDetailStr);
                user = loginDetailJson.getInteger("user");
                loginTime = loginDetailJson.getDate("loginTime");
                loginDateStr = format.format(loginTime);

                loginDetailDate.setUser(user);
                loginDetailDate.setLoginTime(loginTime);
                loginDetailDate.setState(loginDetailJson.getInteger("state"));
                loginDetailDate.setVersion(loginDetailJson.getString("version"));
                LoginDetailDateList.add(loginDetailDate);

                loginInfo.setUser(user);
                loginInfo.setLoginDate(loginTime);
                loginInfo.setLastLoginTime(loginTime);
                loginInfo.setState(loginDetailJson.getInteger("state"));
                loginInfo.setVersion(loginDetailJson.getString("version"));
                loginInfo.setVersionCode(loginDetailJson.getInteger("versionCode"));
                loginInfo.setPhoneName(loginDetailJson.getString("phoneName"));
                loginInfo.setPhoneSystem(loginDetailJson.getString("phoneSystem"));
                if (loginInfoMap.containsKey(user+":"+loginDateStr)){
                    loginInfo.setLoginCount(loginInfoList.get(loginInfoMap.get(user+":"+loginDateStr)).getLoginCount()+1);
                    loginInfoList.set(loginInfoMap.get(user+":"+loginDateStr),loginInfo);
                }else {
                    loginInfo.setLoginCount(1);
                    loginInfoMap.put(user+":"+loginDateStr,listIndex);
                    loginInfoList.add(listIndex,loginInfo);
                    listIndex++;
                }
                limit++;
                jedis.hdel(key,entry.getKey());
                if (limit>=100000)
                    break;
            }
            int result = 0;
            if (LoginDetailDateList.size()>0){
                this.redisToLoginDetailDate(LoginDetailDateList);
                this.redisToLoginInfo(loginInfoList);
                this.redisToDeviceManage(loginInfoList);
                this.redisToRegisterInfo(loginInfoList);
            }
            this.redisToSysVersion(jedis);
            Boolean deleteLock = RedisUtil.releaseDistributedLock(jedis,lockKey,requestid);
            if (deleteLock){
                System.out.println("delete "+lockKey+" lock success-->requestid:"+requestid);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 批量处理redis-->LoginDetailDate
     * @param LoginDetailDateList
     */
    public void redisToLoginDetailDate (List<LoginDetailDate> LoginDetailDateList){
        SqlParameterSource[] beanSources  = SqlParameterSourceUtils.createBatch(LoginDetailDateList.toArray());
        String sql = "INSERT INTO login_detail_date(user,login_time,state,version) VALUES (:user,:loginTime,:state,:version)";
        namedParameterJdbcTemplate.batchUpdate(sql, beanSources);
    }


    /**
     * 批量处理redis-->LoginInfo
     * @param loginInfoList
     */
    public void redisToLoginInfo (List<LoginInfo> loginInfoList){
        if (loginInfoList==null || loginInfoList.size()<1)
            return;
        List<LoginInfo> insertList = new ArrayList<>();
        List<LoginInfo> updateList = new ArrayList<>();
        String updateSql = "UPDATE login_info SET login_count = login_count+:loginCount,last_login_time  = :lastLoginTime ," +
                "version = :version,version_code = :versionCode,phone_name = :phoneName,phone_system = :phoneSystem," +
                "state = :state where user = :user and login_date = DATE_FORMAT(:loginDate,'%Y-%m-%d')";

        String insertSql = "INSERT INTO login_info(user, login_count, login_date, last_login_time, version,version_code,phone_name,phone_system,state)" +
                " VALUES (:user,:loginCount,:loginDate,:lastLoginTime,:version,:versionCode,:phoneName,:phoneSystem,:state)";
        SqlParameterSource[] updateSources;
        SqlParameterSource[] insertSources;
        Map<String,Object> map;
        for (int i=0;i<loginInfoList.size();i++){
            String querySql = "select count(1) from login_info where user = :user and login_date = DATE_FORMAT(:loginDate,'%Y-%m-%d')";
            map = new HashMap<>();
            map.put("user",loginInfoList.get(i).getUser());
            map.put("loginDate",loginInfoList.get(i).getLoginDate());
            int count = namedParameterJdbcTemplate.queryForObject(querySql,map, Integer.class);
            if (count>0){
                updateList.add(loginInfoList.get(i));
                if (updateList.size()>90){
                    updateSources  = SqlParameterSourceUtils.createBatch(updateList.toArray());
                    namedParameterJdbcTemplate.batchUpdate(updateSql, updateSources);
                    updateList = new ArrayList<>();
                }
            }else
                insertList.add(loginInfoList.get(i));
        }
        if (insertList!=null && insertList.size()>0){
            insertSources  = SqlParameterSourceUtils.createBatch(insertList.toArray());
            namedParameterJdbcTemplate.batchUpdate(insertSql, insertSources);
        }

        if (updateList!=null && updateList.size()>0){
            updateSources  = SqlParameterSourceUtils.createBatch(updateList.toArray());
            namedParameterJdbcTemplate.batchUpdate(updateSql, updateSources);
        }
    }

    /**
     * 批量处理redis-->RegisterInfo
     * @param loginInfoList
     */
    public void redisToRegisterInfo (List<LoginInfo> loginInfoList){
        if (loginInfoList==null || loginInfoList.size()<1)
            return;
        List<LoginInfo> insertList = new ArrayList<>();
        List<LoginInfo> updateList = new ArrayList<>();

        String updateSql = "UPDATE register_info SET version = :version,version_code = :versionCode," +
                "phone_name = :phoneName,phone_system = :phoneSystem,state = :state " +
                "where user = :user";

        String insertSql = "insert into register_info (user, create_time, version,version_code,phone_name,phone_system,state)" +
                " VALUES (:user,:lastLoginTime,:version,:versionCode,:phoneName,:phoneSystem,:state)";

        SqlParameterSource[] updateSources;
        SqlParameterSource[] insertSources;
        Map<String,Object> map;
        for (int i=0;i<loginInfoList.size();i++){
            String querySql = "select count(1) from register_info where user = :user";
            map = new HashMap<>();
            map.put("user",loginInfoList.get(i).getUser());
            int count =  namedParameterJdbcTemplate.queryForObject(querySql,map, Integer.class);
            if (count>0){
                updateList.add(loginInfoList.get(i));
                if (updateList.size()>90){
                    updateSources  = SqlParameterSourceUtils.createBatch(updateList.toArray());
                    namedParameterJdbcTemplate.batchUpdate(updateSql, updateSources);
                    updateList = new ArrayList<>();
                }
            }else
                insertList.add(loginInfoList.get(i));
        }
        if (insertList!=null && insertList.size()>0){
            insertSources  = SqlParameterSourceUtils.createBatch(insertList.toArray());
            namedParameterJdbcTemplate.batchUpdate(insertSql, insertSources);
        }
        if (updateList!=null && updateList.size()>0){
            updateSources  = SqlParameterSourceUtils.createBatch(updateList.toArray());
            namedParameterJdbcTemplate.batchUpdate(updateSql, updateSources);
        }
    }

    /**
     * 批量处理redis-->DeviceManage
     * @param loginInfoList
     */
    public void redisToDeviceManage (List<LoginInfo> loginInfoList) {
        String insertSql = "insert into device_manage (user, phone_name, phone_system, last_login_time)" +
                " VALUES (:user,:phoneName,:phoneSystem,:lastLoginTime)" +
                "ON DUPLICATE KEY UPDATE " +
                "phone_system = :phoneSystem ," +
                "last_login_time = :lastLoginTime";
        SqlParameterSource[] beanSources  = SqlParameterSourceUtils.createBatch(loginInfoList.toArray());
        namedParameterJdbcTemplate.batchUpdate(insertSql, beanSources);
    }

    /**
     * 批量处理redis-->SysVersion
     * @param jedis
     */
    public void redisToSysVersion (Jedis jedis) {
        Map<String, String> sysVersionMap = jedis.hgetAll("sysVersion");
        if (sysVersionMap==null||sysVersionMap.size()<1)
            return;
        Iterator<Map.Entry<String, String>> entries = sysVersionMap.entrySet().iterator();
        List<SysVersion> list = new ArrayList<>();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            String sysVersionStr = entry.getValue();
            JSONObject sysVersionJson = JSONObject.parseObject(sysVersionStr);
            SysVersion sysVersion = new SysVersion();
            sysVersion.setIsAn(sysVersionJson.getInteger("state"));
            sysVersion.setVersion(sysVersionJson.getString("version"));
            sysVersion.setVersionCode(sysVersionJson.getInteger("versionCode"));
            list.add(sysVersion);
            jedis.hdel("sysVersion",sysVersionJson.getString("versionCode"));
        }
        String insertSql = "insert into sys_version (version, version_code, is_an)" +
                " VALUES (:version,:versionCode,:isAn)" +
                "ON DUPLICATE KEY UPDATE " +
                "version = :version ," +
                "is_an = :isAn";
        SqlParameterSource[] beanSources  = SqlParameterSourceUtils.createBatch(list.toArray());
        namedParameterJdbcTemplate.batchUpdate(insertSql, beanSources);
    }
}
