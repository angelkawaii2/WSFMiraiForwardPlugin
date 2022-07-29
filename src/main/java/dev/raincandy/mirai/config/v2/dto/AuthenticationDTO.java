package dev.raincandy.mirai.config.v2.dto;

import java.util.LinkedList;
import java.util.List;

/**
 * 权限类
 */
public class AuthenticationDTO {
    private PrivateChatDTO privateChat;
    private List<GroupsDTO> groups;

    public PrivateChatDTO getPrivateChat() {
        return privateChat;
    }

    public void setPrivateChat(PrivateChatDTO privateChat) {
        this.privateChat = privateChat;
    }

    public List<GroupsDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupsDTO> groups) {
        this.groups = groups;
    }

    public static class PrivateChatDTO {
        private String prefix;
        /**
         * 如果这个字段为空，则视为允许所有用户
         * 如果想要限制用户，请填写一个不存在的QQ
         */
        private List<Long> whitelistQQ = new LinkedList<>();

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * 检查是否在允许的qq名单中
         * 注意，如果名单为空，则视为允许所有用户！
         *
         * @param qq 要检查的qq
         * @return 如果列表为空，或者列表中存在返回true，否则返回false
         */
        public boolean hasWhitelist(long qq) {
            if (allowAllUser()) {
                return true;
            }
            return getWhitelistQQ().contains(qq);
        }

        public boolean allowAllUser() {
            return whitelistQQ.size() == 0;
        }

        public List<Long> getWhitelistQQ() {
            return whitelistQQ;
        }

        public void setWhitelistQQ(List<Long> whitelistQQ) {
            this.whitelistQQ = whitelistQQ;
        }
    }

    public static class GroupsDTO {
        private String prefix;
        private List<Long> groupsId = new LinkedList<>();
        /**
         * 如果群号为空，则视为响应所有群
         */
        private List<Long> whitelistQQ = new LinkedList<>();

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public List<Long> getGroupsId() {
            return groupsId;
        }

        public boolean isAllowAllGroups() {
            return groupsId.size() == 0;
        }

        /**
         * 判断群号是否在此配置中
         * 如果配置为空，则视为响应所有群，返回true
         *
         * @param groupId
         * @return 配置为空，或配置文件包含，返回true，否则返回false
         */
        public boolean hasGroupId(long groupId) {
            if (isAllowAllGroups()) {
                return true;
            }
            return getGroupsId().contains(groupId);
        }

        public void setGroupsId(List<Long> groupsId) {
            this.groupsId = groupsId;
        }

        /**
         * 检查是否在允许的qq名单中
         * 注意，如果名单为空，则视为允许所有用户！
         *
         * @param qq 要检查的qq
         * @return 如果列表为空，或者列表中存在返回true，否则返回false
         */
        public boolean hasWhitelist(long qq) {
            if (allowAllUser()) {
                return true;
            }
            return getWhitelistQQ().contains(qq);
        }

        public boolean allowAllUser() {
            return whitelistQQ.size() == 0;
        }

        public List<Long> getWhitelistQQ() {
            return whitelistQQ;
        }

        public void setWhitelistQQ(List<Long> whitelistQQ) {
            this.whitelistQQ = whitelistQQ;
        }
    }
}