package com.healthmanager.healthmanager.activity;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SleepActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        initView();

        final Button button_start=(Button)findViewById(R.id.start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_start.setClickable(false);
                button_start.setText("正在开始");
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/sleep/start", null, new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(SleepActivity.this)
                                    .setTitle("提示")
                                    .setMessage("开始成功")
                                    .setPositiveButton("确定", null)
                                    .show();
                            initView();
                            button_start.setClickable(true);
                            button_start.setText("开始睡眠计时");
                        }
                        else if((result.getInt("status")==-100)){
                            new AlertDialog.Builder(SleepActivity.this)
                                    .setTitle("提示")
                                    .setMessage("登陆后才能使用")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_start.setClickable(true);
                            button_start.setText("开始睡眠计时");
                        }
                        else{
                            new AlertDialog.Builder(SleepActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_start.setClickable(true);
                            button_start.setText("开始睡眠计时");
                        }
                    }
                },SleepActivity.this);
            }
        });

        final Button button_end=(Button)findViewById(R.id.end);
        button_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_end.setClickable(false);
                button_end.setText("正在结束");
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/sleep/end", null, new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(SleepActivity.this)
                                    .setTitle("提示")
                                    .setMessage("成功")
                                    .setPositiveButton("确定", null)
                                    .show();
                            initView();
                            button_end.setClickable(true);
                            button_end.setText("结束计时");
                        }
                        else if((result.getInt("status")==-100)){
                            new AlertDialog.Builder(SleepActivity.this)
                                    .setTitle("提示")
                                    .setMessage("登陆后才能使用")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_end.setClickable(true);
                            button_end.setText("结束计时");
                        }
                        else{
                            new AlertDialog.Builder(SleepActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_end.setClickable(true);
                            button_end.setText("结束计时");
                        }
                    }
                },SleepActivity.this);
            }
        });
    }

    public void initView(){
        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/sleep/query", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {

                if (result.getInt("status") == 0) {
                    JSONArray data = result.getJSONArray("data");
                    TableLayout tableLayout=(TableLayout)findViewById(R.id.table);
                    tableLayout.removeAllViews();
                    for(int i=0;i<data.length();i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Date startTime = new Date(jsonObject.getLong("startTime"));
                        Date endTime = new Date(jsonObject.getLong("endTime"));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        TextView startTimeTextView=new TextView(SleepActivity.this);
                        startTimeTextView.setText(timeFormat.format(startTime));
                        startTimeTextView.setTextSize(18);
                        startTimeTextView.setPadding(50,20,50,20);
                        TextView endTimeTextView=new TextView(SleepActivity.this);
                        endTimeTextView.setText(timeFormat.format(endTime));
                        if(startTime.equals(endTime)){
                            endTimeTextView.setText("正在计时");
                        }
                        endTimeTextView.setTextSize(18);
                        endTimeTextView.setPadding(50,20,50,20);
                        TextView dateTextView=new TextView(SleepActivity.this);
                        dateTextView.setText(dateFormat.format(startTime));
                        dateTextView.setTextSize(18);
                        dateTextView.setPadding(50,20,50,20);
                        TableRow tableRow=new TableRow(SleepActivity.this);
                        tableRow.addView(dateTextView);
                        tableRow.addView(startTimeTextView);
                        tableRow.addView(endTimeTextView);
                        tableRow.setGravity(Gravity.CENTER);
                        tableLayout.addView(tableRow);

                        if(i==0&&(!startTime.equals(endTime))){
                            showLast(startTime,endTime);
                        }
                        else if(i==1&&(!isLastShow())){
                            showLast(startTime,endTime);
                        }
                    }
                }
            }
        }, this);
    }

    public boolean isLastShow(){
        TextView textView=(TextView)findViewById(R.id.yesteday);
        return !textView.getText().toString().equals("暂无数据");
    }

    public void showLast(Date start,Date end){
        Long period=end.getTime()-start.getTime();
        Long hour=period/(60*60*1000);
        period%=(60*60*1000);
        Long minite=period/(60*1000);
        period%=(60*1000);
        Long second=period/1000;
        String time=new StringBuilder(String.valueOf(hour)).append("小时").append(String.valueOf(minite)).append("分钟")
                .append(String.valueOf(second)).append("秒").toString();
        TextView textView=(TextView)findViewById(R.id.yesteday);
        textView.setText(time);
    }
}