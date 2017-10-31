package cf.paradoxie.dizzypassword;


import android.app.Activity;

import java.util.Stack;

public class AppManager {

    private static Stack<Activity> activityStack;// activity的栈
    private static AppManager instance;// AppManager对象实现

    private AppManager() {

    }
    /**
     * 单例模式
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        // 创建实例
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        // 添加到堆栈
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);// 结束
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            //activityStack.remove(activity);// 先从堆栈里移除
            activity.finish();// 再结束当前的Activity
            activity = null;// 之后把引用变为null，结束内存
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {// 遍历堆栈
            if (activity.getClass().equals(cls)) {// 匹配当前的类对象和结束类名是否一样
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {// 遍历堆栈中activity，来关闭
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();// 清空堆栈
    }
}
