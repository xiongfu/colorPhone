
package com.example.phoneshow.view;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ScreenService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        SuspensionView.getInstance().showFlowWindow(this,"15104554356","fuxiong",new byte[]{});
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this,ScreenService.class));
    }
}
