package cf.paradoxie.dizzypassword.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.MyApplication;

public class SPUtils {


    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";
    public static final String FILE_NAME_LIST = "beans_data";

    public SPUtils() {

        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {

        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject) {
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME, MyApplication.getContext().MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public static <T> void setDataList(String tag, List<T> datalist) {

        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public static <T> List<T> getDataList(String tag, Class<T> clazz) {
        List<T> datalist = new ArrayList<>();
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME_LIST, Context.MODE_PRIVATE);
        String strJson = sp.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        //        Gson gson = new Gson();
        //        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        //        }.getType());
        //解决强转类型错误：方案来自知乎
        JsonArray array = new JsonParser().parse(strJson).getAsJsonArray();
        for (final JsonElement elem : array) {
            datalist.add(new Gson().fromJson(elem, clazz));
        }
        return datalist;

    }

    public static void removeDataList(String key) {
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param
     */
    public static void clear() {
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences sp_1 = MyApplication.getContext()
                .getSharedPreferences(FILE_NAME_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferences.Editor editor_1 = sp_1.edit();
        editor_1.clear();
        SharedPreferencesCompat.apply(editor);
        SharedPreferencesCompat.apply(editor_1);
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    //获取密钥
    public static String getKey() {
        String str = SPUtils.get("password", "") + "";
        StringBuffer sb = null;
        int strLen = str.length();
        if (strLen < 8) {
            while (strLen < 8) {
                sb = new StringBuffer();
                sb.append(str).append("0");//右(后)补0
                str = sb.toString();
                strLen = str.length();
            }
        } else {
            str = str.substring(0, 8);
        }
        return str;
    }

}