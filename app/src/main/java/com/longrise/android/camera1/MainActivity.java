package com.longrise.android.camera1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private ImageView mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreview = findViewById(R.id.iv_preview);

        findViewById(R.id.face_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 面部识别
                final Intent intent = new Intent(MainActivity.this, FaceVerifyActivity.class);
                startActivityForResult(intent, 100);

            }
        });

        findViewById(R.id.preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 预览效果
                final Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        final String path = data.getStringExtra("path");
        final Bitmap bitmap = BitmapFactory.decodeFile(path);
        mPreview.setImageBitmap(bitmap);
    }

}
