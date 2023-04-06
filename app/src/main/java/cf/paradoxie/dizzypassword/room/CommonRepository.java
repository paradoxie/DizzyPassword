package cf.paradoxie.dizzypassword.room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;


import java.util.List;

import cf.paradoxie.dizzypassword.bean.CommonEntity;


public class CommonRepository {
    private CommonDao commonDao;

    public CommonRepository(Context context) {
        this.commonDao = MyDataBase.init(context).getCommonDao();
    }

    /**
     * 插入数据库
     *
     * @param commonEntity
     */
    @SuppressLint("StaticFieldLeak")
    public void insert(CommonEntity commonEntity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                commonDao.insert(commonEntity);
                Log.e("插入成功", commonEntity.toString());
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void update(CommonEntity commonEntity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                commonDao.update(commonEntity);
                Log.e("更新成功", commonEntity.toString());
                return null;
            }
        }.execute();

    }

    /**
     * 查询所有
     *
     * @return
     */
    public LiveData<List<CommonEntity>> getAll() {
        return commonDao.getAll();
    }

    /**
     * 删除所有
     *
     * @return
     */
    @SuppressLint("StaticFieldLeak")
    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                commonDao.deleteAll();
                return null;
            }
        }.execute();
    }


    /**
     * 删除全部
     */
    public void delete(int hisId) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                commonDao.delete(hisId);
                return null;
            }
        }.execute();
    }
}
