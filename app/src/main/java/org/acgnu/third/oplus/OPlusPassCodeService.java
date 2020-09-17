package org.acgnu.third.oplus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class OPlusPassCodeService extends Service {
    public OPlusPassCodeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "hahah", Toast.LENGTH_SHORT);
    }
}
