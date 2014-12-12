package com.imadoko.watcher.entity;

/**
 * WebSocketRequestEntity
 * @author Ryuichi Tanaka
 * @since 2014/12/10
 */
public class WebSocketRequestEntity {
    /** コネクションID */
    private String _connectionId;
    /** リクエストID */
    private String _requestId;

    /**
     * コネクションIDを設定する
     * @param authKey 認証キー
     */
    public void setConnectionId(String authKey) {
        _connectionId = authKey;
    }

    /**
     * コネクションIDを返却する
     * @return 認証キー
     */
    public String getConnectionId() {
        return _connectionId;
    }

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
