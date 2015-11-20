package com.ddkj.chunxiao.mi2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import android.util.Log;
import java.util.TooManyListenersException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private DDGridView gridview;
    //private ListView   listview;

    private final String TAG = "DDKJ";

    private final String ACTION_WARM  = "com.ddkj.chunxiao.mi2.heat";
    private final String ACTION_WASH  = "com.ddkj.chunxiao.mi2.wash";
    private final String ACTION_IN    = "com.ddkj.chunxiao.mi2.inject";
    private final String ACTION_WASTE = "com.ddkj.chunxiao.mi2.discharge";
    private final String ACTION_MIX   = "com.ddkj.chunxiao.mi2.mix";

    private EditText pressure;
    private EditText temp;
    private ImageButton btn1_plus, btn1_minus, btn2_plus, btn2_minus;

    private  Intent intent;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private EditText.OnKeyListener onKeyListener = new EditText.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //Log.d(TAG, "KEY PRESS");
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager.isActive()){
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }

                if(pressure.isFocused()){
                    pressure.clearFocus();
                }else if(temp.isFocused()){
                    temp.clearFocus();
                }

                return true;
            }
            return false;
        }
    };

    private ImageButton.OnClickListener onClickListener = new ImageButton.OnClickListener(){

        @Override
        public void onClick(View v) {
            int view_id = v.getId();
            int cur_value;
            switch(view_id){
                case R.id.plus_pressure:
                    cur_value = Integer.parseInt(pressure.getText().toString()) + 5;
                    pressure.setText(String.valueOf(cur_value));
                    break;
                case R.id.minus_pressure:
                    cur_value = Integer.parseInt(pressure.getText().toString()) - 5;
                    pressure.setText(String.valueOf(cur_value));
                    break;
                case R.id.plus_temp:
                    cur_value = Integer.parseInt(temp.getText().toString()) + 1;
                    temp.setText(String.valueOf(cur_value));
                    break;
                case R.id.minus_temp:
                    cur_value = Integer.parseInt(temp.getText().toString()) - 1;
                    temp.setText(String.valueOf(cur_value));
                    break;
                default:
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "POS is: " + position);
        //Log.d(TAG, "parent is:" + parent.getId());
        String str = pressure.getText().toString();
        Log.d(TAG, "pressureX: " + str);
        if(!str.equals(preferences.getString("pressure", null))){
            editor.putString("pressure", str);
            //editor.commit();
        }
        str = temp.getText().toString();
        Log.d(TAG, "tempX: " + str);
        if(!str.equals(preferences.getString("temperature", null))){
            editor.putString("temperature", str);
            //editor.commit();
        }

        intent = new Intent(this, RunActivity.class);

        if(parent.getId() == R.id.gridview){
            //Log.d(TAG, "GridView");
            switch(position){
                case 0:
                    //intent.setAction(ACTION_WARM);
                    //startService(intent);
                    editor.putString("model", "heat");
                    editor.commit();

                    startActivity(intent);
                    break;
                case 1:
                    editor.putString("model", "inject");
                    editor.commit();

                    startActivity(intent);
                    break;
                case 2:
                    editor.putString("model", "mix");
                    editor.commit();

                    startActivity(intent);
                    break;
                case 3:
                    editor.putString("model", "discharge");
                    editor.commit();

                    startActivity(intent);
                    break;
                case 4:
                    editor.putString("model", "wash");
                    editor.commit();

                    startActivity(intent);
                    break;
                case 5:
                    break;
                default:

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate");
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setLogo(R.mipmap.user);
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        intent = new Intent(this, DDIntentService.class);

        gridview=(DDGridView) findViewById(R.id.gridview);
        gridview.setAdapter(new DDGridAdapter(this));

        gridview.setOnItemClickListener(this);

        /*
        listview = (ListView)findViewById(R.id.list_para);
        listview.setAdapter(new DDListAdapter(this));
        listview.setOnItemClickListener(this);
        */

        pressure = (EditText)findViewById(R.id.et_pre);
        pressure.setOnKeyListener(onKeyListener);
        temp     = (EditText)findViewById(R.id.et_temp);
        temp.setOnKeyListener(onKeyListener);

        btn1_plus = (ImageButton)findViewById(R.id.plus_pressure);
        btn1_plus.setOnClickListener(onClickListener);
        btn1_minus = (ImageButton)findViewById(R.id.minus_pressure);
        btn1_minus.setOnClickListener(onClickListener);
        btn2_plus = (ImageButton)findViewById(R.id.plus_temp);
        btn2_plus.setOnClickListener(onClickListener);
        btn2_minus = (ImageButton)findViewById(R.id.minus_temp);
        btn2_minus.setOnClickListener(onClickListener);

        preferences = getSharedPreferences("ddkj", MODE_PRIVATE);
        editor = preferences.edit();

        String str;
        str = preferences.getString("pressure", null);
        Log.d(TAG, "pressure: " + str);
        if(str == null){
            Log.d(TAG, pressure.getText().toString());
            editor.putString("pressure", pressure.getText().toString());
            //editor.commit();
        }else{
            pressure.setText(str);
        }

        str = preferences.getString("temperature", null);
        Log.d(TAG, "temp: " + str);
        if(str == null){
            Log.d(TAG, temp.getText().toString());
            editor.putString("temperature", temp.getText().toString());
            //editor.commit();
        }else{
            temp.setText(str);
        }
        editor.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity onStart");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "MainActivity onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.home){
            Toast.makeText(this, item.getTitle(), 1000).show();
            finish();
            return true;
        }else if(id == R.id.action_user){
            //Toast.makeText(this, "Hello world", 1000).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
