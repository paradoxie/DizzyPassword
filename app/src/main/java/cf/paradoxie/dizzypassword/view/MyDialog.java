package cf.paradoxie.dizzypassword.view;

import android.app.Activity;

import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyDialog {
    private static MyDialog instance = new MyDialog();
    private SweetAlertDialog pDialog = null;

    public static MyDialog getInstance() {
        return instance;
    }

    private MyDialog() {

    }

    public void show(Activity context, String title) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(ThemeUtils.getPrimaryDarkColor(context));
        pDialog.setTitleText(title);
        pDialog.show();
    }

    public void dismiss() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}
