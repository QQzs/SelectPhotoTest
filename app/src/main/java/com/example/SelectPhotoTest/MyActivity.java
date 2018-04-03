package com.example.SelectPhotoTest;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

public class MyActivity extends Activity implements View.OnClickListener{
    /**
     * Called when the activity is first created.
     */

    private ImageView mImage;
    private Button mBtn;
    private PopupWindow mSetPhotoPop;

    private LinearLayout mMainView;
    // TODO 截图后的 Uri
    private Uri imageUri;
    // TODO 拍照的 Uri
    private Uri mAvatarUri;

    private static final int PHOTO_PICKED_WITH_DATA = 1881;

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
                mAvatarUri = PhotoUtil.doTakePhoto(MyActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            switch (requestCode) {
                case PHOTO_PICKED_WITH_DATA:
                    // 相册选择图片后裁剪图片
                    imageUri = PhotoUtil.startPhotoZoom(this,data.getData());
                    break;
                case PhotoUtil.CAMERA_WITH_DATA:
                    // 相机拍照后裁剪图片
                    imageUri = PhotoUtil.startPhotoZoom(this,PhotoUtil.getImageUri(this,mAvatarUri,"avatar_test"));
                    break;
                case PhotoUtil.PHOTO_CROP_RESOULT:
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    }catch (Exception e){
                        Toast.makeText(this,"失败，请重新设置",Toast.LENGTH_SHORT).show();
                    }
                    if (bitmap != null){
                        mImage.setImageBitmap(bitmap);
                    }
                    break;

            }


        }
    }


}
