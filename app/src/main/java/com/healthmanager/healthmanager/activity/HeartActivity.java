package com.healthmanager.healthmanager.activity;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HeartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/heart/query", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {
                if (result.getInt("status") == 0) {
                    JSONObject data = result.getJSONObject("data");
                    EditText editText = (EditText) findViewById(R.id.rate);
                    editText.setText(String.valueOf(data.getInt("rate")));
                }
            }
        }, this);

        Button button_cancel = (Button) findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button button_measure = (Button) findViewById(R.id.measure);
        button_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HeartActivity.this)
                        .setTitle("提示")
                        .setMessage("暂不支持")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });

        final Button button_finish = (Button) findViewById(R.id.finish);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_finish.setClickable(false);
                button_finish.setText("保存中");
                EditText editText = (EditText) findViewById(R.id.rate);
                String rateString = editText.getText().toString();
                if (rateString.equals("")) {
                    rateString = "0";
                }
                Integer rate = Integer.valueOf(rateString);
                final Map<String, Object> request = new HashMap<>();
                request.put("rate", rate);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/heart/update", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if (result.getInt("status") == 0) {
                            new AlertDialog.Builder(HeartActivity.this)
                                    .setTitle("提示")
                                    .setMessage("保存成功")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("保存");
                        } else if ((result.getInt("status") == -100)) {
                            new AlertDialog.Builder(HeartActivity.this)
                                    .setTitle("提示")
                                    .setMessage("登陆后才能保存")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("保存");
                        } else {
                            new AlertDialog.Builder(HeartActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("保存");
                        }
                    }
                }, HeartActivity.this);
            }
        });

    }
}
