package com.imadoko.watcher.entity;

/**
 * WebSocketRequestEntity
 * @author Ryuichi Tanaka
 * @since 2014/12/10
 */
public class WebSocketRequestEntity {
    /** リクエストID */
    private String _requestId;

    /**
     * リクエストIDを設定する
     * @param requestId リクエストID
     */
    public void setRequestId(String requestId) {
        _requestId = requestId;
    }

    /**
     * リクエストIDを返却する
     * @return リクエストID
     */
    public String getRequestId() {
        return _requestId;
    }
}
