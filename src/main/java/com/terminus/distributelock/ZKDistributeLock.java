package com.terminus.distributelock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKDistributeLock {
    // 连接参数
    private final String connectString = "127.0.0.1:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zk;
    // 节点
    private final String rootNode = "/locks";
    private final String subNode = "/seq-";
    // 当前client等待的子节点
    private String waitPath;

    // Zookeeper连接等待(当前线程执行前必须有一个线程先执行，倒数)
    private CountDownLatch connectDownLatch = new CountDownLatch(1);
    // Zookeeper节点等待
    private CountDownLatch waitLatch = new CountDownLatch(1);

    // 当前 client 创建的子节点
    private String currentNode;

    public ZKDistributeLock() throws Exception {
        // 创建连接
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            // 连接建立时, 打开 latch, 唤醒 wait 在该 latch 上的线程
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectDownLatch.countDown();
            }
            // 发生了 waitPath 的删除事件
            if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted
                    && watchedEvent.getPath().equals(waitPath)) {
                waitLatch.countDown();
            }
        });
        // 等待连接建立
        connectDownLatch.await();
        // 获取根节点状态，如果根节点不存在，创建根节点(永久节点)
        Stat stat = zk.exists(rootNode, null);
        if (stat == null) {
            zk.create(rootNode, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    // 加锁
    public void zkLock() {
        try {
            // 在根节点下创建临时顺序节点，返回值为创建的节点路径
            currentNode = zk.create(rootNode + subNode, null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            // wait一下
            Thread.sleep(10);
            // 获取/locks下所有的节点
            // 注意, 没有必要监听"/locks"的子节点的变化情况
            List<String> children = zk.getChildren(rootNode, false);
            // 列表中只有一个子节点, 那肯定就是 currentNode , 说明client 获得锁
            if (children.size() == 1) {
                return;
            } else {
                // 对父节点下所有的临时节点排序
                Collections.sort(children);
                // 当前节点名称 seq000000
                String thisNode = currentNode.substring((rootNode+"/").length());
                // 获取当前节点的位置
                int index = children.indexOf(thisNode);
                if (index == -1) {
                    System.out.println("数据异常");
                } else if (index == 0) {
                    return;
                } else {
                    // 获得排名比 currentNode 前 1 位的节点
                    waitPath = rootNode + "/" + children.get(index - 1);
                    // 在 waitPath 上注册监听器, 当 waitPath 被删除时,
                    // zookeeper 会回调监听器的 process 方法
                    zk.getData(waitPath, true, null);
                    // 进入等待锁状态
                    waitLatch.await();

                    return;
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // 解锁
    public void unZkLock() throws InterruptedException, KeeperException {
        zk.delete(this.currentNode, -1);
    }
}
