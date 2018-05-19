package com.healthmanager.healthmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.common.StepCounter;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LinearLayout userView;
    private LinearLayout healthView;
    private LinearLayout runView;

    StepCounter stepCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userView = findViewById(R.id.userTag);
        healthView = findViewById(R.id.healthTag);
        runView = findViewById(R.id.runTag);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("health").setIndicator("生活")
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String tag) {
                        return healthView;
                    }
                }));
        tabHost.addTab(tabHost.newTabSpec("run").setIndicator("运动")
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String tag) {
                        return runView;
                    }
                }));
        tabHost.addTab(tabHost.newTabSpec("user").setIndicator("我的")
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String tag) {
                        return userView;
                    }
                }));

        stepCounter = new StepCounter(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        final TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            private boolean isHealthInit=false;
            private boolean isRunInit=false;
            private boolean isUserInit=false;

            {
                String index=tabHost.getCurrentTabTag();
                Log.d("index init",index);
                onTabChanged(index);
            }

            @Override
            public void onTabChanged(String tabId) {
                Log.d("index change",tabId);
                if(tabId.equals("health")){
                    if(!isHealthInit) {
                        initHealthView();
                        isHealthInit = true;
                    }
                }
                else if(tabId.equals("user")){
                    if(!isUserInit) {
                        initUserView();
                        isUserInit = true;
                    }
                }
                else if(tabId.equals("run")){
                    if(!isRunInit) {
                        initRunView();
                        isRunInit = true;
                    }
                }

            }
        });
    }

    public void initUserView(){
        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/user/info", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception{
                userView.removeAllViews();
                if (result.getInt("status") != 0) {
                    String[] items={"登录","注册","忘记密码"};
                    View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.tag_user_notlogin,null);
                    userView.addView(view);
                    ListView listView=(ListView)findViewById(R.id.userNotLoginListView);
                    listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(id==0){
                                Intent intent=new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                            }
                            else if(id==1){
                                Intent intent=new Intent(MainActivity.this, SignUpActivity.class);
                                startActivity(intent);
                            }
                            else if(id==2){
                                Intent intent=new Intent(MainActivity.this, ForgetPasswordActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else if(result.getInt("status") == 0) {
                    JSONObject data=result.getJSONObject("data");
                    String[] items={"修改信息","修改密码","注销"};
                    View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.tag_user_login,null);
                    userView.addView(view);
                    TextView name=findViewById(R.id.name);
                    name.setText("你好："+data.getString("name"));
                    TextView email=findViewById(R.id.email);
                    email.setText(data.getString("email"));
                    TextView sign=findViewById(R.id.sign);
                    String signText = data.getString("sign");
                    sign.setText(signText.equals("")?"这个家伙很懒,什么都没写":signText);
                    ListView listView=(ListView)findViewById(R.id.userListView);
                    listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(id==0){
                                Intent intent=new Intent(MainActivity.this, ChangeInfoActivity.class);
                                startActivity(intent);
                            }
                            else if(id==1){
                                Intent intent=new Intent(MainActivity.this, ChangePasswordActivity.class);
                                startActivity(intent);
                            }
                            else if(id==2){
                                HttpUtil.clearCookie();
                                initUserView();
                            }
                        }
                    });
                }
            }
        },this);
    }

    public void initHealthView(){
        healthView.removeAllViews();
        String[] items={"BMI指数","饮水","心率","睡眠"};
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.tag_health,null);
        healthView.addView(view);
        ListView listView=(ListView)findViewById(R.id.healthListView);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id==0){
                    Intent intent=new Intent(MainActivity.this, BMIActivity.class);
                    startActivity(intent);
                }
                else if(id==1){
                    Intent intent=new Intent(MainActivity.this, WaterActivity.class);
                    startActivity(intent);
                }
                else if(id==2){
                    Intent intent=new Intent(MainActivity.this, HeartActivity.class);
                    startActivity(intent);
                }
                else if(id==3){
                    Intent intent=new Intent(MainActivity.this, SleepActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void initRunView(){
        runView.removeAllViews();
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.tag_running,null);
        runView.addView(view);
        HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/running/getToday", null, new HttpUtil.CallBack() {
            @Override
            public void call(JSONObject result) throws Exception {
                if (result.getInt("status") == 0) {
                    TextView textView=findViewById(R.id.step);
                    textView.setText("今日步数："+result.getInt("data"));
                }
            }
        },this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stepCounter.stop();
    }
}
