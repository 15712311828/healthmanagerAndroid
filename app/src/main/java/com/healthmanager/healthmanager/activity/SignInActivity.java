package com.healthmanager.healthmanager.activity;

import android.content.DialogInterface;
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


public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button button_cancel=(Button)findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button button_submit=(Button)findViewById(R.id.submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_submit.setClickable(false);
                button_submit.setText("登录中");
                EditText passwordEditText=(EditText)findViewById(R.id.password);
                String password=passwordEditText.getText().toString();
                EditText emailEditText=(EditText)findViewById(R.id.email);
                String email=emailEditText.getText().toString();
                final Map<String,Object> request=new HashMap<>();
                request.put("password",password);
                request.put("email",email);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/signIn", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("提示")
                                    .setMessage("登录成功")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        else{
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_submit.setClickable(true);
                            button_submit.setText("重试");
                        }
                    }
                },SignInActivity.this);
            }
        });
    }
}
