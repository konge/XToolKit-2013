
package kq.xtoolkit;

import kq.xtoolkit.XToolKitService.ServiceCallbacks;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class ControlLayout extends FrameLayout {
    private Context mContext;

    private View panel;

    private int mWindowWidth;
    private int mWindowHeight;
    private int mStatusbarHeight;

    private ServiceCallbacks mServiceCallback;


    public ControlLayout(Context context) {
        this(context, null);
    }

    public ControlLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        //init global variable
        mStatusbarHeight = XUtils.getStatusBarHeight(mContext);
        mWindowWidth = XUtils.getWindowsWidth(mContext);
        mWindowHeight = XUtils.getWindowsHeight(mContext);

    }


    public void show() {
        setVisibility(VISIBLE);
        panel.setX(mServiceCallback.getBallXPosition() - mServiceCallback.getBallWidth() / 2);
        panel.setY(mServiceCallback.getBallYPosition() - mServiceCallback.getBallHeight() / 2);
        panel.setAlpha(0.2f);
        panel.animate().alpha(1).scaleX(1.0f).scaleY(1.0f).x((mWindowWidth - panel.getWidth()) / 2)
                .y((mWindowHeight - panel.getHeight()) / 2).setDuration(300).setListener(null);
    }
    

    public void hide() {
        if (panel != null) {
            panel.animate().alpha(0.2f).scaleX(0f).scaleY(0f)
                    .x(mServiceCallback.getBallXPosition() - mServiceCallback.getBallWidth()/2)
                    .y(mServiceCallback.getBallYPosition() - mServiceCallback.getBallHeight()/2)
                    .setDuration(300).setListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (getVisibility() == VISIBLE) {
                                setVisibility(GONE);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO Auto-generated method stub

                        }
                    });
        }
    }

    public void setServiceCallbacks(ServiceCallbacks callback) {
        mServiceCallback = callback;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        panel = findViewById(R.id.panel);
    }
}
