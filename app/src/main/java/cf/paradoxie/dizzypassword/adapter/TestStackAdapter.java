package cf.paradoxie.dizzypassword.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.List;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;

public class TestStackAdapter extends StackAdapter<Integer> {
    private static List<AccountBean> mBeanList;

    public TestStackAdapter(Context context, List list) {
        super(context);
        this.mBeanList = list;
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle, mTag1, mTag2, mTag3, mAccount, mPassword;
        Button mChange, mDelete;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            mAccount = (TextView) view.findViewById(R.id.tv_account);
            mPassword = (TextView) view.findViewById(R.id.tv_password);
            mChange = (Button) view.findViewById(R.id.bt_change);
            mDelete = (Button) view.findViewById(R.id.bt_delete);

            mTag1 = (TextView) view.findViewById(R.id.text_list_card_tag1);
            mTag2 = (TextView) view.findViewById(R.id.text_list_card_tag2);
            mTag3 = (TextView) view.findViewById(R.id.text_list_card_tag3);
            mTextTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    MyApplication.showToast("别动啊");
                }
            });
            final RxBean rxEvent = new RxBean();
            mTag1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent.setMessage(mTag1.getText().toString().trim());
                    RxBus.getInstance().post(rxEvent);
                }
            });
            mTag2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent.setMessage(mTag2.getText().toString().trim());
                    RxBus.getInstance().post(rxEvent);
                }
            });
            mTag3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent.setMessage(mTag3.getText().toString().trim());
                    RxBus.getInstance().post(rxEvent);
                }
            });
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(Integer data, int position) {
            String name = DesUtil.decrypt(mBeanList.get(position).getName().toString(), SPUtils.getKey());
            String account = DesUtil.decrypt(mBeanList.get(position).getAccount().toString(), SPUtils.getKey());
            String password = DesUtil.decrypt(mBeanList.get(position).getPassword().toString(), SPUtils.getKey());
            List<String> tag = mBeanList.get(position).getTag();


            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(String.valueOf(position + 1) + "：" + name);
            mAccount.setText("帐号：" + account);
            mPassword.setText("密码：" + password);

                if (tag.size() == 0) {
                    return;
                }
                if (tag.size() == 1) {
                    mTag1.setText(tag.get(0));
                    mTag1.setVisibility(View.VISIBLE);
                }
                if (tag.size() == 2) {
                    mTag1.setText(tag.get(0));
                    mTag2.setText(tag.get(1));
                    mTag1.setVisibility(View.VISIBLE);
                    mTag2.setVisibility(View.VISIBLE);
                }
                if (tag.size() == 3) {
                    mTag1.setText(tag.get(0));
                    mTag2.setText(tag.get(1));
                    mTag3.setText(tag.get(2));
                    mTag1.setVisibility(View.VISIBLE);
                    mTag2.setVisibility(View.VISIBLE);
                    mTag3.setVisibility(View.VISIBLE);
                }
        }
    }

}
