package com.example.aicard;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleRegistry;

import java.util.Arrays;

public class CustomizeMainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, ">>>onCreate..");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, ">>>onStart..");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, ">>>onResume..");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, ">>>onRestart..");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, ">>>onPause..");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, ">>>onStop..");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, ">>>onDestroy..");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String msg = String.format("RequestCode: %s,  GrantResults: %s,  Permissions: %s", requestCode, Arrays.toString(grantResults), Arrays.toString(permissions));
        Log.v(TAG, ">>>权限请求结果 onRequestPermissionsResult\n" + msg);
    }
}
