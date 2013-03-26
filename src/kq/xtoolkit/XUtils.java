package kq.xtoolkit;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class XUtils {
     // flag, default value is true
    public static boolean gBouncerFlag = true;
    public static boolean gLockFlag = true;
    public static boolean gScreenShotFlag = true;
    public static boolean gFlashLightFlag = true;

    public static int getWindowsWidth(Context context) {
        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }


    public static int getWindowsHeight(Context context) {
        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    public static int getStatusBarHeight(Context context) {
        int height = (int)Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return height;
    }

    
}
