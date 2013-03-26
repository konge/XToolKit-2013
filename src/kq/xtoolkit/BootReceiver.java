package kq.xtoolkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        boolean isAlive = settings.getBoolean(SettingsActivity.ENABLE_SWITCH, false);
        if (isAlive) {
            // start service
            context.startService(new Intent(context, XToolKitService.class));
        }
    }

}
