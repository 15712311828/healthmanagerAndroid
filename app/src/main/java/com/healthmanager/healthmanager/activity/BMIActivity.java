package com.healthmanager.healthmanager.activity;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BMIActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/body/query", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {
                if (result.getInt("status") == 0) {
                    JSONObject data=result.getJSONObject("data");
                    EditText heightEditText=(EditText)findViewById(R.id.height);
                    heightEditText.setText(String.valueOf(data.getDouble("height")));
                    EditText weightEditText=(EditText)findViewById(R.id.weight);
                    weightEditText.setText(String.valueOf(data.getDouble("weight")));
                    BMICalculate();
                }
            }
        },this);

        Button button_cancel=(Button)findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button button_calculate=(Button)findViewById(R.id.calculate);
        button_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BMICalculate();
            }
        });

        final Button button_finish=(Button)findViewById(R.id.finish);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_finish.setClickable(false);
                button_finish.setText("保存中");
                EditText heightEditText=(EditText)findViewById(R.id.height);
                String heightString = heightEditText.getText().toString();
                if(heightString.equals("")){
                    heightString="0";
                }
                Double height = Double.valueOf(heightString);
                EditText weightEditText=(EditText)findViewById(R.id.weight);
                String weightString = weightEditText.getText().toString();
                if(weightString.equals("")){
                    weightString="0";
                }
                Double weight = Double.valueOf(weightString);
                final Map<String,Object> request=new HashMap<>();
                request.put("height",height);
                request.put("weight",weight);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/body/update", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(BMIActivity.this)
                                    .setTitle("提示")
                                    .setMessage("保存成功")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("保存");
                        }
                        else if((result.getInt("status")==-100)){
                            new AlertDialog.Builder(BMIActivity.this)
                                    .setTitle("提示")
                                    .setMessage("登陆后才能保存")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("保存");
                        }
                        else{
                            new AlertDialog.Builder(BMIActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("保存");
                        }
                    }
                },BMIActivity.this);
            }
        });

    }

    private void BMICalculate(){
        Double result;
        try {
            EditText heightEditText = (EditText) findViewById(R.id.height);
            Double height = Double.valueOf(heightEditText.getText().toString());
            EditText weightEditText = (EditText) findViewById(R.id.weight);
            Double weight = Double.valueOf(weightEditText.getText().toString());
            if (height < 1e-6 || height < 1e-6) {
                result = 0.d;
            } else {
                result = weight / (height / 100) / (height / 100);
                BMIAnalise(result);
            }
        }
        catch (Exception e){
            result=0.d;
        }
        EditText bmiEditText=(EditText)findViewById(R.id.bmi);
        bmiEditText.setText(String.valueOf(result));
    }

    private void BMIAnalise(Double result){
        TextView textView=(TextView)findViewById(R.id.warn);
        String pre="BMI分析：";
        String content="";
        if(result<18.5){
            content="你有点瘦哦";
        }
        else if(result<23.9){
            content="身体刚刚好，继续保持";
        }
        else if(result<27){
            content="有点微胖";
        }
        else if(result<32){
            content="同学，该减肥了";
        }
        else{
            content="重量级选手";
        }
        textView.setText(pre+content);

    }
}
