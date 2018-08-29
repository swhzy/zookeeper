package com.swh.zookeeper;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import netscape.javascript.JSObject;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

/**
 * 工作服务器
 */
public class WorkServer {
    private String serversPath;
    private String configPath;
    private ZkClient zkClient;
    private ServerConfig serverConfig;
    private ServerData serverData;

    private IZkDataListener zkDataListener; //数据监听器

    public WorkServer(String serversPath, String configPath, ZkClient zkClient, ServerConfig serverConfig, ServerData serverData) {
        this.serversPath = serversPath;
        this.configPath = configPath;
        this.zkClient = zkClient;
        this.serverConfig = serverConfig;
        this.serverData = serverData;

        /**
         * dataListener  用于监听config节点的配置变化
         */
        this.zkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                String retJson = new String((byte[]) o);
                ServerConfig serverConfig = JSONObject.parseObject(retJson, ServerConfig.class);
                updateServiceConfig(serverConfig);
                System.out.println("change work server config is :"+ JSON.toJSONString(serverConfig));
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }

        };

    }

    /**
     * 服务启动
     */
    public void start(){
        System.out.println("work server start ...");
        initRunning();
    }

    /**
     * 服务停止
     */
    public void stop(){
        System.out.println("work server stop ...");
        zkClient.unsubscribeDataChanges(configPath,zkDataListener);
    }

    /**
     * 服务器的初始化
     */
    private void initRunning() {
         registMeToZookeeper();
    }

    /**
     * 启动zookeeper时把自己注册到zookeeper里
     */
    private void registMeToZookeeper() {
        // 向zookeeper中注册自己的过程其实就是向servers节点下注册一个临时节点
        //构建临时节点
        String concat = serversPath.concat("/").concat(serverData.getAddress());
        try {
            //存入json序列化
            zkClient.createEphemeral(concat,JSON.toJSONString(serverData).getBytes());
        }catch (ZkNoNodeException e){
            //父节点不存在
            zkClient.createPersistent(serversPath,true);
            registMeToZookeeper();
        }

    }


    void  updateServiceConfig(ServerConfig serverConfig){
        this.serverConfig = serverConfig;
    }



}
