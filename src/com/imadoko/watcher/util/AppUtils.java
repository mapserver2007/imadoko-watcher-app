package com.imadoko.watcher.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Hex;

import android.util.Log;

public class AppUtils {
    /**
     * 個体識別番号＋Saltから認証キーを返却する
     * @return 認証キー
     */
    public static String generateAuthKey(String imei, String salt) {
        MessageDigest md;
        String authKey = "";
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest((imei + salt).getBytes());
            authKey = String.valueOf(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            Log.e(AppConstants.TAG_APPLICATION, e.getMessage());
        }

        return authKey;
    }

    public static TrustManager[] getTrustAllCerts() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        }};
    }
}
