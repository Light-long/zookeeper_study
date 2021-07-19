package com.terminus.dynamicregister;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class DistributeServer {

    private final String connectString = "127.0.0.1:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws Exception {
        DistributeServer server  = new DistributeServer();
        // 1. 连接Zookeeper
        server.getConnection();
        // 2. 注册节点(需要传一个参数，节点路径名称)
        server.registerNode(args[0]);
        // 2. 执行服务端业务逻辑（sleep）
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 注册节点
     * @param hostName 节点名（节点是：暂时的，带序号的）
     */
    private void registerNode(String hostName) throws InterruptedException, KeeperException {
        String createdNode = zk.create("/servers/" + hostName, hostName.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(createdNode + "节点已上线");
    }

    private void getConnection() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {

        });
    }
}
