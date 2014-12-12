package com.imadoko.watcher.util;

/**
 * アプリケーション定数クラス
 * @author Ryuichi Tanaka
 * @since 2014/12/10
 */
public class AppConstants {
    // 共通系
    public static final String APPLICATION_TYPE = "2";
    public static final String REQUEST_WATCHER_TO_MAIN = "1";

    /** WebSocket/REST系 */
//    public static final String WEBSOCKET_SERVER_URI  = "ws://imadoko-node-server.herokuapp.com";
//    public static final String AUTHSALT_URL          = "https://imadoko-node-server.herokuapp.com/salt";
//    public static final String AUTH_URL              = "https://imadoko-node-server.herokuapp.com/auth";
//    public static final String LOCATION_URL          = "https://imadoko-node-server.herokuapp.com/location";
    public static final String WEBSOCKET_SERVER_URI  = "ws://192.168.0.30:9224";
    public static final String AUTHSALT_URL          = "http://192.168.0.30:9224/salt";
    public static final String AUTH_URL              = "http://192.168.0.30:9224/auth";
    public static final String LOCATION_URL          = "http://192.168.0.30:9224/location";
    public static final String WEBSOCKET_AUTHKEY_HEADER = "X-Imadoko-AuthKey";
    public static final String WEBSOCKET_APPLICATION_TYPE_HEADER = "X-Imadoko-ApplicationType";
    public static final int CLOSE_CODE = 1002;
    public static final int WS_TIMER_INTERVAL = 30000;
    public static final int PING_TIMER_INTERVAL = 20000;
    public static final int FAST_RECCONECT_MAX_NUM = 10;
    public static final int RECOONECT_FAST_INTRERVAL = 5000;
    public static final int RECONNECT_INTERVAL = 100000;

    /** 地図系 */
    public static final double DEFAULT_LONGITUDE = 139.688845;
    public static final double DEFAULT_LATITUDE = 35.755072;
    public static final long REALTIME_DRAW_INTERVAL = 20000; // 20秒

    /** 位置情報ログ系 */
    public static final String SHARED_PREFERENCES_KEY = "PositionLogPref";
    public static final String PREF_POSITION_LOG_KEY = "PositionLogKey";
    public static final int POSITION_LOG_MAX_SIZE = 10;

    /** LogID */
    public static final String TAG_APPLICATION = "Application";
    public static final String TAG_ASYNCTASK = "AsyncTask";
    public static final String TAG_HTTP = "Http";
    public static final String TAG_WEBSOCKET = "WebSocket";
    public static final String TAG_SERVICE = "Service";
    public static final String TAG_LOCATION = "Location";

    /** ParameterKey */
    public static final String PARAM_AUTH_KEY = "authKey";
    public static final String PARAM_SALT_NAME = "name";
    public static final String PARAM_LOCATION_USERNAME = "userName";
    public static final String PARAM_DIALOG_MESSAGE = "dialogMessage";

    /** DialogID */
    public static final String DIALOG_ALERT = "DialogAlert";
    public static final String DIALOG_AUTH_ERROR = "DialogAuthError";
}
