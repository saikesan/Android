package com.example.cameraproject;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class CameraActivity extends AppCompatActivity {
    private ImageButton SnapshotBtn;
    private PreviewView previewView;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private Preview preview;
    private ImageAnalysis imageAnalysis;
    private CameraSelector cameraSelector;

    private String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private int rotation;
    private int screenAspectRatio;

    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        previewView = findViewById(R.id.previewView);
        SnapshotBtn = findViewById(R.id.camera_capture_button);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        //handler = new Handler();

        cameraProviderFuture.addListener(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    bindPreview(cameraProvider);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

        SnapshotBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File photoFile =  new File(getApplicationContext().getExternalMediaDirs()[0],
                        "Picture/"+new SimpleDateFormat(FILENAME_FORMAT, Locale.ROOT).format(System.currentTimeMillis()) + ".jpg");


                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

                imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback()
                {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults)
                    {
                        String msg = "Take Picture Success! path:\n"+ photoFile.getAbsolutePath();
                        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.i("Camera", msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception)
                    {
                        String msg = "Take Picture Error!\n";
                        Log.e("Camera", msg);
                        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        previewView.getDisplay().getRealMetrics(metrics);
  //      screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels);
        rotation = previewView.getDisplay().getRotation();

        imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(Surface.ROTATION_180)
                .build();

        preview = new Preview.Builder().build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
    //            .setTargetAspectRatio(screenAspectRatio)
                .setIoExecutor(cameraExecutor)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview, imageCapture);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

    }
}
