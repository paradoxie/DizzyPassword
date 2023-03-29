package cf.paradoxie.dizzypassword.http;



import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by actlistener on 2018/4/7.
 * <p>
 * FormUrlEncoded注解和Multipart注解不能同时使用,否则会抛出methodError(“Only one encoding annotation is allowed.”)
 * Path注解与Url注解不能同时使用,否则会抛出parameterError(p, “@Path parameters may not be used with @Url.”)
 * 对于FiledMap,HeaderMap,PartMap,QueryMap这四种作用于方法的注解,其参数类型必须为Map的实例,且key的类型必须为String类型,否则抛出异常
 */

public interface HttpApi {


}
