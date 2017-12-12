package com.example.phoneshow.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.phoneshow.R;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.bean.ThemesBean;
import com.example.phoneshow.constant.Constants;
import com.example.phoneshow.data.DataManager;
import com.example.phoneshow.phone.CallerOperationManager;

import java.util.List;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class SuspensionView implements View.OnClickListener {
    private Context context;

    private View mView;

    private String number;

    private String name;

    private VideoView videoView;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overlay_answer:
                LogUtils.d("接听");
                Intent intent = new Intent(context, BlackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.startActivity(intent);
//                CallerOperationManager.getInstance().acceptCall(context);
//                removeFlowWindow(context);
                break;
            case R.id.overlay_refuse:
                LogUtils.d("挂断");
                CallerOperationManager.getInstance().endCall(context);
                break;
        }
    }

    private static class SuspensionViewHolder {
        private static SuspensionView instance = new SuspensionView();
    }

    private SuspensionView() {
    }

    public static SuspensionView getInstance() {
        return SuspensionViewHolder.instance;
    }

    /**
     * 创建来电界面
     *
     * @param context
     * @param number
     * @param name
     * @param bytes
     */
    public void showFlowWindow(Context context, String number, String name, byte[] bytes) {
        this.context = context.getApplicationContext();
        this.number = number;
        this.name = name;

        //开启广播监听
        startBroadcast();

        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.activity_overlay, null, false);
        dealCallerInterface(context.getApplicationContext(), mView, number, name, bytes);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.CENTER;
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.addView(mView, params);
    }

    /**
     * 加载来电界面布局参数
     *
     * @param context
     * @param view
     * @param number
     * @param name
     * @param bytes
     */
    private void dealCallerInterface(Context context, View view, String number, String name, byte[] bytes) {
//        RelativeLayout bg = (RelativeLayout) view.findViewById(R.id.overlay_relative);
        List<ThemesBean> themesBeanList = DataManager.getInstance().getSimulatedData(context);
        if (DataManager.getInstance().getUse(context, "item1")) {
            initVideoView(view, themesBeanList.get(0).getUriStr());
        } else if (DataManager.getInstance().getUse(context, "item2")) {
            initVideoView(view, themesBeanList.get(1).getUriStr());
        } else if (DataManager.getInstance().getUse(context, "item3")) {
            initVideoView(view, themesBeanList.get(2).getUriStr());
        }

        ImageView phoneAnswer = (ImageView) view.findViewById(R.id.overlay_answer);
        phoneAnswer.startAnimation(CallerOperationManager.getInstance().animationAnswerImg());
        phoneAnswer.setOnClickListener(this);
        ImageView phoneRefuse = (ImageView) view.findViewById(R.id.overlay_refuse);
        phoneRefuse.setOnClickListener(this);

        TextView phoneNumberTex = (TextView) view.findViewById(R.id.overlay_caller_number);
        if (!TextUtils.isEmpty(number)) {
            phoneNumberTex.setText(number);
        }

        TextView phoneName = (TextView) view.findViewById(R.id.overlay_caller_name);
        if (!TextUtils.isEmpty(name)) {
            phoneName.setText(name);
        }

        ImageView phoneIcon = (ImageView) view.findViewById(R.id.overlay_caller_avatar);
        if (bytes != null) {
            phoneIcon.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        } else {
            LogUtils.d("没有发现头像");
        }
    }

    private void initVideoView(View view, String uriStr) {
        //加载视频资源控件
        videoView = (VideoView) view.findViewById(R.id.video_view);
        //设置播放加载路径
        videoView.setVideoURI(Uri.parse(uriStr));
        //播放
        videoView.start();
        //循环播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    /**
     * 移除来电界面
     *
     * @param context
     */
    public void removeFlowWindow(Context context) {
        if (mView != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(mView);
            mView = null;
        }
    }

    private void startBroadcast() {
        //接收挂断广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_END_CALL);
        context.registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_END_CALL://挂断电话后处理
                    removeFlowWindow(context);
                    Intent i = new Intent(context, CallBackActivity.class);
                    i.putExtra(Constants.EXTRA_PHONE_NUM, number);
                    i.putExtra(Constants.EXTRA_PHONE_NAME, name);
                    i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    break;
            }
        }
    };
}
