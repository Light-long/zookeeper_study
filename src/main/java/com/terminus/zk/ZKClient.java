package com.terminus.zk;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ZKClient {

    // 连接ip：port
    private String connectString = "127.0.0.1:2181";
    // 超时时间
    private int sessionTimeout = 5000;
    private ZooKeeper zkClient;


    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {

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
}
