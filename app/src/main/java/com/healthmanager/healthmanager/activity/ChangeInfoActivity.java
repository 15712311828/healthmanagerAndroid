package com.healthmanager.healthmanager.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);

        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/info", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {
                if (result.getInt("status") == 0) {
                    JSONObject data=result.getJSONObject("data");
                    EditText nameEditText=(EditText)findViewById(R.id.name);
                    nameEditText.setText(data.getString("name"));
                    EditText signEditText=(EditText)findViewById(R.id.sign);
                    signEditText.setText(data.getString("sign"));
                    RadioGroup radioGroup=(RadioGroup)findViewById(R.id.sex);
                    if(data.getInt("sex")==1){
                        radioGroup.check(R.id.male);
                    }
                    else{
                        radioGroup.check(R.id.female);
                    }
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

        final Button button_finish=(Button)findViewById(R.id.finish);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_finish.setClickable(false);
                button_finish.setText("修改中");
                EditText nameEditText=(EditText)findViewById(R.id.name);
                String name=nameEditText.getText().toString();
                EditText signEditText=(EditText)findViewById(R.id.sign);
                String sign=signEditText.getText().toString();
                RadioGroup radioGroup=(RadioGroup)findViewById(R.id.sex);
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                Integer sex=checkedRadioButtonId==R.id.male?1:0;
                final Map<String,Object> request=new HashMap<>();
                request.put("name",name);
                request.put("sex",sex);
                request.put("sign",sign);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/changeInfo", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(ChangeInfoActivity.this)
                                    .setTitle("提示")
                                    .setMessage("修改成功")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        else{
                            new AlertDialog.Builder(ChangeInfoActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("重试");
                        }
                    }
                },ChangeInfoActivity.this);
            }
        });
    }
}
