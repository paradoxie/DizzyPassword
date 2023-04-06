package cf.paradoxie.dizzypassword.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.CommonEntity;
import cf.paradoxie.dizzypassword.utils.Utils;

public class GridMineAdapter extends RecyclerView.Adapter<GridMineAdapter.ViewHolder> implements View.OnClickListener {
    private Activity context;
    private List<CommonEntity> lists = new ArrayList<>();
    private Onclick onclick;

    public GridMineAdapter(Activity context, Onclick onclick) {
        this.context = context;
        this.onclick = onclick;
    }


    public void setLists(List<CommonEntity> newLists) {
        if (newLists != null) {
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
        CommonEntity homeGridBean = lists.get(position);
        holder.tv_info.setText("" + homeGridBean.getName());
//        Glide.with(context).load(homeGridBean.getLogo()).into(holder.iv_icon);
        Utils.loadImg(holder.iv_icon, homeGridBean.getPicUrl(), false);

        holder.card.setOnClickListener(this);
        holder.card.setTag(homeGridBean);

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card) {
            CommonEntity orderBean = (CommonEntity) v.getTag();
            onclick.click(orderBean);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_info;
        ImageView iv_icon;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_info = itemView.findViewById(R.id.tv_info);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            card = itemView.findViewById(R.id.card);
        }
    }


 public    interface Onclick {
        void click(CommonEntity entity);
    }
}
