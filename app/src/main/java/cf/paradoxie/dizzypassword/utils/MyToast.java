package cf.paradoxie.dizzypassword.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cf.paradoxie.dizzypassword.R;


/**
 * Created by Yzker on 2017/2/17.
 */

public class MyToast {


    private static LinearLayout toastView = null;
    private static LinearLayout.LayoutParams params = null;
    private static Toast toast;
    private static long oneTime=0;
    private static long twoTime=0;
    private static String oldMsg;


    public static void show(Context context, String message,int color) {
        if (toastView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            toastView = (LinearLayout) inflater.inflate(R.layout.toast, null);
            toastView.setBackgroundColor(color);
        }
        TextView tv = (TextView) toastView.findViewById(R.id.message);
        tv.setLayoutParams(params);
        tv.setText(message);
        if (toast==null){
            toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);
            toast.show();
            oneTime=System.currentTimeMillis();
        }else{
            twoTime=System.currentTimeMillis();
            if(message.equals(oldMsg)){
                if(twoTime-oneTime>Toast.LENGTH_LONG){
                    toast.show();
                }
            }else{
                oldMsg = message;
                tv.setText(message);
                toast.show();
            }
        }
        oneTime=twoTime;
    }
}
