package com.shrb.versionowner.lock;

import java.util.concurrent.ConcurrentHashMap;

public class LockFactory {
    //单例锁对象池
    private static volatile ConcurrentHashMap<String, Object> singletonLocks;

    static {
        singletonLocks = new ConcurrentHashMap<>();
        //添加管理用户信息的锁
        singletonLocks.put("userInfo", new Object());
    }

    /**
     * 获取单例锁对象
     * @param name
     * @return
     */
    public static Object getLock(String name) {
        Object lock = singletonLocks.get(name);
        if(lock == null) {
            synchronized (LockFactory.class) {
                if(singletonLocks.get(name) == null) {
                    lock = new Object();
                    singletonLocks.put(name, lock);
                }
            }

        }
        return lock;
    }

    public static Object removeLock(String name) {
        return singletonLocks.remove(name);
    }
}
