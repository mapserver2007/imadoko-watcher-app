package com.imadoko.watcher.entity;

/**
 * WebSocket用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class WebSocketResponseEntity {
    /** 認証キー */
    private String _authKey;
    /** 経度 */
    private double _lng;
    /** 緯度 */
    private double _lat;

    /**
     * 認証キーを設定する
     * @param authKey 認証キー
     */
    public void setAuthKey(String authKey) {
        _authKey = authKey;
    }

    /**
     * 認証キーを返却する
     * @return 認証キー
     */
    public String getAuthKey() {
        return _authKey;
    }

    /**
     * 経度を返却する
     * @return 経度
     */
    public double getLng() {
        return _lng;
    }

    /**
     * 経度を設定する
     * @param lng 経度
     */
    public void setLng(double lng) {
        _lng = lng;
    }

    /**
     * 緯度を返却する
     * @return 緯度
     */
    public double getLat() {
        return _lat;
    }

    /**
     * 緯度を設定する
     * @param lat 緯度
     */
    public void setLat(double lat) {
        _lat = lat;
    }
}
