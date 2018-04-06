package com.healthmanager.healthmanager.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        RadioGroup radioGroup=(RadioGroup)findViewById(R.id.sex);
        radioGroup.check(R.id.male);

        final Button button_code=(Button)findViewById(R.id.btn_code);
        button_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_code.setClickable(false);
                button_code.setText("发送中");
                EditText editText=(EditText)findViewById(R.id.email);
                String email=editText.getText().toString();
                Map<String,String> map=new HashMap<>();
                map.put("email",email);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/verificationCode", new JSONObject(map), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception{
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("发送成功")
                                    .setMessage("邮件已发送")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_code.setText("已发送");
                            button_code.setClickable(true);
                        }
                        else{
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("发送失败")
                                    .setMessage("请确定邮箱正确，再点击重试")
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_code.setText("重试");
                            button_code.setClickable(true);
                        }
                    }
                },SignUpActivity.this);
            }
        });

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
                button_finish.setText("注册中");
                EditText nameEditText=(EditText)findViewById(R.id.name);
                String name=nameEditText.getText().toString();
                EditText passwordEditText=(EditText)findViewById(R.id.password);
                String password=passwordEditText.getText().toString();
                EditText emailEditText=(EditText)findViewById(R.id.email);
                String email=emailEditText.getText().toString();
                EditText codeEditText=(EditText)findViewById(R.id.code);
                String code=codeEditText.getText().toString();
                EditText signEditText=(EditText)findViewById(R.id.sign);
                String sign=signEditText.getText().toString();
                RadioGroup radioGroup=(RadioGroup)findViewById(R.id.sex);
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                Integer sex=checkedRadioButtonId==R.id.male?1:0;
                final Map<String,Object> request=new HashMap<>();
                request.put("name",name);
                request.put("password",password);
                request.put("email",email);
                request.put("verificationCode",code);
                request.put("sex",sex);
                request.put("sign",sign);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/signUp", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("提示")
                                    .setMessage("注册成功")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        else{
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("重试");
                        }
                    }
                },SignUpActivity.this);
            }
        });
    }
}
