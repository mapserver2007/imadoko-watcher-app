package com.imadoko.watcher.activity;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapActivity;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.PinOverlay;
import net.arnx.jsonic.JSON;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocket.READYSTATE;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;

import com.imadoko.watcher.R;
import com.imadoko.watcher.entity.LocationEntity;
import com.imadoko.watcher.entity.WebSocketRequestEntity;
import com.imadoko.watcher.entity.WebSocketResponseEntity;
import com.imadoko.watcher.util.AppConstants;
import com.imadoko.watcher.util.AppKeys;

public class MainActivity extends MapActivity {
    private WebSocketClient _ws;
    private String _authKey;
    private MapView _mapView;
    private Timer _timer;
    private Timer _heartbeatTimer;
    private SharedPreferences _pref;
    private LinkedList<Long> _heartbeatPool;
    private int _recconectCount;

    private ActionBarDrawerToggle _drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        _authKey = intent.getStringExtra(AppConstants.PARAM_AUTH_KEY);
        _heartbeatPool = new LinkedList<Long>();

        if (_pref == null) {
            _pref = getSharedPreferences(AppConstants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }

        _mapView = new MapView(this, AppKeys.APP_KEY);
        MapController c = _mapView.getMapController();
        LocationEntity defaultLocation = getPositionLog();
        int x = (int) (defaultLocation.getLng() * 1E6);
        int y = (int) (defaultLocation.getLat() * 1E6);
        c.setCenter(new GeoPoint(y, x));
        c.setZoom(3);
        setContentView(_mapView);

        // とりあえず
        String connectionId = "a79ae076bc8cbaefdbf36e9562ac58f8bc352921";
        connect(connectionId);
    }

