package com.terminus.dynamicregister;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DistributeClient {

    private final String connectString = "127.0.0.1:2181";
    private final int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws Exception{
        DistributeClient client = new DistributeClient();
        // 1. 创建连接
        client.getConnection();
        // 2. 获取服务器注册列表，监听节点
        client.getServerRegisterList();
        // 3. 执行业务
        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerRegisterList() throws InterruptedException, KeeperException {
        // 服务器注册列表
        List<String> list = new ArrayList<>();
        List<String> children = zk.getChildren("/servers", true);
        for (String child : children) {
            // 获取每个节点的值（服务器信息）
            byte[] data = zk.getData("/servers/" + child, false, null);
            list.add(new String(data));
        }
        // 打印服务器列表信息
        System.out.println(list);
    }

    private void getConnection() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            try {
                getServerRegisterList();
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        });
    }
}
