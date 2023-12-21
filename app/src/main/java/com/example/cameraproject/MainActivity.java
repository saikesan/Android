package com.example.cameraproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity
{
    private final String[] REQUIRED_PERMISSIONS = new String[]
            {
                    "android.permission.CAMERA",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            };

    private static final int CAMERA_REQUEST_CODE = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Button enableCamera = findViewById(R.id.enableCamera);
        enableCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (hasCameraPermission())
                {
                    enableCamera();
                }
                else
                {
                    requestPermission();
                }
            }
        });

        File file = new File(this.getExternalMediaDirs()[0], "Picture");
        if(!file.mkdirs())
        {
            Log.d("Main", "Dir not created");
        }
        else
        {
            Log.d("Main", "Dir created");
        }
    }

    private boolean hasCameraPermission()
    {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions
                (
                        this,
                        REQUIRED_PERMISSIONS,
                        CAMERA_REQUEST_CODE
                );
    }

    private void enableCamera()
    {
        Intent intent = new Intent(this, CameraActivity.class);

        startActivity(intent);
    }
}
