package com.mazatlab.domotic_app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.mazatlab.domotic_app.R;
import com.mazatlab.domotic_app.api.Client;
import com.mazatlab.domotic_app.api.Service;
import com.mazatlab.domotic_app.api.json.login.LoginPayload;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Network {
    public final static int RETRY_TIMES = 3;
    public final static int RETRY_MILLISECONDS_TIME = 10000;

    public static String getDeviceName(Context context) {
        return Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
    }

    private static String formatIntIP(int ip) throws UnknownHostException {
        byte[] byteArray = BigInteger.valueOf(ip).toByteArray();
        int len = byteArray.length;
        byte[] mappedArray = new byte[len];

        for(int i = 0; i < len; i++) {
            mappedArray[i] = byteArray[(len-1) - i];
        }

        return InetAddress.getByAddress(mappedArray).getHostAddress();
    }

    public static String getIPAddress(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            return formatIntIP(manager.getDhcpInfo().ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "127.0.0.1";
    }

    public static String getApiServerUrl(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.preferences_file), Context.MODE_PRIVATE);

        return preferences.getString("apiServerUrl", "http://raspberrypi.local:5000/");
    }

    private static String formatHostAddress(String hostAddress) {
        StringBuilder digits = new StringBuilder();
        StringBuilder result = new StringBuilder();

        hostAddress = hostAddress.replace("fe80::", "");
        hostAddress = hostAddress.replace("ff:fe", ":");
        hostAddress = hostAddress.substring(0, hostAddress.indexOf("%"));

        int index = 0;
        String[] segments = hostAddress.split(":");
        for(String segment: segments) {

            // if segment is impair
            if (segment.length() % 2 == 1) {
                boolean evaluation = true;

                if ( !Character.isDigit( segment.charAt(0) ) ) {
                    segment = "0" + segment;
                    evaluation = false;
                }

                if ( !Character.isDigit( segment.charAt(2) ) && evaluation ) {
                    StringBuilder segmentPatch = new StringBuilder(segment);
                    segment = segmentPatch.insert( (segment.length() -1), 0).toString();
                }
            }

            if(index == 0) {
                segment = segment.substring(Math.max(segment.length()-2,0));
            }

            digits.append(segment);

            index++;
        }

        for(int i = 0; i < digits.length(); i++) {
            if (i % 2 == 1 && i < digits.length() -1 ) {
                result.append(digits.charAt(i) + ":");
            } else {
                result.append(digits.charAt(i));
            }
        }

        return result.toString().toUpperCase();
    }

    public static String getHostAddress(Context context) {
        String partialMacAddress = "";
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for(NetworkInterface networkInterface : networkInterfaces) {
                if(networkInterface.getName().equalsIgnoreCase("wlan0")){
                    String hostAddress = networkInterface.getInterfaceAddresses().get(0).getAddress().getHostAddress();
                    partialMacAddress = formatHostAddress(hostAddress);

                    return partialMacAddress;
                }
            }
            return "02:00:00:00:00:00";
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "02:00:00:00:00:00";
    }

    public static void keepDeviceConnectedInHome(Context context) {
        Service updateService;
        LoginPayload loginPayload;

        String apiServerUrl = Network.getApiServerUrl(context.getApplicationContext());
        String partialMac = Network.getHostAddress(context.getApplicationContext());

        loginPayload = new LoginPayload(partialMac);
        updateService = Client.getClient(apiServerUrl).create(Service.class);

        Call<String> updateExpirationCall = updateService.putUpdateExpiration(loginPayload);
        updateExpirationCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("REQUEST", "Expiration Update");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }
}
