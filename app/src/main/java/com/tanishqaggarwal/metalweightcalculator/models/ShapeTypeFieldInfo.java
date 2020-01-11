package com.tanishqaggarwal.metalweightcalculator.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Metadata fields required to fully describe a shape numerically.
 */
public class ShapeTypeFieldInfo extends RealmObject {
    public String fieldName;
    public String fieldType;
    public RealmList<String> fieldUnits;

    public ShapeTypeFieldInfo() {
    }

    public ShapeTypeFieldInfo(String fieldName, String fieldType, RealmList<String> fieldUnits) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldUnits = fieldUnits;
    }
}