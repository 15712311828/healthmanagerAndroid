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

public class ChangePasswordActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

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
                EditText oldPasswordEditText=(EditText)findViewById(R.id.oldpassword);
                String oldPassword=oldPasswordEditText.getText().toString();
                EditText passwordEditText=(EditText)findViewById(R.id.newpassword);
                String password=passwordEditText.getText().toString();
                final Map<String,Object> request=new HashMap<>();
                request.put("oldPassword",oldPassword);
                request.put("password",password);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/changePassword", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {
                        if(result.getInt("status")==0){
                            new AlertDialog.Builder(ChangePasswordActivity.this)
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
                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setTitle("错误")
                                    .setMessage(result.getString("message"))
                                    .setPositiveButton("确定", null)
                                    .show();
                            button_finish.setClickable(true);
                            button_finish.setText("重试");
                        }
                    }
                },ChangePasswordActivity.this);
            }
        });
    }
}
