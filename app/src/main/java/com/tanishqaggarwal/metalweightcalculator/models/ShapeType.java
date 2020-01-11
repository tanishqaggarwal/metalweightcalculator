package com.tanishqaggarwal.metalweightcalculator.models;

import io.realm.RealmList;
import io.realm.RealmObject;


/**
 * Shape type data.
 */
public class ShapeType extends RealmObject {

    public String shapeName;
    public int shapeIcon;
    public int shapeDimPic;
    public String areaCalculation;
    public String perimeterCalculation;
    public RealmList<ShapeTypeFieldInfo> shapeFields;

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
