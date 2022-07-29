package dev.raincandy.mirai.config.v2;

import dev.raincandy.mirai.config.v2.dto.target.ChatTargetsDTO;

import java.util.List;

public class ChatConfig {

    private Integer version;
    private Boolean enableChatForward;
    private List<ChatTargetsDTO> targets;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getEnableChatForward() {
        return enableChatForward;
    }

    public void setEnableChatForward(Boolean enableChatForward) {
        this.enableChatForward = enableChatForward;
    }

    public List<ChatTargetsDTO> getTargets() {
        return targets;
    }

    public void setTargets(List<ChatTargetsDTO> targets) {
        this.targets = targets;
    }

}
