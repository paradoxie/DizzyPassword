package cf.paradoxie.dizzypassword.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.CommonEntity;
import cf.paradoxie.dizzypassword.utils.Utils;

public class BannerAdapter extends BaseBannerAdapter<CommonEntity> {

    private GridMineAdapter.Onclick onclick;

    public BannerAdapter(GridMineAdapter.Onclick onclick) {
        this.onclick = onclick;
    }

    @Override
    protected void bindData(BaseViewHolder<CommonEntity> holder, CommonEntity data, int position, int pageSize) {
        ImageView iv = holder.findViewById(R.id.banner_image);
        TextView tv = holder.findViewById(R.id.tv_info);
        ConstraintLayout cl = holder.findViewById(R.id.cl);
        Utils.loadImg(iv, data.getPicUrl(), false);
        tv.setText(data.getInfo());
        cl.setOnClickListener(v -> {
            onclick.click(data);
        });
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.layout_banner_item;
    }
}
