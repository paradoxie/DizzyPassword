package cf.paradoxie.dizzypassword.bean;

public class MineNavBean {
    private int logo;
    private String text;

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MineNavBean(int logo, String text) {
        this.logo = logo;
        this.text = text;
    }
}
