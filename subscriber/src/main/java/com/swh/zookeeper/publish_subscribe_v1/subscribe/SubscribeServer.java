package com.swh.zookeeper.publish_subscribe_v1.subscribe;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import com.swh.zookeeper.publish_subscribe_v1.DBConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SubscribeServer {

    private static DBConfig dbConfig;
    private static CuratorFramework client;
    private static NodeCache nodeCache;

    public static void main(String[] args) throws InterruptedException {
        //创建连接
        init();
        // 监听zookeeper中的数据变化
        subscribeInfo();
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static void subscribeInfo() {
        //  创建node节点
        NodeCache nodeCache = new NodeCache(client, ZKConstants.configPath);
        try{
            nodeCache.start(true);
            if(nodeCache.getCurrentData()!=null) {
                if (!(nodeCache.getCurrentData().getData()).equals("")) {
                    unSerialize();
                } else {
                    readConfig();
                }
            }else {
                readConfig();
            }
            //  设置监听  当zookeeper中的数据变化的时候会调用该实现
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    System.out.println("数据库节点信息发生变化，读取新的数据库信息！");
                    unSerialize();//反序列化得到信息
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void readConfig() {

        BufferedReader reader = null;//加载文件流
        System.out.println("读取本地数据库信息。。。。。。");
        try {
            reader = new BufferedReader(new FileReader("G:\\学习\\workspace\\zookeeper\\subscriber\\src\\main\\resource\\dbpreperties.properties"));
            Properties prop = new Properties();//创建属性操作对象
            prop.load(reader);//加载流
            dbConfig = new DBConfig(prop.getProperty("url"),prop.getProperty("username"),prop.getProperty("password"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("本地数据库配置信息为：" + dbConfig.toString());
    }

    private static void unSerialize() throws Exception {
        System.out.println("读取zookeeper服务器数据库信息.............");
        byte[] data = new byte[0];
        data = client.getData().forPath(ZKConstants.configPath);
        /*Kryo kryo = new Kryo();
        Input input = new Input(data);
        DBConfig dbConfig = kryo.readObject(input, DBConfig.class);
        input.close();*/
        System.out.println("从zookeeper读出的数据库信息:"+JSON.parseObject(data,DBConfig.class).toString());
    }

    private static void init() {
         client = CuratorFrameworkFactory.builder()
                .connectString(ZKConstants.zkAddress)
                .sessionTimeoutMs(ZKConstants.sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
         client.start();
    }






}
