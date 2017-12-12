
package com.example.phoneshow.phone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.example.phoneshow.bean.PhoneInfoBean;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.constant.Constants;
import com.example.phoneshow.data.DataManager;
import com.example.phoneshow.view.OverLayActivity;
import com.example.phoneshow.view.SuspensionView;

public class PhoneStateReceiver extends BroadcastReceiver {
    private static final String ACTION_UNANSWERED = "com.android.phone.NotificationMgr.MissedCall_intent";

    /**
     * 电话管理
     */
    private TelephonyManager telMgr = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context ctx = context;
        byte[] bytes = null;
        String name = null;

        telMgr = (TelephonyManager) ctx.getSystemService(Service.TELEPHONY_SERVICE);
        switch (telMgr.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
                if (!DataManager.getInstance().getPhoneState(context)){
                    LogUtils.d("来电响铃，但是被设置成关闭状态了");
                    return;
                }

                final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                LogUtils.d("来电接受广播 number = " + number);
                PhoneInfoBean phoneInfoBean = CallerOperationManager.getInstance().queryContactDatabase(context,number);
                if (phoneInfoBean != null){
                    bytes = CallerOperationManager.getInstance().getPhoto(context,phoneInfoBean.getId());
                    name = phoneInfoBean.getName();
                }
                final byte[] finalBytes = bytes;
                final String finalName = name;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        CallerOperationManager.getInstance().wakeUpAndUnlock(ctx);
//                        showActivity(ctx, number,finalName, finalBytes);
//                    }
//                }, 100);
//                showActivity(ctx, number,finalName, finalBytes);
                SuspensionView.getInstance().showFlowWindow(context,number,name,bytes);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话
                LogUtils.d("来电接听广播");
//                CallerOperationManager.getInstance().sendEndCallBroadCast(ctx);
                break;
            case TelephonyManager.CALL_STATE_IDLE:// 挂断电话
                LogUtils.d("来电挂断广播");
                CallerOperationManager.getInstance().sendEndCallBroadCast(ctx);
                break;
            default:
                break;
        }

    }

    /**
     * 显示来电Activity
     *
     * @param ctx
     * @param number
     */
    private void showActivity(Context ctx, String number,String name,byte[] bytes) {
        Intent intent = new Intent(ctx, OverLayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EXTRA_PHONE_NUM, number);
        intent.putExtra(Constants.EXTRA_PHONE_NAME,name);
        intent.putExtra(Constants.EXTRA_PHOTO_BYTES,bytes);
        LogUtils.d("开启来电界面 number = "+number+" name = "+name+" 头像长度 = "+bytes.length);
        ctx.startActivity(intent);
    }
}
