package com.example.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.finalproject.Helper.bBitmapHelper;
import com.example.finalproject.Helper.fBitmapHelper;

import java.io.File;

public class pill_Image_Load extends AppCompatActivity implements View.OnClickListener {


    private static final int PICK_FROM_CAMERA_F = 0;
    private static final int PICK_FROM_ALBUM_F = 1;
    private static final int CROP_FROM_CAMERA_F = 2;
    private static final int PICK_FROM_CAMERA_B = 3;
    private static final int PICK_FROM_ALBUM_B = 4;
    private static final int CROP_FROM_CAMERA_B = 5;

    private Uri mImageCaptureUri;
    private ImageView fPhotoImageView, bPhotoImageView;
    private Button fButton,bButton,iButton;
    private Bitmap bitmapF, bitmapB;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill__image__load);

        fButton = (Button) findViewById(R.id.loadF);
        fPhotoImageView = (ImageView) findViewById(R.id.imageF);

        bButton = (Button)findViewById(R.id.loadB);
        bPhotoImageView = (ImageView) findViewById(R.id.imageB);

        iButton = (Button)findViewById(R.id.btn_start);

        fButton.setOnClickListener(this);
        bButton.setOnClickListener(this);
        iButton.setOnClickListener(this);

    }

    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction(int image)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".png";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        if(image == 1)
            startActivityForResult(intent, PICK_FROM_CAMERA_F);
        else
            startActivityForResult(intent, PICK_FROM_CAMERA_B);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction(int image)
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

        if(image == 1)
            startActivityForResult(intent, PICK_FROM_ALBUM_F);
        else
            startActivityForResult(intent, PICK_FROM_ALBUM_B);

    }

    /**
     * 함수 호출이 일어났을 때 원하는 과정을 수행한 후 이미지 생성
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA_F:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    bitmapF = photo;
                    fPhotoImageView.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM_F:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }
            case PICK_FROM_CAMERA_F:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 96);
                intent.putExtra("outputY", 96);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA_F);

                break;
            }

            case CROP_FROM_CAMERA_B:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    bitmapB = photo;
                    bPhotoImageView.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM_B:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();
            }
            case PICK_FROM_CAMERA_B:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA_B);

                break;
            }


        }
    }


    //버튼 클릭 시 발생
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadF:
                DialogInterface.OnClickListener cameraListenerF = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction(1);
                    }
                };

                DialogInterface.OnClickListener albumListenerF = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction(1);
                    }
                };

                DialogInterface.OnClickListener cancelListenerF = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListenerF)
                        .setNeutralButton("앨범선택", albumListenerF)
                        .setNegativeButton("취소", cancelListenerF)
                        .show();

                break;
            case R.id.loadB:
                DialogInterface.OnClickListener cameraListenerB = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction(2);
                    }
                };

                DialogInterface.OnClickListener albumListenerB = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction(2);
                    }
                };

                DialogInterface.OnClickListener cancelListenerB = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("사진촬영", cameraListenerB)
                        .setNeutralButton("앨범선택", albumListenerB)
                        .setNegativeButton("취소", cancelListenerB)
                        .show();
                break;
            case R.id.btn_start:
                fBitmapHelper.getInstance().setBitmap(bitmapF);
                bBitmapHelper.getInstance().setBitmap(bitmapB);
                Intent intent = new Intent(this, pill_Identifier.class);
                startActivity(intent);
                break;
        }
    }

}
