package cf.paradoxie.dizzypassword.bean;

import java.io.Serializable;

public class SpSaveModel<T> implements Serializable {
    private int saveTime;
    private T value;
    private long currentTime;

    public SpSaveModel() {
    }

    public SpSaveModel(int saveTime, T value,long currentTime) {
        this.saveTime = saveTime;
        this.value = value;
        this.currentTime=currentTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(int saveTime) {
        this.saveTime = saveTime;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SpSaveModel{" +
                "saveTime=" + saveTime +
                ", value=" + value +
                ", currentTime=" + currentTime +
                '}';
    }
}
