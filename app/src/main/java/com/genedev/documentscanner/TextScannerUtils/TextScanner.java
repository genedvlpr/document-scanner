package com.genedev.documentscanner.TextScannerUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.genedev.documentscanner.HelperUtil;
import com.genedev.documentscanner.HomeUtils.HomeGuest;
import com.genedev.documentscanner.HomeUtils.HomeRegUser;
import com.genedev.documentscanner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;
import java.util.Objects;

public class TextScanner extends AppCompatActivity {


    private FloatingActionButton fab_area, fab_finish, fab_add, fab_parameter, fab_indicator, fab_content;

    private CameraView camera;
    private Class aClass;
    private String last_intent;
    private ImageView img_preview;

    private TextView tv_area, tv_parameter, tv_indicator, tv_content;

    private ImageButton flashOff, flashOn;

    private MaterialCardView crd_area, crd_parameter, crd_indicator, crd_content;

    private FirebaseVisionImage image;
    private FirebaseVisionTextRecognizer detector;

    private  View overlay;

    private String document_part = "";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_scanner);
        overlay = findViewById(R.id.layout);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        fab_area = findViewById(R.id.fab_area);
        fab_parameter = findViewById(R.id.fab_parameter);
        fab_indicator = findViewById(R.id.fab_indicator);
        fab_content = findViewById(R.id.fab_content);
        fab_finish = findViewById(R.id.fab_finish);

        img_preview = findViewById(R.id.img_preview);

        tv_area = findViewById(R.id.tv_area);
        tv_parameter = findViewById(R.id.tv_parameter);
        tv_indicator = findViewById(R.id.tv_indicator);
        tv_content = findViewById(R.id.tv_content);

        crd_area = findViewById(R.id.crd_area_holder);
        crd_area.setVisibility(View.INVISIBLE);

        flashOff = findViewById(R.id.flashOff);
        flashOn = findViewById(R.id.flashOn);

        flashOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.setFlash(CameraKit.Constants.FLASH_TORCH);
            }
        });

        flashOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.setFlash(CameraKit.Constants.FLASH_OFF);
            }
        });

        fab_finish.setOnClickListener(v -> {
            HelperUtil.nextActivityFromHome(last_intent, this, PDFScanner.class);
        });

        camera = findViewById(R.id.camera);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                camera.start();
            }
        }, 3000);
        camera.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, 480, 200, false);

                image = FirebaseVisionImage.fromBitmap(Objects.requireNonNull(bitmap));
                detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

                Task<FirebaseVisionText> result =
                        detector.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText result) {
                                        // Task completed successfully
                                        // ...
                                        String resultText = result.getText();
                                        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                                            String blockText = block.getText();
                                            Float blockConfidence = block.getConfidence();
                                            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                            Point[] blockCornerPoints = block.getCornerPoints();
                                            Rect blockFrame = block.getBoundingBox();
                                            for (FirebaseVisionText.Line line: block.getLines()) {
                                                String lineText = line.getText();
                                                if (document_part.equals("Parameter")){
                                                    tv_parameter.setText(lineText);
                                                }
                                                Float lineConfidence = line.getConfidence();
                                                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                                Point[] lineCornerPoints = line.getCornerPoints();
                                                Rect lineFrame = line.getBoundingBox();
                                                for (FirebaseVisionText.Element element: line.getElements()) {
                                                    String elementText = element.getText();
                                                    Float elementConfidence = element.getConfidence();
                                                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                                    Point[] elementCornerPoints = element.getCornerPoints();
                                                    Rect elementFrame = element.getBoundingBox();
                                                    if (document_part.equals("Indicator")){
                                                        tv_indicator.setText(elementText);
                                                    }
                                                }
                                            }
                                        }
                                        if (document_part.equals("Area")){
                                            crd_area.setVisibility(View.VISIBLE);
                                            tv_area.setText(resultText);
                                        }

                                        if (document_part.equals("Content")){
                                            tv_content.setText(resultText);
                                        }
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


        Intent i = getIntent();
        last_intent = i.getStringExtra("Class");
        try {
            if (last_intent.equals("HomeReg")){
                aClass = HomeRegUser.class;
            } else {
                aClass = HomeGuest.class;
            }
        }catch (Exception e){
            Log.d("Error", "Setting class");
        }



        fab_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage();
                document_part = "Area";
            }
        });

        fab_parameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage();
                document_part = "Parameter";
            }
        });

        fab_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage();
                document_part = "Indicator";
            }
        });

        fab_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage();
                document_part = "Content";
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
        overlay = findViewById(R.id.layout);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.setFlash(CameraKit.Constants.FLASH_OFF);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //camera.setFlash(CameraKit.Constants.FLASH_AUTO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //camera.setFlash(CameraKit.Constants.FLASH_AUTO);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        HelperUtil.nextActivityFromHome(last_intent,this, aClass);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e("TAG", "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }
}
