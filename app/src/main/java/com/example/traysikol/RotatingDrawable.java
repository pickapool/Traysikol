package com.example.traysikol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemClock;

public class RotatingDrawable extends LayerDrawable implements Runnable {
    private Drawable drawable;
    private float rotation;

    public RotatingDrawable(Context context, int resId) {
        super(new Drawable[]{context.getResources().getDrawable(resId)});
        drawable = getDrawable(0);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotation, drawable.getBounds().centerX(), drawable.getBounds().centerY());
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    public void run() {
        rotation += 2.0f;
        if (rotation >= 360.0f) {
            rotation = 0.0f;
        }
        invalidateSelf();
        scheduleNext();
    }

    public void start() {
        scheduleNext();
    }

    private void scheduleNext() {
        unscheduleSelf(this);
        scheduleSelf(this, SystemClock.uptimeMillis() + 16);
    }
}
