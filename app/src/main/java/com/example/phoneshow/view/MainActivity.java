package com.example.phoneshow.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.phoneshow.BaseActivity;
import com.example.phoneshow.R;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.Utils.ToastUtils;
import com.example.phoneshow.adapter.ThemesRecyclerViewAdapter;
import com.example.phoneshow.bean.ThemesBean;
import com.example.phoneshow.data.DataManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 000001;

    private ImageView mSetting;

    private RecyclerView mRecyclerView;

    private List<ThemesBean> mThemesList = new ArrayList<>();

    private ThemesRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        super.initData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //申请授权，第一个参数为要申请用户授权的权限；第二个参数为requestCode 必须大于等于0，主要用于回调的时候检测，匹配特定的onRequestPermissionsResult。
            //可以从方法名requestPermissions以及第二个参数看出，是支持一次性申请多个权限的，系统会通过对话框逐一询问用户是否授权。
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.PROCESS_OUTGOING_CALLS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        askForPermission();

        mThemesList = DataManager.getInstance().getSimulatedData(this);
    }

    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_main);
        mSetting = (ImageView) findViewById(R.id.main_setting);
        mSetting.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        LogUtils.d("主题数量 ："+mThemesList.size());
        mAdapter = new ThemesRecyclerViewAdapter(this,mThemesList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initSlidingMenu(){
        SlidingMenu slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_setting:
                if (DataManager.getInstance().getPhoneState(this)){
                    DataManager.getInstance().savePhoneState(this,false);
                    ToastUtils.showShort(this,"关闭通话助手功能");
                } else {
                    DataManager.getInstance().savePhoneState(this,true);
                    ToastUtils.showShort(this,"打开通话助手功能");
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 6.0之后申请悬浮窗权限
     */
    private void askForPermission(){
        LogUtils.d("sdk = "+Build.VERSION.SDK_INT+" m = "+Build.VERSION_CODES.M);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                startService(new Intent(this,ScreenService.class));
            }
        }else{
            startService(new Intent(this,ScreenService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    ToastUtils.showShort(this,"授权失败，无法开启悬浮窗");
                } else {
                    ToastUtils.showShort(this,"权限授予成功");
                    startService(new Intent(this,ScreenService.class));
                }
            }

        }
    }
}
