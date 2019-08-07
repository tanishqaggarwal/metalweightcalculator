package com.tanishqaggarwal.metalweightcalculator;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Shape type data.
 */
public class ShapeTypeInfo {
    protected String shapeName;
    protected List<ShapeTypeFieldInfo> shapeFields;
    protected Drawable shapeIcon;
    protected Drawable shapeImg;

    /**
     * Constructor.
     *
     * @param shapeName
     * @param shapeImg
     * @param shapeFields
     */
    public ShapeTypeInfo(String shapeName, Drawable shapeIcon, Drawable shapeImg, List<ShapeTypeFieldInfo> shapeFields) {
        this.shapeName = shapeName;
        this.shapeIcon = shapeIcon;
        this.shapeImg = shapeImg;
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

    /**
     * Reads shape data from JSON file and returns it, for consumption by ShapeListActivity
     * and MetalCalculateActivity.
     *
     * @param assets AssetManager used to get access to XML file.
     * @return Map from shape name to shape image and field data.
     */
    public static Map<String, ShapeTypeInfo> readShapeData(AssetManager assets, Context ctx) {
        Map<String, ShapeTypeInfo> shapeTypes = new HashMap<>();

        try {
            InputStream is = assets.open("shape_type_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject json = new JSONObject(new String(buffer, "UTF-8"));

            JSONArray shapeTypesJsonArray = json.getJSONArray("shapes");
            for(int i = 0; i < shapeTypesJsonArray.length(); i++) {
                JSONObject shapeJsonObj = shapeTypesJsonArray.getJSONObject(i);

                // Read raw shape data
                String shapeName = shapeJsonObj.getString("shape_name");
                String shapeIconFilename = shapeJsonObj.getString("icon");
                String shapeDimPicFilename = shapeJsonObj.getString("dim_pic");

                // Convert image filenames into Drawable objects that can be passed around
                int shapeIconResourceId = ctx.getResources().getIdentifier(shapeIconFilename,
                        "drawable", ctx.getPackageName());
                int shapeDimPicResourceId = ctx.getResources().getIdentifier(shapeDimPicFilename,
                        "drawable", ctx.getPackageName());
                Drawable shapeIcon = ResourcesCompat.getDrawable(ctx.getResources(),
                        shapeIconResourceId, null);
                Drawable shapeDimPic = ResourcesCompat.getDrawable(ctx.getResources(),
                        shapeDimPicResourceId, null);

                // Read shape fields
                JSONArray shapeFieldsJsonArray = shapeJsonObj.getJSONArray("fields");
                List<ShapeTypeFieldInfo> shapeFields = new LinkedList<>();
                for(int j = 0; j < shapeFieldsJsonArray.length(); j++) {
                    JSONObject field = shapeFieldsJsonArray.getJSONObject(j);
                    String fieldName = field.getString("field_name");
                    String fieldType = field.getString("type");

                    JSONArray fieldUnitsJsonArray = field.getJSONArray("units");
                    List<String> fieldUnits = new LinkedList<>();
                    for(int k = 0; k < fieldUnitsJsonArray.length(); k++) {
                        fieldUnits.add(fieldUnitsJsonArray.getString(k));
                    }

                    shapeFields.add(new ShapeTypeFieldInfo(fieldName, fieldType, fieldUnits));
                }

                shapeTypes.put(shapeName, new ShapeTypeInfo(shapeName, shapeIcon, shapeDimPic,
                        shapeFields));
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }

        return shapeTypes;
    }
}
