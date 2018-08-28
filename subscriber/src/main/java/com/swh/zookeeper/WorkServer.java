package com.swh.zookeeper;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

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

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        };
    }
}
