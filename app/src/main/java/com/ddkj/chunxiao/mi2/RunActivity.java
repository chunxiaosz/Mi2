package com.ddkj.chunxiao.mi2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class RunActivity extends AppCompatActivity implements OnTouchListener{

    private ImageView btn_start_pause;
    private ImageView btn_stop;
    private TextView  state;

    private boolean run = false;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Intent intent;

    private final String TAG = "DDKJ_RunActivity";

    private final String ACTION_WARM  = "com.ddkj.chunxiao.mi2.heat";
    private final String ACTION_WASH  = "com.ddkj.chunxiao.mi2.wash";
    private final String ACTION_IN    = "com.ddkj.chunxiao.mi2.inject";
    private final String ACTION_WASTE = "com.ddkj.chunxiao.mi2.discharge";
    private final String ACTION_MIX   = "com.ddkj.chunxiao.mi2.mix";

    private TextView tv_model;
    private TextView tv_pre;
    private TextView tv_tmp;

    private String model;
    private String pre;
    private String tmp;
    private String ip_addr;
    private String port;

    private Map<String, String> map = new HashMap<String, String>();
    private Map<String, String> map2 = new HashMap<String, String>();


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            v.setBackground(getResources().getDrawable(R.drawable.run_bg));
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            v.getBackground().setAlpha(0);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.bg_gv));

        map.put("heat", "加热");
        map.put("inject", "进药");
        map.put("mix", "混合");
        map.put("discharge", "排废");
        map.put("wash", "冲洗");

        map2.put("heat", ACTION_WARM);
        map2.put("inject", ACTION_IN);
        map2.put("mix", ACTION_MIX);
        map2.put("discharge", ACTION_WASTE);
        map2.put("wash", ACTION_WASH);

        btn_start_pause = (ImageView)findViewById(R.id.start_pause);
        btn_stop = (ImageView)findViewById(R.id.stop);

        state = (TextView)findViewById(R.id.state);

        tv_model = (TextView)findViewById(R.id.run_et_model);
        tv_pre = (TextView)findViewById(R.id.run_et_pre);
        tv_tmp = (TextView)findViewById(R.id.run_et_tmp);

        preferences = getSharedPreferences("ddkj", MODE_PRIVATE);
        editor = preferences.edit();

        model = preferences.getString("model", "NA");
        if(map.containsKey(model)){
            tv_model.setText(map.get(model));
        }

        pre = preferences.getString("pressure", "80");
        tv_pre.setText(pre);

        tmp = preferences.getString("temperature", "30");
        tv_tmp.setText(tmp);

        ip_addr = preferences.getString("ip_addr", "127.0.0.1");
        port = preferences.getString("port", "9000");

        intent = new Intent(this, DDIntentService.class);

        //btn_start_pause.setOnTouchListener(this);
        //btn_stop.setOnTouchListener(this);

        btn_start_pause.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(map2.containsKey(model)){
                    intent.setAction(map2.get(model));
                }

                intent.putExtra("ip_addr", ip_addr);
                intent.putExtra("port", port);

                intent.putExtra("press", pre);
                intent.putExtra("temp", tmp);

                if(run) {
                    btn_start_pause.setImageDrawable(getDrawable(R.drawable.start));
                    state.setText("暂停");
                    run = false;

                    Log.d(TAG, "run -> pause");

                    intent.putExtra("state", "pause");
                }else{
                    btn_start_pause.setImageDrawable(getDrawable(R.drawable.pause));
                    state.setText("运行");
                    run = true;

                    Log.d(TAG, "pause -> run");

                    intent.putExtra("state", "start");
                }

                startService(intent);
            }
        });

        btn_stop.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(map2.containsKey(model)){
                    intent.setAction(map2.get(model));
                }

                intent.putExtra("ip_addr", ip_addr);
                intent.putExtra("port", port);

                //intent.putExtra("press", pre);
                //intent.putExtra("temp", tmp);

                intent.putExtra("state", "stop");

                startService(intent);

                finish();
            }
        });
    }

}
