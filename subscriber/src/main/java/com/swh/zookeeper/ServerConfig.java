package com.swh.zookeeper;

/**
 * 用于记录WorkServer(工作服务器)的基本配置
 */
public class ServerConfig {
    private String dbUrl;
    private String dbUser;
    private String dbPass;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }
}
