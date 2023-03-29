package cf.paradoxie.dizzypassword.http;

public interface HttpListener<T> {
    void success(T t);
    void failed();
}
