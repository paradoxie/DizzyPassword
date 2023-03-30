package cf.paradoxie.dizzypassword.wdsyncer.api;

public interface OnSyncResultListener {
    public void onSuccess(String result);
    public void onError(String errorMsg);
}
