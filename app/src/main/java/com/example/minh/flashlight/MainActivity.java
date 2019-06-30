package com.example.minh.flashlight;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkDrawOverlayPermission();
        addListener();
    }

    private void addListener() {
        findViewById(R.id.btn_run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOverView();
            }
        });
    }


//    private void init() {
//        ActivityCompat.requestPermissions(MainActivity.this,
//                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startService(new Intent(this, MessageService.class));
//        } else {
//            Toast.makeText(this, "Not Permission", Toast.LENGTH_SHORT).show();
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


    // Check permission overlay
    public void checkDrawOverlayPermission() {
        /* check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT > 22) {
            if (!Settings.canDrawOverlays(this)) {
                /* if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /* request permission via start activity for result */
                startActivityForResult(intent, 2019);
            } else {
                startOverView();
            }
        } else {
            startOverView();
        }
    }

    private void startOverView() {
        Intent intent =  new Intent(this, MessageService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2019 && Build.VERSION.SDK_INT > 22) {
            /* if so check once again if we have permission */
            if (Settings.canDrawOverlays(this)) {
                startOverView();
            }
        }
    }

}
