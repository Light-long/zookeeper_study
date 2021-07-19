package com.terminus.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ZKClient {

    // 连接ip：port
    private final String connectString = "127.0.0.1:2181";
    // 超时时间
    private final int sessionTimeout = 5000;
    private ZooKeeper zkClient;


    // 初始化Zookeeper客户端
    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
//            // 收到事件通知后的回调函数
//            System.out.println(watchedEvent.getType() + "--" + watchedEvent.getPath());
//            // 因为一次注册只能一次监听，再次注册
//            try {
//                List<String> children = zkClient.getChildren("/", true);
//                for (String child : children) {
//                    System.out.println(child);
//                }
//            } catch (KeeperException | InterruptedException e) {
//                e.printStackTrace();
//            }
        });
    }

    @Test
    public void createTest() throws InterruptedException, KeeperException {
        /**
         * 1. path创建的节点的路径
         * 2. 节点数据
         * 3. 节点权限
         * 4. 节点类型（永久/暂时、有序号/没序号）
         */
        zkClient.create("/terminus", "lishilong".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 监听路径变化
     */
    @Test
    public void getChildrenTest() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
    }

    /**
     * 判断节点是否存在
     */
    @Test
    public void existsNode() throws InterruptedException, KeeperException {
        // 不开启监听
        Stat stat = zkClient.exists("/terminus", false);
        System.out.println(stat != null ? "exists" : "not exists");
    }

}
