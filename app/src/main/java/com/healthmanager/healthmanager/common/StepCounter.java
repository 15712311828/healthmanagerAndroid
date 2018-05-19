package com.healthmanager.healthmanager.common;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.healthmanager.healthmanager.R;
import com.healthmanager.healthmanager.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.SENSOR_SERVICE;

public class StepCounter {

    AppCompatActivity activity;
    SensorManager mSensorManager;
    Sensor mStepDetector;
    SensorEventListener listener=new SensorEventListener() {

        int i=0;
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("step",String.valueOf(event.values[0]));
            i++;
            if(i%100==3){
                final Map<String,Object> request=new HashMap<>();
                request.put("step",(int)event.values[0]);
                HttpUtil.postJsonAsync("http://140.143.209.108:8080/HealthManager/running/add", new JSONObject(request), new HttpUtil.CallBack() {
                    @Override
                    public void call(JSONObject result) throws Exception {

                    }
                },activity);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public StepCounter(AppCompatActivity activity){
        this.activity=activity;
        mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER  );
        mSensorManager.registerListener(listener, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop(){
        mSensorManager.unregisterListener(listener);
    }

}
