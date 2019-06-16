package com.example.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.Helper.DatabaseAccessHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class pill_result extends AppCompatActivity implements View.OnClickListener{

    private EditText shape;//shape
    private EditText color;
    private EditText print;
    private Button restart, research;
    private TextView result_name; //itemname
    private Intent intent, data;
    private ImageView imageView;
    private String icolor, ishape, tess1, tess2, pill_name;
    private int percent;
    public static String imageURL = " ";
    private ProgressDialog progressDialog;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_result);

        shape = findViewById(R.id.color); //shape
        color = findViewById(R.id.shape);
        print = findViewById(R.id.print);

        restart = findViewById(R.id.restart);
        research = findViewById(R.id.research);
        result_name = findViewById(R.id.result);//itemname

        imageView = (ImageView)findViewById(R.id.imageView2);

        research.setOnClickListener(this);
        restart.setOnClickListener(this);

        //Intent()를 이용하여 이전 Activity에서 글자,모양,색 값 받아오기
        data = getIntent();
        ishape = data.getStringExtra("shape1");
        icolor = data.getStringExtra("color1");
        tess1 = data.getStringExtra("tess1");
        tess2 = data.getStringExtra("tess2");

        //화면에 받아온 값 표시하기
        shape.setText(ishape);
        color.setText(icolor);
        print.setText(tess1);

        //Database(pill.db)에 접근하여 input값과 동일한 알약의 이름 찾기
        DatabaseAccessHelper databaseAccess = DatabaseAccessHelper.getInstance(getApplicationContext());
        databaseAccess.open();

        String n = shape.getText().toString();
        String c = color.getText().toString();
        String p = print.getText().toString();

        //이름 찾기
        String name = databaseAccess.getNAME(n,c,p);
        String npname = databaseAccess.npgetNAME(n,p);
        String cpname = databaseAccess.cpgetNAME(c,p);
        String ncname = databaseAccess.ncgetNAME(n,c);

        //이미지 URL찾기
        imageURL = databaseAccess.getURL(pill_name);

        result_name.setText(" NAME :  "+pill_name);//itemname

        //Thread를 사용하여, URL에서 이미지를 로드해 화면에 보여주기
        Thread mthread = new Thread(){
            public void run(){
                try{
                    URL url = new URL(imageURL);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bitmap);

                }catch(IOException ex){

                }
            }
        };

        mthread.start();

        try{
            mthread.join();

        }catch (InterruptedException e){

        }

        databaseAccess.close();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.research:
                DatabaseAccessHelper databaseAccess = DatabaseAccessHelper.getInstance(getApplicationContext());
                databaseAccess.open();

                String n = shape.getText().toString();
                String c = color.getText().toString();
                String p = print.getText().toString();

                String name = databaseAccess.getNAME(n,c,p);//shape 의 itemname...

                imageURL = databaseAccess.getURL(name);

                result_name.setText(" NAME :  " +name);//itemname

                Thread mthread = new Thread(){
                    public void run(){
                        try{
                            URL url = new URL(imageURL);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);

                        }catch(IOException ex){

                        }
                    }
                };

                mthread.start();

                try{
                    mthread.join();
                    imageView.setImageBitmap(bitmap);
                }catch (InterruptedException e){

                }

                databaseAccess.close();

                break;
            case R.id.restart:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
