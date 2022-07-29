package dev.raincandy.mirai.config.v2;

import dev.raincandy.mirai.config.v2.dto.target.CmdTargetsDTO;

import java.util.LinkedList;
import java.util.List;

public class CommandConfig {

    private Integer version;
    private Boolean enableCommandForward;
    private List<CmdTargetsDTO> targets;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getEnableCommandForward() {
        return enableCommandForward;
    }

    public void setEnableCommandForward(Boolean enableCommandForward) {
        this.enableCommandForward = enableCommandForward;
    }

    /**
     * 返回所有target配置
     *
     * @return 所有配置，注意，所有的default配置都会重新排序到最后
     */
    public List<CmdTargetsDTO> getTargets() {

        List<CmdTargetsDTO> defaults = new LinkedList<>();
        List<CmdTargetsDTO> results = new LinkedList<>();
        for (CmdTargetsDTO t : targets) {
            if (t.isDefaultCommand()) {
                defaults.add(t);
            } else {
                results.add(t);
            }
        }
        results.addAll(defaults);
        return results;
    }

    public void setTargets(List<CmdTargetsDTO> targets) {
        this.targets = targets;
    }

}
