package com.example.SelectPhotoTest;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.SelectPhotoTest.Utils.PhotoUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class MyActivity extends Activity implements View.OnClickListener{
    /**
     * Called when the activity is first created.
     */

    private ImageView mImage;
    private Button mBtn;
    private PopupWindow mSetPhotoPop;

    private LinearLayout mMainView;
    private File mCurrentPhotoFile;
    // TODO 截图后的 Uri
    private Uri imageUri;
    // TODO 拍照的 Uri
    private Uri mAvatarUri;
    // TODO 图片存放路径
    private static final String IMAGE_FILE = Environment.getExternalStorageDirectory()+"/Yun/Images";
    // TODO 拍照的文件名
    private static final String IMAGE_FILE_PHOTO = Environment.getExternalStorageDirectory()+"/Yun/Images/avatar_test";
    // TODO 截图的文件名
    private static final String IMAGE_FILE_LOCATION = Environment.getExternalStorageDirectory()+"/Yun/Images/avatar_crop";


    private static final int PHOTO_PICKED_WITH_DATA = 1881;
    private static final int CAMERA_WITH_DATA = 1882;
    private static final int PHOTO_CROP_RESOULT = 1883;
    private static final int ICON_SIZE = 450;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
    }

    public void initView(){
        mMainView = (LinearLayout) findViewById(R.id.main_layout);
        mImage = (ImageView) findViewById(R.id.main_show_image);
        mBtn = (Button) findViewById(R.id.main_btn);
        mBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_btn:
                showPop();
                break;
        }
    }

    /**
     *  弹出 popupwindow
     */
    public void showPop(){
        View mainView = LayoutInflater.from(this).inflate(R.layout.alert_setphoto_menu_layout, null);
        Button btnTakePhoto = (Button) mainView.findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
                // 拍照获取
                doTakePhoto();
            }
        });
        Button btnCheckFromGallery = (Button) mainView.findViewById(R.id.btn_check_from_gallery);
        btnCheckFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
                // 相册获取
                doPickPhotoFromGallery();
            }
        });
        Button btnCancle = (Button) mainView.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetPhotoPop.dismiss();
            }
        });
        mSetPhotoPop = new PopupWindow(this);
        mSetPhotoPop.setBackgroundDrawable(new BitmapDrawable());
        mSetPhotoPop.setFocusable(true);
        mSetPhotoPop.setTouchable(true);
        mSetPhotoPop.setOutsideTouchable(true);
        mSetPhotoPop.setContentView(mainView);
        mSetPhotoPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSetPhotoPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSetPhotoPop.setAnimationStyle(R.style.bottomStyle);
        mSetPhotoPop.showAtLocation(mMainView, Gravity.BOTTOM, 0, 0);
        mSetPhotoPop.update();
    }

    /**
     * 调用系统相机拍照
     */
    protected void doTakePhoto() {
        try {
            File yunDir = new File(IMAGE_FILE);
            if(!yunDir.exists()){
                yunDir.mkdirs();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            mAvatarUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "/Yun/Images/avatar_test"
                    ));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 从相册选择图片
     */
    protected void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 相册裁剪图片
     *
     * @param uri
     */
    /*public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, "image");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", ICON_SIZE);
        intent.putExtra("outputY", ICON_SIZE);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CROP_RESOULT);

    }*/

    /**
     * 裁剪图片
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        File yunDir = new File(IMAGE_FILE);
        if(!yunDir.exists()){
            yunDir.mkdirs();
        }
        imageUri = Uri.fromFile(new File(IMAGE_FILE_LOCATION));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("circleCrop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", ICON_SIZE);
        intent.putExtra("outputY", ICON_SIZE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//图像输出
        intent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);//回调方法data.getExtras().getParcelable("data")返回数据为空
        startActivityForResult(intent, PHOTO_CROP_RESOULT);


    }

    private Bitmap getBitmapFromUri(Uri uri, Context mContext) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            switch (requestCode) {
                case PHOTO_PICKED_WITH_DATA:
                    // 相册选择图片后裁剪图片
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_WITH_DATA:
                    // 相机拍照后裁剪图片
                    // 判断是否有旋转度
                    Bitmap photoBitmap = PhotoUtil.readBitmapFromPath(this,IMAGE_FILE_PHOTO);
                    int degree = PhotoUtil.getExifOrientation(IMAGE_FILE_PHOTO);
                    if(degree != 0){
                        photoBitmap = PhotoUtil.rotaingImageView(photoBitmap,degree);
                        if(mCurrentPhotoFile == null){
                            mCurrentPhotoFile = new File(IMAGE_FILE_LOCATION);
                        }
                        mCurrentPhotoFile = PhotoUtil.saveBitmaptoSdCard(photoBitmap,this,"/Yun/Images");
                        startPhotoZoom(Uri.fromFile(mCurrentPhotoFile));
                    }else{
                        startPhotoZoom(mAvatarUri);
                    }
                    break;
                case PHOTO_CROP_RESOULT:
                    try {
                        Bitmap bitmap = getBitmapFromUri(imageUri,this);
                        mImage.setImageBitmap(bitmap);
                    }catch (Exception e){
                        Toast.makeText(this,"失败，请重新设置",Toast.LENGTH_SHORT).show();
                    }
                    break;

            }


        }
    }


}
