package com.example.finalproject;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.Helper.bBitmapHelper;
import com.example.finalproject.Helper.fBitmapHelper;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class pill_Identifier extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private TessBaseAPI tessBaseAPI;

    private TextView detectlabel1, detectlabel2;
    private android.widget.Button Button,Button_start, Button_next;
    private ImageView imageVIewInput;
    private ImageView imageVIewOutput;
    private Mat img_input_f, img_output_f=null;
    private Mat img_input_b, img_output_b=null;
    private int threshold1=50;
    private int threshold2=150;

    private int returned;
    private Bitmap bmp32_f, bmp32_b, bmp32f, bmp32b;

    private static final String TAG = "opencv";
    private final int GET_GALLERY_IMAGE = 200;

    public String shape1, shape2, color1, color2,tess1,tess2 ;
    boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill__identifier);
        OpenCVLoader.initDebug();

        imageVIewInput = (ImageView)findViewById(R.id.imgV_input);
        imageVIewOutput = (ImageView)findViewById(R.id.imgV_output);
        detectlabel1 = (TextView)findViewById(R.id.detectlabel1);
        detectlabel2 = (TextView)findViewById(R.id.detectlabel2);
        Button = (Button)findViewById(R.id.btn_play);
        Button_start = (Button)findViewById(R.id.btn_start);
        Button_next = (Button)findViewById(R.id.identify_B);

        //이전 activity에서 intent를 이용하여 이미지를 받아오기.
        imageVIewInput.setImageBitmap(fBitmapHelper.getInstance().getBitmap());
        imageVIewOutput.setImageBitmap(bBitmapHelper.getInstance().getBitmap());

        //identify하는 input 이미지 설정하기
        img_input_f = new Mat();
        bmp32_f = fBitmapHelper.getInstance().getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32_f, img_input_f);

        img_input_b = new Mat();
        bmp32_b = bBitmapHelper.getInstance().getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32_b, img_input_b);

        //tesseract 실행
        tessBaseAPI = new TessBaseAPI();
        String dir = getFilesDir() + "/tesseract";
        if(checkLanguageFile(dir+"/tessdata"))
            tessBaseAPI.init(dir, "eng");

        Button.setOnClickListener(this);
        Button_start.setOnClickListener(this);
        Button_next.setOnClickListener(this);
    }

    /**
     * Button 에 대한 실행
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                Intent nextIntent = new Intent(this, pill_result.class);
                nextIntent.putExtra("shape1", shape1);
                nextIntent.putExtra("color1", color1);
                nextIntent.putExtra("shape2", shape2);
                nextIntent.putExtra("color2", color2);
                nextIntent.putExtra("tess1", tess1);
                nextIntent.putExtra("tess2", tess2);
                startActivity(nextIntent);
                break;
            case R.id.btn_start:
                imageprocess_and_showResult(threshold1, threshold2,1);
                new AsyncTess().execute(bmp32f);
                break;
            case R.id.identify_B:
                imageprocess_and_showResult(threshold1, threshold2,2);
                new AsyncTess2().execute(bmp32b);
                break;
        }
    }

    /**
     * Tesseract OCR.
     */
    private boolean checkLanguageFile(String dir) {
        File file = new File(dir);
        if(!file.exists() && file.mkdirs())
            createFiles(dir);
        else if(file.exists()){
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if(!langDataFile.exists())
                createFiles(dir);
        }
        return true;
    }

    private void createFiles(String dir) {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try{
            inputStream = assetMgr.open("eng.traineddata");
            String destFile = dir + "/eng.traineddata";
            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while((read = inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {

        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result){
            print(1,returned,result);
            tess1 = result;
            Button.setEnabled(true);

        }
    }

    private class AsyncTess2 extends AsyncTask<Bitmap, Integer, String> {

        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result){
            print(2,returned,result);
            tess2 = result;
            Button.setEnabled(true);
        }
    }



    private void print(int image, int returned, String result){

        int shape,color;
        color =  returned/10;
        shape = returned%10;
        System.out.println("color:"+color);
        System.out.println("shale:"+shape);

        String Sshape = "삼각형 ";
        String Scolor = "검정 ";

        switch(shape){
            case 1:
                Sshape = "삼각형";
                break;
            case 2:
                Sshape = "사각형";
                break;
            case 3:
                Sshape = "마름모";
                break;
            case 4:
                Sshape = "오각형";
                break;
            case 5:
                Sshape = "육각형";
                break;
            case 6:
                Sshape = "팔각형";
                break;
            case 7:
                Sshape = "원형";
                break;
            case 8:
                Sshape = "타원형";
                break;
            case 9:
                Sshape = "장방형";
                break;
            default:
                Sshape = "null";

        }

        switch(color){
            case 1:
                Scolor = "핑크";
                break;
            case 2:
                Scolor = "자주";
                break;
            case 3:
                Scolor = "보라";
                break;
            case 4:
                Scolor = "빨강";
                break;
            case 5:
                Scolor = "주황";
                break;
            case 6:
                Scolor = "노랑";
                break;
            case 7:
                Scolor = "초록";
                break;
            case 8:
                Scolor = "연두";
                break;
            case 9:
                Scolor = "파랑";
                break;
            case 10:
                Scolor = "청록";
                break;
            case 11:
                Scolor = "남색";
                break;
            case 12:
                Scolor = "갈색";
                break;
            case 13:
                Scolor ="검정";
                break;
            case 14:
                Scolor = "하양";
                break;
            default:
                Scolor = "Null";
        }

        if(image == 1){
            detectlabel1.setText("     1.   "+"S :"+Sshape+"   C  :"+Scolor+"   I  :"+result);
            shape1 = Sshape;
            color1 = Scolor;
        }else {
            detectlabel2.setText("     2.   " + "S :" + Sshape + "   C  :" + Scolor + "   I  :" + result);
            shape2 = Sshape;
            color2 = Scolor;
        }
    }

    /**
     * openCV를 이용한 Identifier 실행
     */
    @Override
    protected void onResume() {
        super.onResume();

        isReady = true;
    }

    //opencv를 사용하여 image processing 하기.
    public native int imageprocessing(long inputImage);
    public native void tessImage(long inputImage, long outputImage);

    private void imageprocess_and_showResult(int th1, int th2, int image) {

        if (isReady==false) return;

        if (img_output_f == null)
            img_output_f = new Mat(img_input_f.cols(), img_input_f.rows(), CvType.CV_8UC1);
        else
            Imgproc.resize(img_output_f, img_output_f, img_input_f.size());

        if (img_output_b == null)
            img_output_b = new Mat(img_input_b.cols(), img_input_b.rows(), CvType.CV_8UC1);
        else
            Imgproc.resize(img_output_b,img_output_b,img_input_b.size());

        if(image == 1) {
            System.out.println("@@@@image1");
            tessImage(img_input_f.getNativeObjAddr(), img_output_f.getNativeObjAddr());
            returned = imageprocessing(img_input_f.getNativeObjAddr());
            bmp32f = Bitmap.createBitmap(img_output_f.cols(), img_output_f.rows(), Bitmap.Config.ARGB_8888);
            System.out.println("@@@@"+img_output_f.rows()+", " + img_output_f.cols()+", " + bmp32f.getHeight()+", " + bmp32f.getWidth());
            Utils.matToBitmap(img_output_f, bmp32f);
        }else {
            System.out.println("@@@@image2");
            tessImage(img_input_b.getNativeObjAddr(), img_output_b.getNativeObjAddr());
            returned = imageprocessing(img_input_b.getNativeObjAddr());
            bmp32b = Bitmap.createBitmap(img_output_b.cols(), img_output_b.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(img_output_b,bmp32b);
        }

    }



    /**
     * Permission 코드
     */
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS  = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //모든 퍼미션이 허가된 경우
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!writeAccepted )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  pill_Identifier.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

}
