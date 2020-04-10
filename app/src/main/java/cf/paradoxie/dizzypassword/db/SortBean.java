package cf.paradoxie.dizzypassword.db;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class SortBean implements Comparator<SortBean> {
    private String mName;
    private String mSortLetters;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSortLetters() {
        return mSortLetters;
    }

    public void setSortLetters(String sortLetters) {
        mSortLetters = sortLetters;
    }

    @Override
    public int compare(SortBean t1, SortBean t2) {
        Comparator<Object> com = Collator.getInstance(Locale.ENGLISH);
        return t1.getSortLetters().compareTo(t2.getSortLetters());
    }
}

