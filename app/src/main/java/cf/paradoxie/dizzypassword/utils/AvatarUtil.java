package cf.paradoxie.dizzypassword.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import cf.paradoxie.dizzypassword.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 头像工具类
 */
public class AvatarUtil {
    private static AvatarUtil avatarUtil = new AvatarUtil();
    private static Activity context;
    private ImageView imgView;
    String path = Environment.getExternalStorageDirectory().getPath() + "/头像/";

    private AvatarUtil() {
    }

    public static AvatarUtil getInstance(Activity c) {
        if (context == null) {
            context = c;
        }
        return avatarUtil;
    }


    public void showImage(String url) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        imgView = getView();
        Utils.loadImg(imgView, url, false);
        dialog.setContentView(imgView);
        dialog.getWindow().setWindowAnimations(R.style.DialogOutAndInStyle);   //设置dialog的显示动画
        dialog.show();

        // 点击图片消失
        imgView.setOnClickListener(v1 -> {
            dialog.dismiss();
        });
        ImageView finalImgView = imgView;
        imgView.setOnLongClickListener(view -> {
            requestAllPower();
            popSave(finalImgView);
            return false;
        });
    }

    //申请权限，需要使用之前申请
    private void requestAllPower() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
            }
        }
    }

    private ImageView getView() {
        ImageView imgView = new ImageView(context);
        imgView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

        @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.mipmap.ic_logo);
        Drawable drawable = BitmapDrawable.createFromStream(is, null);
        imgView.setImageDrawable(drawable);

        return imgView;
    }

    private void popSave(ImageView finalImgView) {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("保存图片")
                .setContentText("保存路径：" + path)
                .setConfirmText("保存")
                .setConfirmClickListener(sDialog -> {
                    saveBitmap(finalImgView, path);
                    sDialog.cancel();
                })
                .show();

    }

    private void saveBitmap(View view, String filePath) {

        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        //存储
        FileOutputStream outStream = null;

        isFolderExists(filePath);

        File file = new File(filePath + Calendar.getInstance().getTimeInMillis() + ".png");
        if (file.isDirectory()) {//如果是目录不允许保存
            Toast.makeText(context, "该路径为目录路径", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error", e.getMessage() + "#");
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                bitmap.recycle();
                if (outStream != null) {
                    outStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);

        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else
                return false;
        }
        return true;
    }
}
