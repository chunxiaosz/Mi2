package com.ddkj.chunxiao.mi2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class DDListAdapter extends BaseAdapter {
    private Context mContext;

    public String[] img_text = { "压力: ", "温度: ",};
    public String[] et_text = {"100", "36",};
    public String[] utv_text = {"mmHg", "℃",};

    private EditText et;

    public DDListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.para_item);
        et = BaseViewHolder.get(convertView, R.id.et_item);
        TextView utv = BaseViewHolder.get(convertView, R.id.unit_item);
        ImageButton btn_plus = BaseViewHolder.get(convertView, R.id.btn1_item);
        ImageButton btn_minus = BaseViewHolder.get(convertView, R.id.btn2_item);

        tv.setText(img_text[position]);
        et.setText(et_text[position]);
        utv.setText(utv_text[position]);

        btn_plus.setBackgroundResource(R.drawable.plus);
        btn_minus.setBackgroundResource(R.drawable.minus);


        return convertView;
    }

}
