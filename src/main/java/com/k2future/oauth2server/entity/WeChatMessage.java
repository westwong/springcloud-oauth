package com.k2future.oauth2server.entity;

import lombok.Data;

/**
 * @author West
 * @date create in 2020/1/9
 */
@Data
public class WeChatMessage {

    private Integer errcode;

    private String errmsg;

    private String openId;

    private String session_key;

    private String unionid;

    /**
     * 是否成功 吐槽一下腾讯文档，文档上说成功要返回 errcode为0
     * 然而并没有
     */
    public boolean success() {
        return openId != null && openId.trim() != "";
    }
}
