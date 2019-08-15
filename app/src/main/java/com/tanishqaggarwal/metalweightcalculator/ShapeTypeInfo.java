package com.tanishqaggarwal.metalweightcalculator;

import android.graphics.drawable.Drawable;
import java.util.List;


/**
 * Shape type data.
 */
public class ShapeTypeInfo {
    protected String shapeName;
    protected int shapeIcon;
    protected int shapeImg;
    protected String areaCalculation;
    protected List<ShapeTypeFieldInfo> shapeFields;


    /**
     * Constructor.
     *
     * @param shapeName
     * @param shapeImg
     * @param shapeFields
     */
    public ShapeTypeInfo(String shapeName, int shapeIcon, int shapeImg, String areaCalculation, List<ShapeTypeFieldInfo> shapeFields) {
        this.shapeName = shapeName;
        this.shapeIcon = shapeIcon;
        this.shapeImg = shapeImg;
        this.areaCalculation = areaCalculation;
        this.shapeFields = shapeFields;
    }

    /**
     * Metadata fields required to fully describe a shape numerically.
     */
    public static class ShapeTypeFieldInfo {
        String fieldName;
        String fieldType;
        List<String> fieldUnits;
        ShapeTypeFieldInfo(String fieldName, String fieldType, List<String> fieldUnits) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldUnits = fieldUnits;
        }
    }
}
