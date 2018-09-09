package com.swh.zookeeper.publish_subscribe_v1.subscribe;

/**
 *   zookeeper 基础信息
 */
public class ZKConstants {
    public static String zkAddress = "192.168.43.91:2181,192.168.43.174:2181,192.168.43.104:2181"; //zk 节点地址列表
    public static int sessionTimeout = 5000; //连接超时时间


    public static String parentPath = "/parent"; // 父节点的路径
    public static String configPath = "/config"; // 配置节点的路径


}
