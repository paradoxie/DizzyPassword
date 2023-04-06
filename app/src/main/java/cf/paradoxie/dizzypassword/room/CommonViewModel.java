package cf.paradoxie.dizzypassword.room;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.bean.CommonEntity;


public class CommonViewModel extends ViewModel {
    private CommonRepository commonRepository;

    public CommonViewModel() {
        commonRepository = new CommonRepository(MyApplication.getContext());
    }

    /**
     * 增加一条转换记录
     *
     * @param jd
     */
    public void insert(CommonEntity jd) {
        commonRepository.insert(jd);
        Log.e("插入数据库", jd.toString());
    }



    /**
     * 查询所有记录
     *
     */
    public LiveData<List<CommonEntity>> getAll() {
        return commonRepository.getAll();
    }

    /**
     * 删除所有记录
     *
     */
    public void deleteAll() {
         commonRepository.deleteAll();
        Log.e("清除所有数据", "");
    }

    /**
     * 更新
     *
     * @param commonEntity
     */
    public void update(CommonEntity commonEntity) {
        commonRepository.update(commonEntity);
    }


    /**
     * 删除一条记录
     */
    public void delete(int hisId) {
        commonRepository.delete(hisId);
    }

}
