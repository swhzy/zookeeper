package com.swh.zookeeper;

import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;

public class SubscribeZkClient {
    //需要多少个workserver
    private static final int CLIENT_QTY = 5;
    private static final String ZOOKEEPER_SERVER = "";
    private static final String CONFIG_PATH = "/config";
    private static final String COMMAND_PATH = "/command";
    private static final String SERVERS_PATH = "/servers";

    public static void main(String[] args) {
        List<ZkClient> zkClients = new ArrayList<>();
        List<WorkServer> workServers = new ArrayList<>();

        ServerConfig initConfig = new ServerConfig();
        initConfig.setDbPass("");
        initConfig.setDbUrl("");
        initConfig.setDbUser("");


    }

}
