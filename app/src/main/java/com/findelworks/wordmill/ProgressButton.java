package com.findelworks.wordmill;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.widget.ImageView;

public class ProgressButton extends ImageView {

    private ShapeDrawable mDrawable;

    public ProgressButton(Context context, float start, float sweep) {
        super(context);

        int x = 10;
        int y = 10;
        int width = 300;
        int height = 50;

        float arcStart, arcSweep = 0;

        arcStart = 1;
        arcSweep = 1;

        mDrawable = new ShapeDrawable(new ArcShape(arcStart, arcSweep));
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(x, y, x + width, y + height);
    }

    protected void onDraw(Canvas canvas) {
        mDrawable.draw(canvas);
    }

}