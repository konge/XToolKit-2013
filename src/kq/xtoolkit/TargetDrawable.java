package kq.xtoolkit;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class TargetDrawable {

    private float mTranslationX = 0.0f;
    private float mTranslationY = 0.0f;
    private float mPositionX = 0.0f;
    private float mPositionY = 0.0f;
    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;
    private float mAlpha = 1.0f;
    private Drawable mDrawable;
    private boolean mEnabled = true;
    private final int mResourceId;

    public TargetDrawable(Resources res, int resId) {
        mResourceId = resId;
        setDrawable(res, resId);
    }

    public void setDrawable(Resources res, int resId) {
        Drawable drawable = resId == 0 ? null : res.getDrawable(resId);
        // Mutate the drawable so we can animate shared drawable properties.
        mDrawable = drawable != null ? drawable.mutate() : null;
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
    }

    public void setX(float x) {
        mTranslationX = x;
    }

    public void setY(float y) {
        mTranslationY = y;
    }

    public void setScaleX(float x) {
        mScaleX = x;
    }

    public void setScaleY(float y) {
        mScaleY = y;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public float getX() {
        return mTranslationX;
    }

    public float getY() {
        return mTranslationY;
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setPositionX(float x) {
        mPositionX = x;
    }

    public void setPositionY(float y) {
        mPositionY = y;
    }

    public float getPositionX() {
        return mPositionX;
    }

    public float getPositionY() {
        return mPositionY;
    }

    public int getWidth() {
        return (int) (mDrawable != null ? mDrawable.getIntrinsicWidth() * mScaleX : 0);
    }

    public int getHeight() {
        return (int) (mDrawable != null ? mDrawable.getIntrinsicHeight() * mScaleY : 0);
    }

    public void draw(Canvas canvas) {
        if (mDrawable == null || !mEnabled) {
            return;
        }
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(mTranslationX + mPositionX, mTranslationY + mPositionY);
        canvas.translate(-0.5f * getWidth(), -0.5f * getHeight());
        canvas.scale(mScaleX, mScaleY);
        mDrawable.setAlpha((int) Math.round(mAlpha * 255f));
        mDrawable.draw(canvas);
        canvas.restore();
    }


    public int getResourceId() {
        return mResourceId;
    }
}
