package cf.paradoxie.dizzypassword.utils;

import cf.paradoxie.dizzypassword.base.MyApplication;


public class SizeUtils {
    private SizeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
