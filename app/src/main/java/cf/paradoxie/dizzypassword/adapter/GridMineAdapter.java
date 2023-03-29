package cf.paradoxie.dizzypassword.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.MineNavBean;

public class GridMineAdapter extends RecyclerView.Adapter<GridMineAdapter.ViewHolder> {
    private Activity context;
    private List<MineNavBean> lists = new ArrayList<>();

    public GridMineAdapter(Activity context) {
        this.context = context;
    }


    public void setLists(List<MineNavBean> newLists) {
       if (newLists!=null){
           lists.clear();
           lists.addAll(newLists);
           notifyDataSetChanged();
       }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root_view = LayoutInflater.from(context)
                .inflate(R.layout.item_mine_grid, parent, false);
        return new ViewHolder(root_view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MineNavBean homeGridBean = lists.get(position);
        holder.tv_info.setText("" + homeGridBean.getText());
        Glide.with(context).load(homeGridBean.getLogo()).into(holder.iv_icon);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_info;
        ImageView iv_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_info = itemView.findViewById(R.id.tv_info);
            iv_icon = itemView.findViewById(R.id.iv_icon);
        }
    }

}
