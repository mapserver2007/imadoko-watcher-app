package com.imadoko.watcher.entity;

public class LocationEntity {
    /** 経度 */
    private double _lng;
    /** 緯度 */
    private double _lat;
    /** 更新日時 */
    private String _dateTime;

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

    /**
     * 更新日時を返却する
     * @return 更新日時
     */
    public String getDateTime() {
        return _dateTime;
    }

    /**
     * 更新日時を設定する
     * @param dateTime
     */
    public void setDateTime(String dateTime) {
        _dateTime = dateTime;
    }
}
