package com.swh.zookeeper.publish_subscribe_v1.publish;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.swh.zookeeper.publish_subscribe_v1.DBConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

/**
 *  发布者
 */
public class PublishServer {

    private static DBConfig dbConfig;

    private static CuratorFramework client;


    public static void main(String[] args) {
        //  准备工作  创建客户端连接 创建节点
        init();
        // 读取数据
        readConfig();
        // 发布数据
        publishInfo();
    }

    private static void publishInfo() {
        try{
            /*Kryo kryo = new Kryo();
            Output output = new Output(1, 1024);
            kryo.writeObject(output,dbConfig);*/
            //  把数据发布到zookeeper上
            client.setData().forPath(ZKConstants.configPath,JSON.toJSONBytes(dbConfig));

            /*output.close();*/
        }catch (Exception e){

        }
    }

    private static void readConfig() {
        BufferedReader reader = null;
        try {//   读取配置文件
            reader = new BufferedReader(new FileReader("G:\\学习\\workspace\\zookeeper\\subscriber\\src\\main\\resource\\dbpreperties.properties"));
            Properties properties = new Properties();
            properties.load(reader);
            //  把配置文件信息存储到dbConfig  类中
            dbConfig = new DBConfig(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     *   创建客户端
     */
    private static void init() {
        //  创建连接  连接客户端
        client=CuratorFrameworkFactory.builder()
                .connectString(ZKConstants.zkAddress)
                .sessionTimeoutMs(ZKConstants.sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();//连接开始
        try {//查看节点是否存在  如果不存在则创建一个节点
            //  如果父节点为空则创建一个父节点
            if (client.checkExists().forPath(ZKConstants.parentPath) == null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZKConstants.parentPath);
            }
            if(client.checkExists().forPath(ZKConstants.configPath)==null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ZKConstants.configPath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
