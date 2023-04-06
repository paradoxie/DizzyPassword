package cf.paradoxie.dizzypassword.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cf.paradoxie.dizzypassword.bean.CommonEntity;

@Dao
public interface CommonDao {

    //插入一条数据
    @Insert
    void insert(CommonEntity... commonEntity);

    //删除一条数据
    @Query("DELETE FROM commonentity WHERE id= :hisId")
    void delete(int hisId);

    //删除所有数据
    @Query("DELETE  FROM commonentity")
    void deleteAll();


    //更新一条数据
    @Update()
    void update(CommonEntity commonEntity);


    //查询所有内容
    @Query("SELECT * FROM commonentity ORDER BY id DESC")
    LiveData<List<CommonEntity>> getAll();

}
