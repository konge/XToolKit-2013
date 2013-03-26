
package kq.xtoolkit;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class PanelView extends View {
    private ArrayList<TargetDrawable> mTargetDrawables = new ArrayList<TargetDrawable>();
    private TargetDrawable mLeftDrawable;
    private TargetDrawable mRightDrawable;
    private TargetDrawable mTopDrawable;
    private TargetDrawable mBottomDrawable;

    private float mCenterX;
    private float mCenterY;

    private Drawable mPanel;
    public PanelView(Context context) {
        this(context, null);
    }

    public PanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPanel = getResources().getDrawable(R.drawable.panel);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PanelView);
        TypedValue outValue = new TypedValue();
        // Read array of target Drawable
        if (a.getValue(R.styleable.PanelView_targetDrawables, outValue)) {
            mTargetDrawables = loadDrawableArray(outValue.resourceId);
        }

        mLeftDrawable = mTargetDrawables.get(0);
        mRightDrawable = mTargetDrawables.get(1);
        mTopDrawable = mTargetDrawables.get(2);
        mBottomDrawable = mTargetDrawables.get(3);
        // set background
        setBackgroundDrawable(mPanel);
    }


    private ArrayList<TargetDrawable> loadDrawableArray(int resourceId) {
        Resources res = getContext().getResources();
        TypedArray array = res.obtainTypedArray(resourceId);
        final int count = array.length();
        ArrayList<TargetDrawable> drawables = new ArrayList<TargetDrawable>(count);
        for (int i = 0; i < count; i++) {
            TypedValue value = array.peekValue(i);
            TargetDrawable target = new TargetDrawable(res, value != null ? value.resourceId : 0);
            drawables.add(target);
        }
        array.recycle();
        return drawables;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mPanel != null) {
            int computedWidth = mPanel.getIntrinsicWidth();
            int computedHeight = mPanel.getIntrinsicHeight();
            setMeasuredDimension(computedWidth, computedHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int width = right - left;
        final int height = bottom - top;
        int panelWidth = 0;
        int panelHeight = 0;
        if (mPanel != null) {
            panelWidth = mPanel.getIntrinsicWidth();
            panelHeight = mPanel.getIntrinsicHeight();
        }
        mCenterX = Math.max(width, panelWidth) / 2;
        mCenterY= Math.max(height, panelHeight) / 2;
        updateTargetPositions(mCenterX, mCenterY);
    }

    private void updateTargetPositions(float centerX, float centerY) {
        final float offset = 60;
        mLeftDrawable.setX(offset);
        mLeftDrawable.setY(centerY);
        mRightDrawable.setX(getPanelWidth()-offset);
        mRightDrawable.setY(centerY);
        mTopDrawable.setX(centerX);
        mTopDrawable.setY(offset);
        mBottomDrawable.setX(centerX);
        mBottomDrawable.setY(getPanelHeight()-offset);
    }

    private float getPanelHeight() {
        float height = 0;
        if (mPanel != null) {
            height = mPanel.getIntrinsicHeight();
        }
        return height;
    }

    private float getPanelWidth() {
        float width = 0;
        if (mPanel != null) {
            width = mPanel.getIntrinsicWidth();
        }
        return width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mTargetDrawables.size(); i++) {
            TargetDrawable target = mTargetDrawables.get(i);
            if (target != null) {
                target.draw(canvas);
            }
        }
    }
}
