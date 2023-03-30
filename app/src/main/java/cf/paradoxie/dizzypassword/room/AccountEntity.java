package cf.paradoxie.dizzypassword.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AccountEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String comId;
    //发送
    private String send;
    //接受
    private String receive;
    //创建时间
    private String create_time;
    //对话内容
    private String content;

    public AccountEntity(int id, String comId, String send, String receive, String create_time, String content) {
        this.id = id;
        this.comId = comId;
        this.send = send;
        this.receive = receive;
        this.create_time = create_time;
        this.content = content;
    }

    @Ignore
    public AccountEntity(String comId, String send, String receive, String create_time, String content) {
        this.comId = comId;
        this.send = send;
        this.receive = receive;
        this.create_time = create_time;
        this.content = content;
    }

    @Ignore
    public AccountEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommonEntity{" +
                "id=" + id +
                ", comId='" + comId + '\'' +
                ", send='" + send + '\'' +
                ", receive='" + receive + '\'' +
                ", create_time='" + create_time + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
