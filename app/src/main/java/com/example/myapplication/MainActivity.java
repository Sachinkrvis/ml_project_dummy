package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.ml.Data;
import com.example.myapplication.ml.Model;
import com.example.myapplication.ml.Try;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    Button selectBtn,predictBtn,captureBtn;
    Bitmap bitmap;
    TextView result;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String labels[]=new String[1001];
//        int count=0;
//        try {
//            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
//            String line=bufferedReader.readLine();
//            while (line!=null){
//                labels[count]=line;
//                count++;
//                line=bufferedReader.readLine();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        String labels[]=new String[8];
        int count=0;
        try {
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(getAssets().open("newlabels.txt")));
            String line=bufferedReader.readLine();
            while (line!=null){
                labels[count]=line;
                count++;
                line=bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        selectBtn=findViewById(R.id.Select_btn);
        predictBtn=findViewById(R.id.predict_btn);
        captureBtn=findViewById(R.id.capture_btn);
        result=findViewById(R.id.result_textView);
        imageView=findViewById(R.id.Image_view);
        // when selectBtn is clicked
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectBtnClicked();
            }
        });

//--------------------------------------------------------------------------------------------------
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Model model = Model.newInstance(MainActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                    bitmap=Bitmap.createScaledBitmap(bitmap,64,64,true);
//                    String tensorImage = TensorImage(DataType.FLOAT32)
//                    tensorImage.load(bitmap)
//                    byteBuffer = tensorImage.buffer
                    TensorImage ti = new TensorImage(DataType.FLOAT32);
                    ti.load(bitmap);

                    inputFeature0.loadBuffer(ti.getBuffer());

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    result.setText(outputFeature0.getFloatArray()+" ");
                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }

                //----------------------------
//                try {
//                    Try model = Try.newInstance(MainActivity.this);
//
//                    // Creates inputs for reference.
//                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
//                    bitmap=Bitmap.createScaledBitmap(bitmap,64,64,true);
//                    TensorImage ti = new TensorImage(DataType.FLOAT32);
//                    ti.load(bitmap);
//
//                    inputFeature0.loadBuffer(ti.getBuffer());
//
//
//                    // Runs model inference and gets result.
//
//
//                    Try.Outputs outputs = model.process(inputFeature0);
//                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//                    result.setText(outputFeature0.getFloatArray()+" ");
//                    // Releases model resources if no longer used.
//                    model.close();
//                } catch (IOException e) {
//                    // TODO Handle the exception
//                }


            }
        });
//--------------------------------------------------------------------------------------------------
        // when captureBtn is clicked
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    CaptureBtnClicked();
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},11);
                }
            }
        });
    }
    int getMax(float[]arr){
        int max=0;
        for(int i=0;i<arr.length;i++){
            if(arr[i]>arr[max]){
                max=i;
            }
        }
        return max;
    }

    // functions for Capture button is clicked
    void CaptureBtnClicked(){
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,12);
    }
    

//--------------------------------------------------------------------------------------------------
    // functions for Select button is clicked
    void  SelectBtnClicked(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,10);

   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==10){
            if(data!=null){
                Uri uri=data.getData();
                try {
                    bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        } else if (requestCode==12) {
            bitmap=(Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}