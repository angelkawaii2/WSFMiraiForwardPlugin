package dev.raincandy.mirai.config.v2;

import java.util.List;

public class IncomingMinecraftChatConfig {

    private Integer version;
    private List<MappingDTO> mapping;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<MappingDTO> getMapping() {
        return mapping;
    }

    public void setMapping(List<MappingDTO> mapping) {
        this.mapping = mapping;
    }

    public static class MappingDTO {
        private Boolean enable;
        private String comments;
        private Long botQQ;
        private List<String> incomingClients;
        private List<Long> targetQQGroups;

        public Long getBotQQ() {
            return botQQ;
        }

        public void setBotQQ(Long botQQ) {
            this.botQQ = botQQ;
        }

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public List<String> getIncomingClients() {
            return incomingClients;
        }

        public void setIncomingClients(List<String> incomingClients) {
            this.incomingClients = incomingClients;
        }

        public List<Long> getTargetQQGroups() {
            return targetQQGroups;
        }

        public void setTargetQQGroups(List<Long> targetQQGroups) {
            this.targetQQGroups = targetQQGroups;
        }
    }
}