    @Override
    public void onStart() {
        super.onStart();

//        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        _drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name, R.string.app_name);
//        _drawerToggle.setDrawerIndicatorEnabled(true);
//        drawerLayout.setDrawerListener(_drawerToggle);

    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (_ws != null && _ws.getReadyState() == READYSTATE.OPEN) {
            _ws.getConnection().close(AppConstants.CLOSE_CODE);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        stopTimer();
        if (_ws != null && _ws.getReadyState() == READYSTATE.OPEN) {
            _ws.getConnection().close(AppConstants.CLOSE_CODE);
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        String connectionId = "a79ae076bc8cbaefdbf36e9562ac58f8bc352921";
        connect(connectionId);
    }

    @Override
    protected boolean isRouteDisplayed() {
       return false;
    }

    private void stopTimer() {
        if (_timer != null) {
            _timer.cancel();
            _timer.purge();
        }
        if (_heartbeatTimer != null) {
            _heartbeatTimer.cancel();
            _heartbeatTimer.purge();
        }
    }

    private void connect(String connectionId) {
        final WebSocketRequestEntity requestEntity = new WebSocketRequestEntity();
        requestEntity.setConnectionId(connectionId);
        requestEntity.setRequestId(AppConstants.REQUEST_WATCHER_TO_MAIN);

        if (_ws == null || _ws.getReadyState() != READYSTATE.OPEN) {
            createWebSocketConnection();
        }

        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (_ws != null && _ws.getReadyState() == READYSTATE.OPEN) {
                    Log.d(AppConstants.TAG_APPLICATION, "send request");
                    _ws.send(JSON.encode(requestEntity));
                }
            }
        }, 1000, AppConstants.REALTIME_DRAW_INTERVAL);
    }

    private void createWebSocketConnection() {
        final Handler handler = new Handler();
        if (_recconectCount > AppConstants.FAST_RECCONECT_MAX_NUM) {
            _recconectCount = AppConstants.FAST_RECCONECT_MAX_NUM + 1;
        } else {
            _recconectCount++;
        }

        URI uri;
        try {
            uri = new URI(AppConstants.WEBSOCKET_SERVER_URI);
        } catch (Throwable e) {
            showDialog("接続エラーが発生しました。");
            return;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AppConstants.WEBSOCKET_AUTHKEY_HEADER, _authKey);
        headers.put(AppConstants.WEBSOCKET_APPLICATION_TYPE_HEADER, AppConstants.APPLICATION_TYPE);

        _ws = new WebSocketClient(uri, new Draft_17(), headers, 3000) {
            @Override
            public void onOpen(ServerHandshake handShake) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onOpen");
                _recconectCount = 0;
                // HeartBaat処理
                _heartbeatTimer = new Timer();
                _heartbeatTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (_ws == null || _ws.getReadyState() != READYSTATE.OPEN) {
                            _heartbeatTimer.cancel();
                            _heartbeatTimer.purge();
                            return;
                        }

                        if (_heartbeatPool.size() > 2) {
                            getConnection().close(AppConstants.CLOSE_CODE);
                            return;
                        }
                        _heartbeatPool.add(System.currentTimeMillis());
                        FramedataImpl1 frame = new FramedataImpl1(Opcode.PING);
                        frame.setFin(true);
                        _ws.getConnection().sendFrame(frame);
                    }
                }, AppConstants.PING_TIMER_INTERVAL, AppConstants.PING_TIMER_INTERVAL);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onClose");
                _heartbeatTimer.cancel();
                _heartbeatTimer.purge();

                if (code != AppConstants.CLOSE_CODE) {
                    _heartbeatPool = new LinkedList<Long>();
                    handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                createWebSocketConnection();
                            }
                        },
                        _recconectCount > AppConstants.FAST_RECCONECT_MAX_NUM ? AppConstants.RECONNECT_INTERVAL
                                : AppConstants.RECOONECT_FAST_INTRERVAL);
                }
            }

            @Override
            public void onError(Exception e) {
                getConnection().close(AppConstants.CLOSE_CODE);
            }

            @Override
            public void onMessage(final String jsonStr) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onMessage");
                final WebSocketResponseEntity entity = JSON.decode(jsonStr, WebSocketResponseEntity.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawPosition(entity);
                    }
                });
            }

            @Override
            public void onWebsocketPing(WebSocket conn, Framedata f) {
                _heartbeatPool.remove();
            }
        };

        _ws.connect();
    }

    private LocationEntity getPositionLog() {
        String json = _pref.getString(AppConstants.PREF_POSITION_LOG_KEY, "");
        LocationEntity location;
        if (json == "") {
            location = new LocationEntity();
            location.setLat(AppConstants.DEFAULT_LATITUDE);
            location.setLng(AppConstants.DEFAULT_LONGITUDE);
        } else {
            location = JSON.decode(json, LocationEntity.class);
        }

        return location;
    }

    private void writePositionLog(WebSocketResponseEntity location) {
        Editor editor = _pref.edit();
        editor.remove(AppConstants.PREF_POSITION_LOG_KEY).apply();
        editor.putString(AppConstants.PREF_POSITION_LOG_KEY, JSON.encode(location)).apply();
    }

    private void drawPosition(WebSocketResponseEntity location) {
        int x = (int) (location.getLng() * 1E6);
        int y = (int) (location.getLat() * 1E6);
        GeoPoint geo = new GeoPoint(y, x);
        _mapView.getMapController().animateTo(geo);
        _mapView.invalidate();

        PinOverlay pinOverlay = new PinOverlay(getResources().getDrawable(R.drawable.ic_current));
        pinOverlay.addPoint(geo, String.valueOf(location.getLng()) + "," + String.valueOf(location.getLat()));
        _mapView.getOverlays().clear();
        _mapView.getOverlays().add(pinOverlay);

        writePositionLog(location);
    }

    private void showDialog(String message) {
        Intent dialogIntent = new Intent(this, AlertDialogActivity.class);
        dialogIntent.putExtra(AppConstants.PARAM_DIALOG_MESSAGE, message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, dialogIntent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException ignore) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
