package com.tanishqaggarwal.metalweightcalculator;

import java.util.List;


/**
 * Shape type data.
 */
public class ShapeType {

    protected String shapeName;
    protected int shapeIcon;
    protected int shapeDimPic;
    protected String areaCalculation;
    protected String perimeterCalculation;
    protected List<ShapeTypeFieldInfo> shapeFields;


    /**
     * Constructor.
     *
     * @param shapeName
     * @param shapeDimPic
     * @param shapeFields
     */
    public ShapeType(String shapeName, int shapeIcon, int shapeDimPic, String areaCalculation,
                     String perimeterCalculation,
                     List<ShapeTypeFieldInfo> shapeFields) {
        this.shapeName = shapeName;
        this.shapeIcon = shapeIcon;
        this.shapeDimPic = shapeDimPic;
        this.areaCalculation = areaCalculation;
        this.perimeterCalculation = perimeterCalculation;
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
