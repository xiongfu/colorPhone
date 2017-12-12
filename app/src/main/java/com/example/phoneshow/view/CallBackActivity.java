package com.example.phoneshow.view;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.example.phoneshow.BaseActivity;
import com.example.phoneshow.R;
import com.example.phoneshow.constant.Constants;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class CallBackActivity extends BaseActivity implements View.OnClickListener{
    private TextView mSure;

    private TextView mCancel;

    private TextView mPeople;

    private String number;

    private String name;

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        number = intent.getStringExtra(Constants.EXTRA_PHONE_NUM);
        name = intent.getStringExtra(Constants.EXTRA_PHONE_NAME);
    }

    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_dialog_callback);
        mSure = (TextView)findViewById(R.id.call_dialog_sure);
        mSure.setOnClickListener(this);
        mCancel = (TextView) findViewById(R.id.call_dialog_cancel);
        mCancel.setOnClickListener(this);
        mPeople = (TextView) findViewById(R.id.call_dialog_people);
        mPeople.setText("来自"+name+"("+number+")的电话");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.call_dialog_sure:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.call_dialog_cancel:
                finish();
                break;
        }
    }
}
