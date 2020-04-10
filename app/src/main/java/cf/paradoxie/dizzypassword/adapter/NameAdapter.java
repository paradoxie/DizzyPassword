package cf.paradoxie.dizzypassword.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.SortBean;

public class NameAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<SortBean> names;

    public NameAdapter(Context context, List<SortBean> names) {
        this.context = context;
        this.names = names;
    }


    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_list_item_1, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = names.get(i).getName();
        holder.tv_name.setText(name);

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int pos) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = names.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == pos) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int i) {
        return names.get(i).getSortLetters().charAt(0);
    }

    class ViewHolder {

        TextView tv_name;


        public ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);

        }

    }
}
