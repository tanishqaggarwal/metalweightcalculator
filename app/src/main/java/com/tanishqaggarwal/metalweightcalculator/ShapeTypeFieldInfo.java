package com.tanishqaggarwal.metalweightcalculator;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Metadata fields required to fully describe a shape numerically.
 */
public class ShapeTypeFieldInfo extends RealmObject {
    String fieldName;
    String fieldType;
    RealmList<String> fieldUnits;

    public ShapeTypeFieldInfo() {
    }

    public ShapeTypeFieldInfo(String fieldName, String fieldType, RealmList<String> fieldUnits) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldUnits= fieldUnits;
    }
}