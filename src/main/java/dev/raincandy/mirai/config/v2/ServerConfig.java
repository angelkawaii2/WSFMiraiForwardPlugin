package dev.raincandy.mirai.config.v2;

import java.util.List;

public class ServerConfig {

    private Integer version;
    private Boolean enable;
    private List<Long> globalAdminQQ;
    private List<ServersDTO> servers;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean isAdminQQ(long qq) {
        return getGlobalAdminQQ().contains(qq);
    }

    public List<Long> getGlobalAdminQQ() {
        return globalAdminQQ;
    }

    public void setGlobalAdminQQ(List<Long> globalAdminQQ) {
        this.globalAdminQQ = globalAdminQQ;
    }

    public List<ServersDTO> getServers() {
        return servers;
    }

    public void setServers(List<ServersDTO> servers) {
        this.servers = servers;
    }

    public static class ServersDTO {
        private Boolean enable;
        private String connectionId;
        private String comments;
        private String url;
        private String token;
        private String clientId;

        public Boolean isEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getConnectionId() {
            return connectionId;
        }

        public void setConnectionId(String connectionId) {
            this.connectionId = connectionId;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }
}
