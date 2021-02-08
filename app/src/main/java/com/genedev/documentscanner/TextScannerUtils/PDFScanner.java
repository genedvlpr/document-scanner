package com.genedev.documentscanner.TextScannerUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.genedev.documentscanner.HelperUtil;
import com.genedev.documentscanner.HomeUtils.HomeRegUser;
import com.genedev.documentscanner.R;
import com.google.android.material.button.MaterialButton;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.Utils;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class PDFScanner extends AppCompatActivity  {

    private MaterialButton btn_scan, btn_save, btn_view;
    private ImageView img_preview;

    private static final int OPEN_THING = 99;

    private static final int REQUEST_CODE_SCAN = 47;

    private static final String SAVED_SCANNED_HHOTO = "scanned_photo";

    private static final int REQUEST_CODE = 99;

    private CameraView cameraView;
    private String scannedPhoto;
    private final ViewHolder viewHolder = new ViewHolder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_scanner);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        btn_save = findViewById(R.id.btn_save);
        btn_scan = findViewById(R.id.btn_scan);
        btn_view = findViewById(R.id.btn_view);

        img_preview = findViewById(R.id.img_preview);
        viewHolder.prepare(findViewById(android.R.id.content));
        if (savedInstanceState != null) {
            scannedPhoto = savedInstanceState.getString(SAVED_SCANNED_HHOTO);
        }

        if (scannedPhoto != null) {
            img_preview.setImageBitmap(Utils.getBitmapFromLocation(scannedPhoto));
        }
        cameraView = findViewById(R.id.camera);

        cameraView.start();

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();
                //Context context = PDFScanner.this;
                try {
                    String path = Environment.getExternalStorageDirectory()+"/Android/data/com.genedev.documentscanner/files/images/";
                    File file = new File(path, "takendocphoto.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    FileOutputStream fos = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    Log.d("File", file.toString()+"");


                } catch (Exception e) {
                    Log.e("saveToInternalStorage()", e.getMessage());

                }
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btn_scan.setOnClickListener(v -> {
            //cameraView.captureImage();
            onScanButtonClicked();
        });

        btn_save.setOnClickListener(v -> {
            HelperUtil.nextActivity(this, HomeRegUser.class);
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //viewHolder.scabBtn.setOnClickListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //viewHolder.scabBtn.setOnClickListener(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            String imgPath = data.getStringExtra(ScanActivity.RESULT_IMAGE_PATH);
            Bitmap bitmap = Utils.getBitmapFromLocation(imgPath);
            img_preview.setImageBitmap(bitmap);
//            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                getContentResolver().delete(uri, null, null);
//                viewHolder.image.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SCANNED_HHOTO, scannedPhoto);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void onScanButtonClicked() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_BRAND_IMG_RES, R.drawable.ic_crop_white_24dp);
        intent.putExtra(ScanActivity.EXTRA_TITLE, "Crop Document");
        intent.putExtra(ScanActivity.EXTRA_ACTION_BAR_COLOR, R.color.colorAccent);
        intent.putExtra(ScanActivity.EXTRA_LANGUAGE, "en");
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    private static class ViewHolder {

        ImageView image;
        View scabBtn;

        void prepare(View parent) {
            image = (ImageView) parent.findViewById(R.id.img_preview);
            scabBtn = parent.findViewById(R.id.btn_scan);
        }
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
