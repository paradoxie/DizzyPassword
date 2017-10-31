package cf.paradoxie.dizzypassword.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.List;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.db.AccountBean;
import cf.paradoxie.dizzypassword.db.RxBean;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

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
        TextView mTextTitle, mNum, mTime, mTag1, mTag2, mTag3, mTag4, mTag5, mAccount, mPassword, mNote;
        Button mChange, mDelete;
        RxBean rxEvent;
        ImageView iv_copy;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            mNum = (TextView) view.findViewById(R.id.text_list_card_num);
            mTime = (TextView) view.findViewById(R.id.text_list_card_time);
            mAccount = (TextView) view.findViewById(R.id.tv_account);
            mPassword = (TextView) view.findViewById(R.id.tv_password);
            mNote = (TextView) view.findViewById(R.id.tv_note);
            mChange = (Button) view.findViewById(R.id.bt_change);
            mDelete = (Button) view.findViewById(R.id.bt_delete);
            iv_copy = (ImageView) view.findViewById(R.id.iv_copy);


            mTag1 = (TextView) view.findViewById(R.id.text_list_card_tag1);
            mTag2 = (TextView) view.findViewById(R.id.text_list_card_tag2);
            mTag3 = (TextView) view.findViewById(R.id.text_list_card_tag3);
            mTag4 = (TextView) view.findViewById(R.id.text_list_card_tag4);
            mTag5 = (TextView) view.findViewById(R.id.text_list_card_tag5);
            rxEvent = new RxBean();
            mTextTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //                    rxEvent.setName(mTextTitle.getText().toString().trim());
                    //                    RxBus.getInstance().post(rxEvent);
                }
            });

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
            mTag4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent.setMessage(mTag4.getText().toString().trim());
                    RxBus.getInstance().post(rxEvent);
                }
            });
            mTag5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent.setMessage(mTag5.getText().toString().trim());
                    RxBus.getInstance().post(rxEvent);
                }
            });

        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(Integer data, final int position) {
            final String id = mBeanList.get(position).getObjectId();
            final String time = mBeanList.get(position).getCreatedAt();
            final String name = DesUtil.decrypt(mBeanList.get(position).getName().toString(), SPUtils.getKey());
            final String account = DesUtil.decrypt(mBeanList.get(position).getAccount().toString(), SPUtils.getKey());
            final String password = DesUtil.decrypt(mBeanList.get(position).getPassword().toString(), SPUtils.getKey());
            String note = mBeanList.get(position).getNote().toString();
            final String note1 = note;
            if (note != null) {
                note = DesUtil.decrypt(mBeanList.get(position).getNote().toString(), SPUtils.getKey());
            }
            final List<String> tag = mBeanList.get(position).getTag();


            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(name);
            mNum.setText(mBeanList.size() + "-" + String.valueOf(position + 1));
            mTime.setText(time);
            mAccount.setText("帐号：" + account);
            mPassword.setText("密码：" + password);
            mNote.setText("备注：" + note);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除当前数据
                    AccountBean accountBean = new AccountBean();
                    accountBean.setObjectId(id);
                    accountBean.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                mTextTitle.setText("已删除");
                                mAccount.setText("已删除");
                                mPassword.setText("已删除");
                                mTag1.setVisibility(View.GONE);
                                mTag2.setVisibility(View.GONE);
                                mTag3.setVisibility(View.GONE);
                                mTag4.setVisibility(View.GONE);
                                mTag5.setVisibility(View.GONE);
                                mNote.setText("删除成功，请点击右上角刷新按钮");
                                MyApplication.showToast("删除成功");
                            } else {
                                MyApplication.showToast("删除失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });

                }
            });
            iv_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAccount.getText().length() > 0 || mPassword.getText().length() > 0) {
                        ClipboardManager cm = (ClipboardManager) MyApplication.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(mAccount.getText()+"\n"+mPassword.getText());
                        Snackbar.make(view, R.string.copy, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, R.string.nothing_copy, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

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
            if (tag.size() == 4) {
                mTag1.setText(tag.get(0));
                mTag2.setText(tag.get(1));
                mTag3.setText(tag.get(2));
                mTag3.setText(tag.get(3));
                mTag1.setVisibility(View.VISIBLE);
                mTag2.setVisibility(View.VISIBLE);
                mTag3.setVisibility(View.VISIBLE);
                mTag4.setVisibility(View.VISIBLE);
            }
            if (tag.size() == 5) {
                mTag1.setText(tag.get(0));
                mTag2.setText(tag.get(1));
                mTag3.setText(tag.get(2));
                mTag3.setText(tag.get(3));
                mTag3.setText(tag.get(4));
                mTag1.setVisibility(View.VISIBLE);
                mTag2.setVisibility(View.VISIBLE);
                mTag3.setVisibility(View.VISIBLE);
                mTag4.setVisibility(View.VISIBLE);
                mTag5.setVisibility(View.VISIBLE);
            }
        }
    }

}
