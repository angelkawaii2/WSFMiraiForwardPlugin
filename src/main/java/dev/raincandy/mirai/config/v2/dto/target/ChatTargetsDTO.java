package dev.raincandy.mirai.config.v2.dto.target;

import dev.raincandy.mirai.config.v2.dto.AuthenticationDTO;

import java.util.List;

public class ChatTargetsDTO {
    private Boolean enable;
    private String connectionId;
    private String comments;
    private AuthenticationDTO authentication;
    private List<String> targetClientsId;

    public Boolean getEnable() {
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

    public AuthenticationDTO getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AuthenticationDTO authentication) {
        this.authentication = authentication;
    }

    public List<String> getTargetClientsId() {
        return targetClientsId;
    }

    public void setTargetClientsId(List<String> targetClientsId) {
        this.targetClientsId = targetClientsId;
    }

}