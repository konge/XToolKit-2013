
package kq.xtoolkit;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.BounceInterpolator;

/*
 * create this service to handle view's entire life
 */
public class XToolKitService extends Service {
    public final static boolean DEBUG = true;
    private final static String TAG = "XToolKitService";
    private WindowManager wm = null;

    private Context mContext;

    private View ballView;
    private ControlLayout controlLayout;
    //variable
    private int mStatusbarHeight;
    //state
    private static final int STATE_ATTACTHED = 0;
    private static final int STATE_ACTIVE = 1;
    private static final int STATE_INACTIVE = 2;
    private int mViewState = STATE_ATTACTHED;
    // handler constant
    private static final int MSG_START_BOUNCER_ANIMATION = 1;
    public ServiceCallbacks mCallback = new ServiceCallbacks();
    class  ServiceCallbacks {
        public void handleMessage(android.os.Message msg) {};

        public int getBallXPosition() {
            WindowManager.LayoutParams wmParams = ((XToolKitApplication)getApplication())
            .getBallParams();
            return wmParams.x;
        }

        public int getBallYPosition() {
            WindowManager.LayoutParams wmParams = ((XToolKitApplication)getApplication())
            .getBallParams();
            return wmParams.y;
        }

        public int getBallWidth() {
            int width = 0;
            if (ballView != null) {
                width = ballView.getWidth();
            }
            return width;
        }

        public int getBallHeight() {
            int height = 0;
            if (ballView != null) {
                height = ballView.getHeight();
            }
            return height;
        }
    };


    private Runnable animRunnable = new Runnable() {

        @Override
        public void run() {
            mHandler.sendEmptyMessageDelayed(MSG_START_BOUNCER_ANIMATION, 2000);
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_START_BOUNCER_ANIMATION:
                    if (mViewState == STATE_INACTIVE) {
                        startBouncerAnimation();
                    }
                    break;
            }
        };
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        // init global variable
        mStatusbarHeight = XUtils.getStatusBarHeight(mContext);
        wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // init ball view
        ballView = LayoutInflater.from(mContext).inflate(R.layout.ball_view, null, false);
        controlLayout = (ControlLayout)LayoutInflater.from(mContext).inflate(R.layout.control_layout, null, false);
        controlLayout.setServiceCallbacks(mCallback);
        ballView.setOnTouchListener(new OnTouchListener() {
            private float lastX = 0;
            private float lastY = 0;
            private long downTime = 0;
            private long upTime = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        downTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setViewState(STATE_ACTIVE);
                        float newX = event.getRawX();
                        float newY = event.getRawY();
                        float dx = newX - lastX;
                        float dy = newY - lastY;
                        if (Math.abs(dx * dx + dy * dy) > 4) {
                            updateBallViewPosition(newX, newY);
                        }
                        lastX = newX;
                        lastY = newY;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        upTime = System.currentTimeMillis();
                        long duration = Math.abs(downTime - upTime);
                        if (duration < 130) {
                            // show panel view
                            if (controlLayout.getVisibility() == View.GONE) {
                                controlLayout.show();
                                setViewState(STATE_ACTIVE);
                            } else {
                                controlLayout.hide();
                                setViewState(STATE_INACTIVE);
                                mHandler.post(animRunnable);
                            }
                        }
                        break;
                }
                return true;
            }
        });

        // must attach panel to window before ball
        attachPanelViewToWindow();
        attachBallViewToWindow();
    }

    @Override
    public void onDestroy() {
        if (ballView != null) {
            wm.removeView(ballView);
        }
        if (controlLayout != null){
            wm.removeView(controlLayout);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // init global flag from preference
        initPrefFlag();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void setViewState(int state) {
        mViewState = state;
    }

    // get values from preference file and init global variable
    private void initPrefFlag() {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        XUtils.gBouncerFlag = settings.getBoolean(SettingsActivity.BOUNCER_ANIMATION, true);
        XUtils.gLockFlag = settings.getBoolean(SettingsActivity.LOCK_SHORTCUT, true);
        XUtils.gScreenShotFlag = settings.getBoolean(SettingsActivity.SCREENSHOT_SHORTCUT, true);
        XUtils.gFlashLightFlag = settings.getBoolean(SettingsActivity.FLASHLIGHT_SHORTCUT, true);
    }

    private void attachBallViewToWindow() {
        WindowManager.LayoutParams wmParams = ((XToolKitApplication)getApplication())
                .getBallParams();
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        wmParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wm.addView(ballView, wmParams);
    }

    private void attachPanelViewToWindow() {
        WindowManager.LayoutParams wmParams = ((XToolKitApplication)getApplication())
                .getPanelParams();
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wmParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        wm.addView(controlLayout, wmParams);
    }

    private void updateBallViewPosition(float x, float y) {
        WindowManager.LayoutParams wmParams = ((XToolKitApplication)getApplication())
                .getBallParams();
        
        float leftOffset = ballView.getWidth()/2;
        float rightOffset = XUtils.getWindowsWidth(mContext) - leftOffset;
        float upOffset = ballView.getHeight()/2;
        float downOffset = XUtils.getWindowsHeight(mContext) - upOffset;
        if (x >= leftOffset && x <= rightOffset) {
            wmParams.x = (int)Math.ceil(x - leftOffset);
        }
        if (y >= upOffset && y <= downOffset) {
            wmParams.y = (int)Math.ceil(y - upOffset - mStatusbarHeight);
        }
        wm.updateViewLayout(ballView, wmParams);
    }

    private void startBouncerAnimation() {
        if (!XUtils.gBouncerFlag) {
            return;
        }
        WindowManager.LayoutParams wmParams = ((XToolKitApplication)getApplication())
                .getBallParams();
        int endY = XUtils.getWindowsHeight(mContext) - ballView.getHeight()/2;
        ValueAnimator animation = ValueAnimator
                .ofFloat(wmParams.y + ballView.getHeight()/2, endY);
        animation.setInterpolator(new BounceInterpolator());
        animation.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String value = ((ValueAnimator)animation).getAnimatedValue().toString();
                updateBallViewPosition(0, Float.parseFloat(value));
            }
        });
        animation.setDuration(1000);
        animation.start();
    }
}
