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
    protected RealmList<ShapeTypeFieldInfo> shapeFields;

    public ShapeType() {
    }

    public ShapeType(String shapeName, int shapeIcon, int shapeDimPic, String areaCalculation,
                     RealmList<ShapeTypeFieldInfo> shapeFields) {
        this.shapeName = shapeName;
        this.shapeIcon = shapeIcon;
        this.shapeDimPic = shapeDimPic;
        this.areaCalculation = areaCalculation;
        this.shapeFields = shapeFields;
    }


}
