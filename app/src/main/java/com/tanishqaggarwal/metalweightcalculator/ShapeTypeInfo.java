package com.tanishqaggarwal.metalweightcalculator;

import android.graphics.drawable.Drawable;

public class ShapeTypeInfo {
    protected String shapeName;
    protected Drawable shapeImg;

    public ShapeTypeInfo(String shapeName, Drawable shapeImg) {
        this.shapeName = shapeName;
        this.shapeImg = shapeImg;
    }
}
