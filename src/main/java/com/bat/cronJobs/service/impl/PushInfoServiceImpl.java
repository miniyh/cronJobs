package com.bat.cronJobs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bat.cronJobs.model.PushInfo;
import com.bat.cronJobs.service.PushInfoService;
import com.bat.cronJobs.util.LockUtil;
import com.bat.cronJobs.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.*;

@Service(value = "pshInfoService")
public class PushInfoServiceImpl implements PushInfoService {


    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    @Scheduled(cron="0 0 1 * * ?")
    public int dateToDB() {
        System.out.println("new dateToDB -------- now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        autoPushMysql();
//        System.gc();
        return 0;
    }

    private int autoPushMysql(){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String requestid = UUID.randomUUID().toString();
            String lockKey = "pushLock";
            int expireTime = 100000;
            if (LockUtil.addLock(jedis,requestid,lockKey,expireTime) != 1)
                return 0;
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            Date yesterday=cal.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String str = format.format(yesterday);
            String key = "push:"+str;
            Map<String,String> map = jedis.hgetAll(key);
            Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            List<PushInfo> list = new ArrayList<>();
            PushInfo pushInfo;
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                pushInfo = new PushInfo();
                pushInfo.setUser(Integer.parseInt(entry.getKey()));
                String pushStr = entry.getValue();
                JSONObject pushJson = JSONObject.parseObject(pushStr);
                pushInfo.setPushCount((int)pushJson.get("pushCount"));
                pushInfo.setSuccessPersonCount((int)pushJson.get("successPersonCount"));
                pushInfo.setFailPersonCount((int)pushJson.get("failPersonCount"));
                pushInfo.setSuccessGroupCount((int)pushJson.get("successGroupCount"));
                pushInfo.setFailGroupCount((int)pushJson.get("failGroupCount"));
                pushInfo.setPushDate(yesterday);
                list.add(pushInfo);
            }
            int result = 0;
            if (list.size()>0){
                this.redisToPushInfo(list);
            }
            jedis.del(key);
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
     * 批量处理redis-->PushInfo
     * @param list
     */
    public void redisToPushInfo (List<PushInfo> list) {
        String insertSql = "INSERT INTO push_info(user, push_count,success_person_count,fail_person_count,success_group_count,fail_group_count,push_date)" +
                " VALUES (:user,:pushCount,:successPersonCount,:failPersonCount,:successGroupCount,:failGroupCount,DATE_FORMAT(:pushDate,'%Y-%m-%d'))" +
                " ON DUPLICATE KEY UPDATE " +
                "push_count = :pushCount ,success_person_count = :successPersonCount ," +
                "fail_person_count = :failPersonCount ,success_group_count = :successGroupCount ," +
                "fail_group_count = :failGroupCount";
        SqlParameterSource[] beanSources  = SqlParameterSourceUtils.createBatch(list.toArray());
        namedParameterJdbcTemplate.batchUpdate(insertSql, beanSources);
    }


}
