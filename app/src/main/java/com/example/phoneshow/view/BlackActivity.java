package com.example.phoneshow.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.example.phoneshow.BaseActivity;
import com.example.phoneshow.R;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.constant.Constants;
import com.example.phoneshow.phone.CallerOperationManager;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class BlackActivity extends BaseActivity {
    @Override
    public void initData() {
        super.initData();
        LogUtils.d("启动空白activity接听来电");
        CallerOperationManager.getInstance().acceptCall(this);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.ACTION_FINISH);
//        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_black_interface);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SuspensionView.getInstance().removeFlowWindow(BlackActivity.this);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (broadcastReceiver != null) {
//            unregisterReceiver(broadcastReceiver);
//        }
    }

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (intent.getAction()) {
//                case Constants.ACTION_FINISH://挂断电话后处理
//                    LogUtils.d("接收到关闭广播");
////                    SuspensionView.getInstance().removeFlowWindow(BlackActivity.this);
////                    finish();
//                    break;
//            }
//        }
//    };
}
