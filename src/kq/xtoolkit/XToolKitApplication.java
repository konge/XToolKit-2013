
package kq.xtoolkit;

import android.app.Application;
import android.view.WindowManager;

public class XToolKitApplication extends Application {

    private WindowManager.LayoutParams ballParams;

    private WindowManager.LayoutParams panelParams;

    public XToolKitApplication() {
        ballParams = new WindowManager.LayoutParams();
        panelParams = new WindowManager.LayoutParams();
    }

    public WindowManager.LayoutParams getBallParams() {

        return ballParams;

    }

    public WindowManager.LayoutParams getPanelParams() {

        return panelParams;

    }

}
