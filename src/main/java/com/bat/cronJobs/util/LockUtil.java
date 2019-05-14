package com.bat.cronJobs.util;

import redis.clients.jedis.Jedis;

public class LockUtil {

    public static int addLock(Jedis jedis, String requestid, String lockKey, int expireTime){
        try {
            Boolean addLock = RedisUtil.tryGetDistributedLock(jedis,lockKey,requestid,expireTime);
            if (!addLock){
                System.out.println("get "+lockKey+" lock fail-->requestid:"+requestid);
                return 0;
            }
            System.out.println("get "+lockKey+" lock success-->requestid:"+requestid);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
