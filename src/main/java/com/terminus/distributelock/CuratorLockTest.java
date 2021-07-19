package com.terminus.distributelock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

// 使用Curator框架创建分布式锁（可重入）
public class CuratorLockTest {

    private static final String connectString = "127.0.0.1:2181";
    private static final Integer connectTimeout = 2000;
    private static final Integer sessionTimeout = 2000;
    private static final String rootNode = "/locks";

    public static void main(String[] args) {
        // 创建分布式锁
        CuratorFramework client;
        final InterProcessMutex lock1 = new InterProcessMutex(getCuratorFramework(), rootNode);
        final InterProcessMutex lock2 = new InterProcessMutex(getCuratorFramework(), rootNode);

        new Thread(() -> {
            try {
                lock1.acquire();
                System.out.println(Thread.currentThread().getName() + "获取到锁");
                lock1.acquire();
                System.out.println(Thread.currentThread().getName() + "再次获取到锁");
                Thread.sleep(3);
                lock1.release();
                System.out.println(Thread.currentThread().getName() + "释放锁");
                lock1.release();
                System.out.println(Thread.currentThread().getName() + "再次释放锁");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                lock2.acquire();
                System.out.println(Thread.currentThread().getName() + "获取到锁");
                lock2.acquire();
                System.out.println(Thread.currentThread().getName() + "再次获取到锁");
                Thread.sleep(3);
                lock2.release();
                System.out.println(Thread.currentThread().getName() + "释放锁");
                lock2.release();
                System.out.println(Thread.currentThread().getName() + "再次释放锁");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static CuratorFramework getCuratorFramework() {
        // 重试策略(3秒一次， 总共3次)
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(3000, 3);
        // 创建curator
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(policy)
                .build();
        // 开启连接
        client.start();
        System.out.println("Zookeeper init success...");
        return client;
    }
}
