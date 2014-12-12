package com.imadoko.watcher.activity;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import com.imadoko.watcher.R;
import com.imadoko.watcher.entity.AuthSaltEntity;
import com.imadoko.watcher.entity.HttpRequestEntity;
import com.imadoko.watcher.entity.HttpResponseEntity;
import com.imadoko.watcher.network.AsyncHttpTaskLoader;
import com.imadoko.watcher.util.AppConstants;
import com.imadoko.watcher.util.AppUtils;


public class SplashActivity extends FragmentActivity {

    private String _authKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        executeAuth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(AppConstants.PARAM_AUTH_KEY, _authKey);
        startActivity(intent);
        finish();
    }

    private void onAuthResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            startMainActivity();
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        AuthErrorDialogFragment dialog = new AuthErrorDialogFragment();
        dialog.show(getSupportFragmentManager(), AppConstants.DIALOG_AUTH_ERROR);
    }

    private void executeAuth() {
        HttpRequestEntity entity1 = new HttpRequestEntity();
        entity1.setUrl(AppConstants.AUTHSALT_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_SALT_NAME, "main");
        entity1.setParams(params);

        AsyncHttpTaskLoader loader1 = new AsyncHttpTaskLoader(this, entity1) {
            @Override
            public void deliverResult(HttpResponseEntity response) {
                if (response.getStatusCode() == HttpStatus.SC_OK) {
                    AuthSaltEntity authSaltEntity = JSON.decode(response.getResponseBody(), AuthSaltEntity.class);
                    String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    _authKey = AppUtils.generateAuthKey(udid + AppConstants.APPLICATION_TYPE, authSaltEntity.getSalt());

                    HttpRequestEntity entity2 = new HttpRequestEntity();
                    entity2.setUrl(AppConstants.AUTH_URL);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
                    entity2.setParams(params);

                    AsyncHttpTaskLoader loader2 = new AsyncHttpTaskLoader(SplashActivity.this, entity2) {
                        @Override
                        public void deliverResult(HttpResponseEntity response) {
                            onAuthResult(response.getStatusCode());
                        }
                    };
                    loader2.post();
                } else {
                    onAuthResult(response.getStatusCode());
                }
            }
        };
        loader1.get();
    }
}
