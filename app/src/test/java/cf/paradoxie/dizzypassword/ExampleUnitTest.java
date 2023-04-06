package cf.paradoxie.dizzypassword;

import static org.junit.Assert.assertEquals;


import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.bean.CommonEntity;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        List<CommonEntity> adBeans = new ArrayList<CommonEntity>();
        adBeans.add(new CommonEntity("饿了么","饿了么大额红包","https://img0.baidu.com/it/u=2964328697,2544864471&fm=253&fmt=auto&app=138&f=JPEG?w=888&h=500",
                "jumpurl",1,3,true));
        adBeans.add(new CommonEntity("美团外卖","美团外卖，送啥都快","https://img1.baidu.com/it/u=749498605,3792682769&fm=253&fmt=auto&app=138&f=JPEG?w=520&h=342",
                "jumpurl",1,2,true));
        adBeans.add(new CommonEntity("滴滴","滴滴打车","https://img0.baidu.com/it/u=2853145343,1297512061&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                "jumpurl",1,1,true));

        Gson gson =new Gson();
        String str = gson.toJson(adBeans);
       System.out.println(str);
    }
}