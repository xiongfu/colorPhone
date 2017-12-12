package com.example.phoneshow.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phoneshow.BaseActivity;
import com.example.phoneshow.R;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.bean.ThemesBean;
import com.example.phoneshow.constant.Constants;
import com.example.phoneshow.data.DataManager;
import com.example.phoneshow.phone.CallerOperationManager;

import java.util.List;

/**
 * Created by Administrator on 2017/12/9 0009.
 */

public class OverLayActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mPhoneAnswer;

    private ImageView mPhoneRefuse;

    private RelativeLayout mRelative;

    /**
     * 电话号码
     */
    private String mPhoneNumber;

    /**
     * 姓名
     */
    private String mName;

    /**
     * 头像
     */
    private byte[] bytes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //遮住虚拟键
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    public void initData() {
        super.initData();
        //获取来电号码
        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra(Constants.EXTRA_PHONE_NUM);
        mName = intent.getStringExtra(Constants.EXTRA_PHONE_NAME);
        bytes = intent.getByteArrayExtra(Constants.EXTRA_PHOTO_BYTES);

        //接收挂断广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_END_CALL);
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_overlay);
        mRelative = (RelativeLayout) findViewById(R.id.overlay_relative);
        List<ThemesBean> themesBeanList = DataManager.getInstance().getSimulatedData(this);
//        if (DataManager.getInstance().getUse(this,"item1")){
//            mRelative.setBackground(new BitmapDrawable(themesBeanList.get(0).getBg()));
//        } else if (DataManager.getInstance().getUse(this,"item2")){
//            mRelative.setBackground(new BitmapDrawable(themesBeanList.get(1).getBg()));
//        } else if (DataManager.getInstance().getUse(this,"item3")){
//            mRelative.setBackground(new BitmapDrawable(themesBeanList.get(2).getBg()));
//        }

        mPhoneAnswer = (ImageView) findViewById(R.id.overlay_answer);
        mPhoneAnswer.setOnClickListener(this);
        mPhoneRefuse = (ImageView) findViewById(R.id.overlay_refuse);
        mPhoneRefuse.setOnClickListener(this);

        TextView phoneNumberTex = (TextView) findViewById(R.id.overlay_caller_number);
        if (!TextUtils.isEmpty(mPhoneNumber)) {
            phoneNumberTex.setText(mPhoneNumber);
        }

        TextView phoneName = (TextView) findViewById(R.id.overlay_caller_name);
        if (!TextUtils.isEmpty(mName)){
            phoneName.setText(mName);
        }

        ImageView phoneIcon = (ImageView) findViewById(R.id.overlay_caller_avatar);
        if (bytes != null) {
            phoneIcon.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        } else {
            LogUtils.d("没有发现头像");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overlay_answer://接听
                CallerOperationManager.getInstance().acceptCall(this);
                finish();
                break;
            case R.id.overlay_refuse://挂断
                CallerOperationManager.getInstance().endCall(this);
                break;
            default:
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Constants.ACTION_END_CALL://挂断电话后处理
                    Intent i = new Intent(context,CallBackActivity.class);
                    i.putExtra(Constants.EXTRA_PHONE_NUM,mPhoneNumber);
                    i.putExtra(Constants.EXTRA_PHONE_NAME,mName);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                    break;
            }
        }
    };
}
