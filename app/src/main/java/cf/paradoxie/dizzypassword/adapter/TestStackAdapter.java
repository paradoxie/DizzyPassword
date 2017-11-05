package cf.paradoxie.dizzypassword.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import java.util.Timer;
import java.util.TimerTask;

import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.activity.AddActivity;
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
        TextView mTextTitle, mNum, mTime, mTime_up, mTag1, mTag2, mTag3, mTag4, mTag5, mAccount, mPassword, mNote;
        Button mChange, mDelete;
        RxBean rxEvent, rxEvent_1;
        ImageView iv_copy;
        private static Boolean isSure = false;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            mNum = (TextView) view.findViewById(R.id.text_list_card_num);
            mTime = (TextView) view.findViewById(R.id.text_list_card_time);
            mTime_up = (TextView) view.findViewById(R.id.text_list_card_up);
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
            rxEvent_1 = new RxBean();

            mTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent_1.setAction("done");
                    RxBus.getInstance().post(rxEvent_1);
                }
            });
            mTime_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rxEvent_1.setAction("done");
                    RxBus.getInstance().post(rxEvent_1);
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
            final String time_up = mBeanList.get(position).getUpdatedAt();
            final String name = DesUtil.decrypt(mBeanList.get(position).getName().toString(), SPUtils.getKey());
            final String account = DesUtil.decrypt(mBeanList.get(position).getAccount().toString(), SPUtils.getKey());
            final String password = DesUtil.decrypt(mBeanList.get(position).getPassword().toString(), SPUtils.getKey());
            String note = mBeanList.get(position).getNote().toString();
            if (note != null) {
                note = DesUtil.decrypt(mBeanList.get(position).getNote().toString(), SPUtils.getKey());
            }
            final List<String> tag = mBeanList.get(position).getTag();


            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(name);
            mNum.setText(String.valueOf(position + 1) + "-" + mBeanList.size());
            mTime.setText(time + "  创建");
            mTime_up.setText(time_up + "  更新");
            mAccount.setText(account);
            mPassword.setText(password);
            mNote.setText(note);

            final String finalNote = note;
            mChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("account", account);
                    bundle.putString("password", password);
                    bundle.putString("finalNote", finalNote);
                    bundle.putString("tag", DesUtil.listToString(tag, " "));
                    bundle.putString("id", id);

                    intent.putExtras(bundle);
                    MyApplication.getContext().startActivity(intent.setClass(MyApplication.getContext(), AddActivity.class));
                }
            });

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //确定删除
                    Timer tExit = null;
                    if (!isSure) {
                        isSure = true;
                        // 准备删除
                        mDelete.setText("2秒内再次点击删除");
                        tExit = new Timer();
                        tExit.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isSure = false;
                            }
                        }, 2000);
                    } else {
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
                                    mNote.setText("本条帐号信息删除成功，请点击右上角刷新按钮");
                                    mDelete.setText("删除成功");
                                    mDelete.setClickable(false);
                                } else {
                                    if (e.getErrorCode() == 101) {
                                        MyApplication.showToast("已经删掉了哦~");
                                    } else {
                                        MyApplication.showToast("删除失败：" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                }
                            }
                        });

                    }

                }
            });
            iv_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAccount.getText().length() > 0 || mPassword.getText().length() > 0) {
                        ClipboardManager cm = (ClipboardManager) MyApplication.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(mAccount.getText() + "\n" + mPassword.getText());
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
                mTag4.setText(tag.get(3));
                mTag1.setVisibility(View.VISIBLE);
                mTag2.setVisibility(View.VISIBLE);
                mTag3.setVisibility(View.VISIBLE);
                mTag4.setVisibility(View.VISIBLE);
            }
            if (tag.size() == 5) {
                mTag1.setText(tag.get(0));
                mTag2.setText(tag.get(1));
                mTag3.setText(tag.get(2));
                mTag4.setText(tag.get(3));
                mTag5.setText(tag.get(4));
                mTag1.setVisibility(View.VISIBLE);
                mTag2.setVisibility(View.VISIBLE);
                mTag3.setVisibility(View.VISIBLE);
                mTag4.setVisibility(View.VISIBLE);
                mTag5.setVisibility(View.VISIBLE);
            }
        }
    }

}
