package com.ddkj.chunxiao.mi2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DiscoveryActivity extends AppCompatActivity {

    private int             cnt = 0;
    private boolean        find = false;
    //private String bcast;

    final private String TAG    = "DDKJ_DIS";
    final String action = "com.ddkj.chunxiao.mi2.discovery";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    final IntentFilter intentFilter = new IntentFilter("com.ddkj.chunxiao.sense");

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x1) {
                find = true;
            }else if(msg.what == 0x2){
                /* For TEST */
                //editor.putString("ip_addr", "192.168.1.108");
                //editor.putString("port", "9999");
                //editor.commit();

                Intent i = new Intent(DiscoveryActivity.this, MainActivity.class);
                DiscoveryActivity.this.startActivity(i);
                DiscoveryActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        registerReceiver(br, intentFilter);

        preferences = getSharedPreferences("ddkj", MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Discovery Activity onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Discovery Activity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Discovery Activity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Discovery Activity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Discovery Activity onDestroy");

        unregisterReceiver(br);
    }

    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "Discovery Activity onStart");

        final TextView display = (TextView)findViewById(R.id.discovery);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(!find){
                    if (cnt == 0) {
                        display.setText("发现 .");
                    } else if (cnt == 1) {
                        display.setText("发现 ..");
                    } else if (cnt == 2) {
                        display.setText("发现 ...");
                    }
                    cnt = (++cnt) % 3;
                    handler.postDelayed(this, 1000);
                }else{
                    display.setText("未发现");
                }
            }
        };


        Intent i = new Intent(this, DDIntentService.class);
        i.setAction(action);
        i.putExtra("bcast", getBroadcastIPAddress());

        startService(i);

        handler.postDelayed(runnable, 1000);

        Log.d("XXX", "DISCOVERY START");

    }

    public String getLocalIPAddress()
    {
        /*try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                    en.hasMoreElements();){
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress()){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch(SocketException e){
            Log.e(TAG, e.getMessage());
        }*/

        WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
        if(!wifi.isWifiEnabled()){
            wifi.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        Log.d(TAG, "ip is: " + ip);

        return IpInt2String(ip);
    }

    private String IpInt2String(int ip){
        return (ip & 0xFF) + "." + ((ip >> 8 ) & 0xFF) + "." + ((ip >> 16 ) & 0xFF) +"."+ ((ip >> 24 ) & 0xFF );
    }

    /*public String getLocalMacAddress()
    {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }*/

    public String getBroadcastIPAddress()
    {
        WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
        DhcpInfo info = wifi.getDhcpInfo();
        if(info == null){
            Log.e(TAG, "GetDhcpINfo Error");
            return null;
        }

        Log.d(TAG, "info.ipAddress: " + IpInt2String(info.ipAddress));
        Log.d(TAG, "info.netmask: " + IpInt2String(info.netmask));

        int broadcast = (info.ipAddress & info.netmask) | ~info.netmask;

        return IpInt2String(broadcast);

    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BROADCAST RECEIVE: " + intent.getStringExtra("state"));

            String state = intent.getStringExtra("state");
            if(state.equals("kiss")){
                handler.sendEmptyMessage(0x2);
            }else if(state.equals("miss")){
                handler.sendEmptyMessage(0x1);
            }

        }
    };

}
