package dev.raincandy.mirai.config.v2.dto.target;

import java.util.LinkedList;
import java.util.List;

public class CmdTargetsDTO extends ChatTargetsDTO {

    /**
     * 注意，如果这里的commands为空，那么将视为默认指令配置
     * 当其他指令不满足条件时，将会执行全部的默认指令配置
     */
    private List<String> commands = new LinkedList<>();
    private String rewrite;

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * 如果commands配置为空白，则视为这是默认的指令
     */
    public boolean isDefaultCommand() {
        return commands.size() == 0;
    }

    public String getRewrite() {
        return rewrite;
    }

    public boolean hasRewrite() {
        if (getRewrite() != null) {
            return !"".equals(getRewrite());
        }
        return false;
    }

    public void setRewrite(String rewrite) {
        this.rewrite = rewrite;
    }
}
