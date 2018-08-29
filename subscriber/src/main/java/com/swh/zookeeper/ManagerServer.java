package com.swh.zookeeper;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;

public class ManagerServer {
    private String serversPath;
    private String commandPath;
    private String configPath;
    private ZkClient zkClient;
    private ServerConfig serverConfig;
    //用于监听server下子节点的变化
    private IZkChildListener zkChildListener;
    //用于监听zookeeper中command节点的数据变化
    private IZkDataListener dataListener;
    // 工作服务器的列表
    private List<String> workServerList;

    public ManagerServer(String serversPath, String commandPath, String configPath, ZkClient zkClient, ServerConfig serverConfig) {
        this.serversPath = serversPath;
        this.commandPath = commandPath;
        this.configPath = configPath;
        this.zkClient = zkClient;
        this.serverConfig = serverConfig;
        //监听zookeeper中server下的子节点信息
        this.zkChildListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                workServerList = list;
            }
        };
        //监控zookeeper中command节点中的变化
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                String com = new String((byte[]) o);
                System.out.println(com);
                exeCommand(com);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        };
    }

    public void start(){
        initRunning();
    }

    public void stop(){
        zkClient.unsubscribeChildChanges(serversPath,zkChildListener);
        zkClient.unsubscribeDataChanges(commandPath,dataListener);
    }

    private void initRunning() {
        //执行订阅command节点数据变化和servers节点列表的变化
        zkClient.subscribeDataChanges(commandPath,dataListener);
        zkClient.subscribeChildChanges(serversPath,zkChildListener);
    }


    private void exeCommand(String com) {
        if("list".equals(com)){
            exeList();
        }else if ("create".equals(com)){
            exeCreate();
        }else if("modify".equals(com)){
            exeModify();
        }else {
            System.out.println("error commamd!"+com);
        }
    }

    private void exeModify() {
        serverConfig.setDbUser(serverConfig.getDbUser() + "_modify");
        try {
            zkClient.writeData(configPath, JSON.toJSONString(serverConfig).getBytes());
        } catch (ZkNoNodeException e) {
            exeCreate();
        }
    }


    private void exeCreate() {
        if(!zkClient.exists(configPath)){
            try {
                zkClient.createPersistent(configPath, JSON.toJSONString(serverConfig).getBytes());
            }catch (ZkNodeExistsException e){
                // 节点已经存在，直接写入数据
                zkClient.writeData(configPath,JSON.toJSONString(serverConfig).getBytes());
            }catch (ZkNoNodeException e){
                // 父节点不存在
                String parentDir = configPath.substring(0, configPath.lastIndexOf('/'));
                zkClient.createPersistent(parentDir,true);
                exeCreate();
            }
        }
    }


    private void exeList() {
        System.out.println(workServerList.toString());
    }

}
