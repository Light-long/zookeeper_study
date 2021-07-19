package com.terminus.distributelock;

import org.apache.zookeeper.KeeperException;

public class DistributeLockTest {
    public static void main(String[] args) throws Exception {
        final ZKDistributeLock lock1 = new ZKDistributeLock();
        final ZKDistributeLock lock2 = new ZKDistributeLock();

        new Thread(() -> {
            try {
                lock1.zkLock();
                System.out.println(Thread.currentThread().getName() + "获取锁");
                Thread.sleep(5000);
                lock1.unZkLock();
                System.out.println(Thread.currentThread().getName() + "释放锁");
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                lock2.zkLock();
                System.out.println(Thread.currentThread().getName() + "获取锁");
                Thread.sleep(5000);
                lock2.unZkLock();
                System.out.println(Thread.currentThread().getName() + "释放锁");
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
