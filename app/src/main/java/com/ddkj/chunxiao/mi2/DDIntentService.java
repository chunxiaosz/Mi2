package com.ddkj.chunxiao.mi2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DDIntentService extends IntentService {

    private final String ACTION_DIS   = "com.ddkj.chunxiao.mi2.discovery";
    private final String ACTION_WARM  = "com.ddkj.chunxiao.mi2.heat";
    private final String ACTION_WASH  = "com.ddkj.chunxiao.mi2.wash";
    private final String ACTION_IN    = "com.ddkj.chunxiao.mi2.inject";
    private final String ACTION_WASTE = "com.ddkj.chunxiao.mi2.discharge";
    private final String ACTION_MIX   = "com.ddkj.chunxiao.mi2.mix";

    private final String TAG = "DDKJ_Service";

    //private int local_port = 8888;
    private int remote_port = 9999;
    private String data;
    private String func;

    private boolean response;
    private boolean loop;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public DDIntentService() {
        super("DDIntentService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        preferences = getSharedPreferences("ddkj", MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();
            Log.d(TAG, action);
            if (ACTION_DIS.equals(action)) {

                response = false;

                String bcast = intent.getStringExtra("bcast");
                Log.d(TAG, "bcast is: " + bcast);
                if(bcast == null){
                    Log.d(TAG, "bcast info is empty");
                }

                MulticastSocket ms = null;

                try{
                    ms = new MulticastSocket();
                }catch(IOException e){
                    Log.e(TAG, "open socket:" + e.getMessage());
                }

                String idata = "sense";

                byte[] buf = idata.getBytes();

                InetAddress des = null;

                try{
                    des = InetAddress.getByName(bcast);
                }catch(UnknownHostException e){
                    Log.e(TAG, "parse host err:" + e.getMessage());
                }

                DatagramPacket dp = new DatagramPacket(buf, buf.length, des, remote_port);
                if(dp == null){
                    Log.d(TAG, "DatagramPacket is null");
                    ms.close();
                    stopSelf();
                }
                try{
                    ms.send(dp);
                }catch(IOException e){
                    Log.e(TAG, e.getMessage());
                }


                byte[] recvBuf = new byte[128];
                DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

                try {
                    ms.setSoTimeout(5000);
                    ms.receive(recvPacket);
                    response = true;
                } catch (IOException e) {
                    Log.e(TAG, "Time out");
                    Log.e(TAG, e.toString());
                }

                Intent i = new Intent("com.ddkj.chunxiao.sense");
                if(response){
                    String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                    Log.d(TAG, recvStr);

                    String ip = recvPacket.getAddress().getHostAddress();
                    String port = String.valueOf(recvPacket.getPort());

                    editor.putString("ip_addr", ip);
                    editor.putString("port", port);
                    editor.commit();

                    i.putExtra("state", "kiss");

                }else{

                    i.putExtra("state", "miss");

                }

                sendBroadcast(i);

                ms.close();

            }else{

                loop = true;

                String ip_addr = intent.getStringExtra("ip_addr");
                String port = intent.getStringExtra("port");
                String press = intent.getStringExtra("press");
                String temp = intent.getStringExtra("temp");
                String state = intent.getStringExtra("state");

                Log.d(TAG, "ip_addr: " + ip_addr + "; port: " + port + "; press: " + press + "; temp: " + temp + "; state: " + state);

                func = actionToFunc(action);

                //Log.d(TAG, "func: " + func);

                DatagramSocket ds = null;

                try{
                    ds = new DatagramSocket();
                }catch(SocketException e){
                    Log.e(TAG, "open socket:" + e.getMessage());
                }

                data = "func=" + func + "&state=" + state + "&press=" + press + "&temp=" + temp;
                Log.d(TAG, "send data: " + data);

                byte[] buf = data.getBytes();

                InetAddress des = null;

                try{
                    des = InetAddress.getByName(ip_addr);
                }catch(UnknownHostException e){
                    Log.e(TAG, "parse host err:" + e.getMessage());
                }

                DatagramPacket dp = new DatagramPacket(buf, buf.length, des, Integer.parseInt(port));

                byte[] recvBuf = new byte[128];
                DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

                while (loop){
                    try{
                        ds.send(dp);
                    }catch(IOException e){
                        Log.e(TAG, "send data err");
                    }

                    try {
                        ds.setSoTimeout(2000);
                        ds.receive(recvPacket);
                        loop = false;
                    } catch (IOException e) {
                        System.out.println("recv data err");
                    }
                }

                if(loop == false){
                    String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                    Log.d(TAG, "Receive: " + recvStr);
                }

                ds.close();
            }
        }
    }

    public String actionToFunc(String act){
        if(act.equals(ACTION_DIS)){
            return "sense";
        }else if(act.equals(ACTION_IN)){
            return "inject";
        }else if(act.equals(ACTION_MIX)){
            return "mix";
        }else if(act.equals(ACTION_WARM)){
            return "heat";
        }else if(act.equals(ACTION_WASH)){
            return "wash";
        }else if(act.equals(ACTION_WASTE)){
            return "discharge";
        }

        return null;
    }

}
