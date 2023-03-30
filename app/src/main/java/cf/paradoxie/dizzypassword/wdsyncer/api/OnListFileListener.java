package cf.paradoxie.dizzypassword.wdsyncer.api;


import java.util.List;

import cf.paradoxie.dizzypassword.wdsyncer.model.DavData;

public interface OnListFileListener {
    public void listAll(List<DavData> davResourceList);
    public void onError(String errorMsg);
}
