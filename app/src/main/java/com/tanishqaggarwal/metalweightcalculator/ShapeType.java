package com.tanishqaggarwal.metalweightcalculator;

import io.realm.RealmList;
import io.realm.RealmObject;


/**
 * Shape type data.
 */
public class ShapeType extends RealmObject {

    protected String shapeName;
    protected int shapeIcon;
    protected int shapeDimPic;
    protected String areaCalculation;
    protected String perimeterCalculation;
    protected RealmList<ShapeTypeFieldInfo> shapeFields;

    public ShapeType() {
    }

    public ShapeType(String shapeName, int shapeIcon, int shapeDimPic, String areaCalculation,String perimeterCalculation,
                     RealmList<ShapeTypeFieldInfo> shapeFields) {
        this.shapeName = shapeName;
        this.shapeIcon = shapeIcon;
        this.shapeDimPic = shapeDimPic;
        this.areaCalculation = areaCalculation;
        this.perimeterCalculation = perimeterCalculation;
        this.shapeFields = shapeFields;
    }


}
