package com.example.phoneshow.phone;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.android.internal.telephony.ITelephony;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.bean.PhoneInfoBean;
import com.example.phoneshow.constant.Constants;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/12/9 0009.
 */

public class CallerOperationManager {

    private static class PhoneAnswerCallHolder {
        private static CallerOperationManager instance = new CallerOperationManager();
    }

    private CallerOperationManager() {
    }

    public static CallerOperationManager getInstance() {
        return PhoneAnswerCallHolder.instance;
    }

    /**
     * 挂断电话
     */
    public void endCall(Context context) {
        TelephonyManager mTelMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            LogUtils.d("End call.");
            iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelMgr, (Object[]) null);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("Fail to answer ring call.");
        }
    }

    /**
     * 接听电话
     */
    public void acceptCall(Context context) {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (Exception e) {
            LogUtils.e("for version 4.1 or larger");
            acceptCall_4_1(context);
        }
    }

    /**
     * 4.1版本以上接听电话
     */
    private void acceptCall_4_1(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //模拟无线耳机的按键来接听电话
        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = "HTC".equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();
        if (broadcastConnected) {
            broadcastHeadsetConnected(context, false);
        }
        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                context.sendOrderedBroadcast(btnDown, enforcedPerm);
                context.sendOrderedBroadcast(btnUp, enforcedPerm);
            }finally {
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(context, false);
            }
        }
    }

    private void broadcastHeadsetConnected(Context context, boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            context.sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        } finally {
        }
    }

    /**
     * 发送挂断电话的广播
     *
     * @param ctx 上下文对象
     */
    public void sendEndCallBroadCast(Context ctx) {
        Intent i = new Intent();
        i.setAction(Constants.ACTION_END_CALL);
        ctx.sendBroadcast(i);
    }

    /**
     * 发送关闭空白界面广播
     *
     * @param ctx 上下文对象
     */
    private void sendCloseBroadCast(Context ctx) {
        LogUtils.d("发送空白界面广播");
        Intent i = new Intent();
        i.setAction(Constants.ACTION_FINISH);
        ctx.sendBroadcast(i);
    }

    /**
     * 查询联系人数据库，比对传入的号码，获取其他信息
     * @param context
     * @param phoneNum
     * @return
     */
    public PhoneInfoBean queryContactDatabase(Context context, String phoneNum) {
        PhoneInfoBean infoBean = new PhoneInfoBean();
        final ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, new String[]{"_id"}, null, null, null);
        LogUtils.d("查询到的联系人列表总数 getCount = " + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);//获取 id 所在列的索引
                    String contactId = cursor.getString(contactIdIndex);//联系人id
                    String number = getPhoneInfo(contentResolver, contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).replace(" ", "");
                    LogUtils.d("查询到的联系人列表 number = " + number);
                    if (!TextUtils.isEmpty(number) && number.contains(phoneNum)) {
                        String name = getPhoneInfo(contentResolver, contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                        if (name.endsWith("_")) {
                            name = name.substring(0, name.length() - 1);
                        }
                        LogUtils.d("找到来电联系人 number = " + number + " name = " + name + " id = " + contactId);
                        infoBean.setId(contactId);
                        infoBean.setName(name);
                        infoBean.setPhoneNum(phoneNum);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return infoBean;
    }

    /**
     * 根据MIMETYPE类型, 返回对应联系人的data1字段的数据
     */
    private String getPhoneInfo(final ContentResolver contentResolver, String contactId, final String mimeType) {
        StringBuilder stringBuilder = new StringBuilder();

        Cursor dataCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.DATA1},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "='" + mimeType + "'",
                new String[]{String.valueOf(contactId)}, null);
        if (dataCursor != null && dataCursor.getCount() > 0) {
            if (dataCursor.moveToFirst()) {
                do {
                    stringBuilder.append(dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA1)));
                    stringBuilder.append("_");//多个值,之间的分隔符.可以自定义;
                } while (dataCursor.moveToNext());
            }
            dataCursor.close();
        }

        return stringBuilder.toString();
    }

    /**
     * 获取联系人的图片
     */
    public byte[] getPhoto(Context context, String contactId) {
        LogUtils.d("查询头像的id为 = " + contactId);
        byte[] bytes = null;
        Cursor dataCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{"data15"},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                new String[]{String.valueOf(contactId)}, null);
        LogUtils.d("获取头像成功1");
        if (dataCursor != null) {
            LogUtils.d("获取头像成功2 " + dataCursor.getCount());
            if (dataCursor.getCount() > 0) {
                dataCursor.moveToFirst();
                bytes = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));
                LogUtils.d("获取头像成功" + bytes.length);
            }
            dataCursor.close();
        }
        return bytes;
    }

    /**
     * 唤醒屏幕并解锁权限
     */
    @SuppressWarnings("deprecation")
    public void wakeUpAndUnlock(Context context) {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        // 获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        // 点亮屏幕
        wl.acquire();
        // 释放
        wl.release();
        // 得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        // 解锁
        kl.disableKeyguard();
    }

    /**
     * 接听动画
     * @return
     */
    public TranslateAnimation animationAnswerImg(){
        TranslateAnimation animation = new TranslateAnimation(-5, 0, -40, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(900);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    /**
     * 获取第一帧图片
     * @param context
     * @param uriStr
     * @return
     */
    public Bitmap cacheThemeBg(Context context,String uriStr){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri uri = Uri.parse(uriStr);
        mmr.setDataSource(context,uri);
        return mmr.getFrameAtTime();
    }
}
