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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WaterActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        initView();

        final Button button_add=(Button)findViewById(R.id.btn_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_add.setClickable(false);
                button_add.setText("添加中");
                EditText mlEditText=(EditText)findViewById(R.id.ml);
                String mlString = mlEditText.getText().toString();
                if(mlString.equals("")){
                    mlString="0";
                }
                Integer ml = Integer.valueOf(mlString);
                final Map<String,Object> request=new HashMap<>();
                request.put("milliliter",ml);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/water/add", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(WaterActivity.this)
                                    .setTitle("提示")
                                    .setMessage("添加成功")
                                    .setPositiveButton("确定", null)
                                    .show();
                            initView();
                            button_add.setClickable(true);
                            button_add.setText("添加");
                        }
                        else if((result.getInt("status")==-100)){
                            new AlertDialog.Builder(WaterActivity.this)
                                    .setTitle("提示")
                                    .setMessage("登陆后才能添加")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_add.setClickable(true);
                            button_add.setText("添加");
                        }
                        else{
                            new AlertDialog.Builder(WaterActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_add.setClickable(true);
                            button_add.setText("添加");
                        }
                    }
                },WaterActivity.this);
            }
        });
    }

    public void initView(){
        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/water/query", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {
                if (result.getInt("status") == 0) {
                    JSONArray data = result.getJSONArray("data");
                    TableLayout tableLayout=(TableLayout)findViewById(R.id.table);
                    tableLayout.removeAllViews();
                    Integer sum=0;
                    for(int i=0;i<data.length();i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        int milliliter = jsonObject.getInt("milliliter");
                        sum+=milliliter;
                        Date time = new Date(jsonObject.getLong("time"));
                        TextView mlTextView=new TextView(WaterActivity.this);
                        mlTextView.setText(String.valueOf(milliliter)+"ml");
                        mlTextView.setTextSize(18);
                        mlTextView.setPadding(50,20,50,20);
                        TextView timeTextView=new TextView(WaterActivity.this);
                        timeTextView.setTextSize(18);
                        timeTextView.setPadding(50,20,50,20);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        timeTextView.setText(simpleDateFormat.format(time));
                        TableRow tableRow=new TableRow(WaterActivity.this);
                        tableRow.addView(timeTextView);
                        tableRow.addView(mlTextView);
                        tableRow.setGravity(Gravity.CENTER);
                        tableLayout.addView(tableRow);
                    }
                    TextView realTextView=(TextView)findViewById(R.id.real);
                    realTextView.setText("今日饮水量："+String.valueOf(sum)+"ml");
                }
            }
        }, this);

        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/body/query", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {
                if (result.getInt("status") == 0) {
                    JSONObject data=result.getJSONObject("data");
                    TextView suggestTextView=(TextView)findViewById(R.id.suggest);
                    suggestTextView.setText("建议饮水量："+String.valueOf(data.getDouble("weight")*40)+"ml");
                }
            }
        },this);
    }
}
