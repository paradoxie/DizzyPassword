package cf.paradoxie.dizzypassword.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.List;

import cf.paradoxie.dizzypassword.AppManager;
import cf.paradoxie.dizzypassword.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.activity.AddActivity;
import cf.paradoxie.dizzypassword.bean.AccountBean;
import cf.paradoxie.dizzypassword.bean.RxBean;
import cf.paradoxie.dizzypassword.utils.DataUtils;
import cf.paradoxie.dizzypassword.utils.DesUtil;
import cf.paradoxie.dizzypassword.utils.MyToast;
import cf.paradoxie.dizzypassword.utils.RxBus;
import cf.paradoxie.dizzypassword.utils.SPUtils;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.view.SingleLineTextView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class TestStackAdapter extends StackAdapter<Integer> {
    private static List<AccountBean> mBeanList;
    private static Context mContext;

    public TestStackAdapter(Context context, List list) {
        super(context);
        mContext = context;
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
        SingleLineTextView mAccount, mPassword;
        TextView mTextTitle, mNum, mTime, mTime_up, mTag1, mTag2, mTag3, mTag4, mTag5, mPwdvisible, mNote, mWeb;
        ImageView mChange, mDelete;
        RxBean rxEvent, rxEvent_1, rxEvent_2;
        ImageView iv_copy;
        LinearLayout ll_notice;
        private SweetAlertDialog pDialog = null;

        public ColorItemViewHolder(View view) {
            super(view);

            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            ll_notice = view.findViewById(R.id.ll_notice);
            mTextTitle = view.findViewById(R.id.text_list_card_title);
            mNum = view.findViewById(R.id.text_list_card_num);
            mTime = view.findViewById(R.id.text_list_card_time);
            mTime_up = view.findViewById(R.id.text_list_card_up);
            mAccount = view.findViewById(R.id.tv_account);
            mPassword = view.findViewById(R.id.tv_password);
            mPwdvisible = view.findViewById(R.id.tv_password_visible);
            mNote = view.findViewById(R.id.tv_note);
            mWeb = view.findViewById(R.id.tv_web);
            mChange = view.findViewById(R.id.iv_change);
            mDelete = view.findViewById(R.id.iv_delete);
            iv_copy = view.findViewById(R.id.iv_copy);


            mTag1 = view.findViewById(R.id.text_list_card_tag1);
            mTag2 = view.findViewById(R.id.text_list_card_tag2);
            mTag3 = view.findViewById(R.id.text_list_card_tag3);
            mTag4 = view.findViewById(R.id.text_list_card_tag4);
            mTag5 = view.findViewById(R.id.text_list_card_tag5);
            rxEvent = new RxBean();
            rxEvent_1 = new RxBean();
            rxEvent_2 = new RxBean();


            mTag1.setOnClickListener(view1 -> {
                rxEvent.setMessage(mTag1.getText().toString().trim());
                RxBus.getInstance().post(rxEvent);
            });
            mTag2.setOnClickListener(view12 -> {
                rxEvent.setMessage(mTag2.getText().toString().trim());
                RxBus.getInstance().post(rxEvent);
            });
            mTag3.setOnClickListener(view13 -> {
                rxEvent.setMessage(mTag3.getText().toString().trim());
                RxBus.getInstance().post(rxEvent);
            });
            mTag4.setOnClickListener(view14 -> {
                rxEvent.setMessage(mTag4.getText().toString().trim());
                RxBus.getInstance().post(rxEvent);
            });
            mTag5.setOnClickListener(view15 -> {
                rxEvent.setMessage(mTag5.getText().toString().trim());
                RxBus.getInstance().post(rxEvent);
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
            final String name = DesUtil.decrypt(mBeanList.get(position).getName(), SPUtils.getKey());
            final String account = DesUtil.decrypt(mBeanList.get(position).getAccount(), SPUtils.getKey());
            final String password = DesUtil.decrypt(mBeanList.get(position).getPassword(), SPUtils.getKey());
            String note = mBeanList.get(position).getNote();
            if (note != null) {
                note = DesUtil.decrypt(mBeanList.get(position).getNote(), SPUtils.getKey());
            }
            final List<String> tag = mBeanList.get(position).getTag();


            boolean shouldChange = DataUtils.shouldChange(time_up);
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(name);
            mNum.setText(String.valueOf(position + 1) + "-" + mBeanList.size());
            if (shouldChange) {
                mNum.setTextColor(mContext.getResources().getColor(R.color.red600));
                ll_notice.setVisibility(View.VISIBLE);
            } else {
                ll_notice.setVisibility(View.GONE);
            }
            mTime.setText(time + "  创建");
            mTime_up.setText(time_up + "  更新");
            mAccount.setText(account);
            mPassword.setText("**********");
            mNote.setText(note);
            String website;
            //网址记录
            if (mBeanList.get(position).getWebsite() == null) {
                mWeb.setText("");
            } else {
                website = DesUtil.decrypt(mBeanList.get(position).getWebsite(), SPUtils.getKey());
                mWeb.setText(website);
                mWeb.setOnClickListener(view -> {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://" + mWeb.getText().toString());
                    intent.setData(content_url);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在非activity中调用intent必须设置，不然部分手机崩溃
                    MyApplication.getContext().startActivity(intent);
                });
            }

            mPwdvisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.first_check == 0) {
                        MyToast.show(mContext, "请先点击右下角解锁操作权限", ThemeUtils.getPrimaryColor(mContext));
                    } else {
                        if (mPassword.getText().equals("**********")) {
                            mPassword.setText(password);
                            setDrawableLeft(R.drawable.password_open);
                            iv_copy.setVisibility(View.VISIBLE);
                        } else {
                            setDrawableLeft(R.drawable.password);
                            mPassword.setText("**********");
                            iv_copy.setVisibility(View.GONE);
                        }
                    }
                }
            });

            mAccount.setOnLongClickListener(view -> {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mAccount.getText());
                MyApplication.showSnack(view, R.string.copy_account, ThemeUtils.getPrimaryColor(mContext));
                return false;
            });
            mPassword.setOnLongClickListener(view -> {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mPassword.getText());
                MyApplication.showSnack(view, R.string.copy_password, ThemeUtils.getPrimaryColor(mContext));
                return false;
            });


            mNote.setOnLongClickListener(view -> {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mNote.getText());
                MyApplication.showSnack(view, R.string.copy_note, ThemeUtils.getPrimaryColor(mContext));
                return false;
            });

            mWeb.setOnLongClickListener(view -> {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mWeb.getText());
                MyApplication.showSnack(view, R.string.copy_web, ThemeUtils.getPrimaryColor(mContext));
                return false;
            });

            final String finalNote = note;
            final String finalWeb = mWeb.getText().toString();

            mChange.setOnClickListener(view -> {
                if (MyApplication.first_check == 0) {
                    MyToast.show(mContext, "请先点击右下角解锁操作权限", ThemeUtils.getPrimaryColor(AppManager.getAppManager().currentActivity()));
                } else {
                    changeDate(name, account, password, finalWeb, finalNote, tag, id);
                }
            });

            mDelete.setOnClickListener(view -> {
                if (MyApplication.first_check == 0) {
                    MyToast.show(mContext, "请先点击右下角解锁操作权限"
                            , ThemeUtils.getPrimaryColor(mContext));

                } else {
                    showDelete(id, account, password);
                }
            });

            iv_copy.setOnClickListener(view -> {
                if (mAccount.getText().length() > 0 || mPassword.getText().length() > 0) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(mAccount.getText() + "\n" + mPassword.getText());
                    MyApplication.showSnack(view, R.string.copy, ThemeUtils.getPrimaryColor(mContext));
                } else {
                    MyApplication.showSnack(view, R.string.nothing_copy, ThemeUtils.getPrimaryColor(mContext));
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

                showTag(true);
                mTag4.setVisibility(View.GONE);
                mTag5.setVisibility(View.GONE);
            }
            if (tag.size() == 4) {
                mTag1.setText(tag.get(0));
                mTag2.setText(tag.get(1));
                mTag3.setText(tag.get(2));
                mTag4.setText(tag.get(3));

                showTag(true);
                mTag5.setVisibility(View.GONE);
            }
            if (tag.size() == 5) {
                mTag1.setText(tag.get(0));
                mTag2.setText(tag.get(1));
                mTag3.setText(tag.get(2));
                mTag4.setText(tag.get(3));
                mTag5.setText(tag.get(4));
                showTag(true);
            }

        }


        private void changeDate(String name, String account, String password, String web, String finalNote, List<String> tag, String id) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("account", account);
            bundle.putString("password", password);
            bundle.putString("web", web);
            bundle.putString("finalNote", finalNote);
            bundle.putString("tag", DesUtil.listToString(tag, " "));
            bundle.putString("id", id);

            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在非activity中调用intent必须设置，不然部分手机崩溃
            MyApplication.getContext().startActivity(intent.setClass(MyApplication.getContext(), AddActivity.class));
        }

        private void showDelete(final String id, String account, String password) {
            pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("删除此条帐号信息")
                    .setContentText(
                            "帐号:" + account + "\n密码:" + password +
                                    "\n\n您确定要删除么😟")
                    .setConfirmText("确定")
                    .setConfirmClickListener(sDialog -> {
                        deleteDate(id);
                        sDialog.cancel();
                    })
                    .setCancelText("不删了")
                    .setCancelClickListener(sDialog -> sDialog.cancel())
                    .show();

        }

        private void deleteDate(String id) {
            pDialog.show();
            //删除当前数据
            AccountBean accountBean = new AccountBean();
            accountBean.setObjectId(id);
            accountBean.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mTextTitle.setText("本条帐号信息删除成功，请点击右上角刷新按钮");
                        mAccount.setText("已删除");
                        mPassword.setText("已删除");
                        showTag(false);
                        mTime_up.setVisibility(View.GONE);
                        mTime.setVisibility(View.GONE);
                        mNote.setText("已删除");
                        mDelete.setClickable(false);
                        mChange.setClickable(false);
                        mPwdvisible.setClickable(false);
                        setDrawableLeft(R.drawable.password);
                        iv_copy.setVisibility(View.GONE);
                    } else {
                        if (e.getErrorCode() == 101) {
                            MyApplication.showToast("已经删掉了哦~");
                        } else {
                            MyApplication.showToast("删除失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                    pDialog.dismiss();
                }
            });
        }


        /**
         * 设置密码左边的图片显示
         *
         * @param id
         */
        private void setDrawableLeft(int id) {
            Drawable drawableLeft = MyApplication.getContext().getResources().getDrawable(id);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            mPwdvisible.setCompoundDrawables(drawableLeft, null, null, null);
        }

        private void showTag(Boolean b) {
            if (b) {
                mTag1.setVisibility(View.VISIBLE);
                mTag2.setVisibility(View.VISIBLE);
                mTag3.setVisibility(View.VISIBLE);
                mTag4.setVisibility(View.VISIBLE);
                mTag5.setVisibility(View.VISIBLE);
            } else {
                mTag1.setVisibility(View.GONE);
                mTag2.setVisibility(View.GONE);
                mTag3.setVisibility(View.GONE);
                mTag4.setVisibility(View.GONE);
                mTag5.setVisibility(View.GONE);
            }
        }
    }

}
