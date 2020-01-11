package com.tanishqaggarwal.metalweightcalculator;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;

public class CacheConstants {
    public static HashMap<String, ShapeType> shapeTypes;
    public static Map<String, Double> metalData;
    public static final Map<String, Double> lengthUnits;
    public static final Map<String, Double> weightUnits;

    static {
        lengthUnits = new HashMap<>();
        weightUnits = new HashMap<>();

        lengthUnits.put("ft", 304.8);
        lengthUnits.put("m", 1000.0);
        lengthUnits.put("mm", 1.0);
        lengthUnits.put("cm", 100.0);
        lengthUnits.put("in", 25.4);

        weightUnits.put("kg", 1.0);
        weightUnits.put("lb", 0.4536);
    }

    /**
     * Read metalData from JSON file to populate into material selection option.
     *
     * @param assets Asset Manager that will provide the JSON file.
     * @return Map from material name to density.
     */
    public static Map<String, Double> readDensities(AssetManager assets) {
        if(null != metalData){
            return metalData;
        }
        metalData = new HashMap<>();
        Map<String, Double> metalData = CacheConstants.metalData;
        try {
            InputStream is = assets.open("materials.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject json = new JSONObject(new String(buffer, "UTF-8"));

            JSONArray materialProperties = json.getJSONArray("materials");
            for(int i = 0; i < materialProperties.length(); i++) {
                JSONObject materialPropertyJsonObj = materialProperties.getJSONObject(i);
                String materialName = materialPropertyJsonObj.getString("material_name");
                double materialDensity = materialPropertyJsonObj.getDouble("density");
                metalData.put(materialName, materialDensity);
            }
            return metalData;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Reads shape data from JSON file and returns it, for consumption by ShapeListActivity
     * and MetalCalculateActivity.
     *
     * @param assets AssetManager used to get access to XML file.
     * @return Map from shape name to shape image and field data.
     */
    public static void readShapeData(AssetManager assets, Context ctx) {
        if(null != shapeTypes){
            return;
        }
        shapeTypes = new HashMap<>();
        try {
            InputStream is = assets.open("shape_type_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject json = new JSONObject(new String(buffer, "UTF-8"));

            HashMap<String, ShapeType> shapeTypes = CacheConstants.shapeTypes;
            JSONArray shapeTypesJsonArray = json.getJSONArray("shapes");
            for(int i = 0; i < shapeTypesJsonArray.length(); i++) {
                JSONObject shapeJsonObj = shapeTypesJsonArray.getJSONObject(i);

                // Read raw shape data
                String shapeName = shapeJsonObj.getString("shape_name");
                String shapeIconFilename = shapeJsonObj.getString("icon");
                String shapeDimPicFilename = shapeJsonObj.getString("dim_pic");
                String shapeVolumeCalculation = shapeJsonObj.getString("area_calc");
                String shapeSurfaceAreaCalculation = shapeJsonObj.getString("perimeter_calc");
                // Convert image filenames into Drawable objects that can be passed around
                int shapeIconResourceId = ctx.getResources().getIdentifier(shapeIconFilename,
                        "drawable", ctx.getPackageName());
                int shapeDimPicResourceId = ctx.getResources().getIdentifier(shapeDimPicFilename,
                        "drawable", ctx.getPackageName());

                // Read shape fields
                JSONArray shapeFieldsJsonArray = shapeJsonObj.getJSONArray("fields");
                RealmList<ShapeTypeFieldInfo> shapeFields = new RealmList<>();
                for(int j = 0; j < shapeFieldsJsonArray.length(); j++) {
                    JSONObject field = shapeFieldsJsonArray.getJSONObject(j);
                    String fieldName = field.getString("field_name");
                    String fieldType = field.getString("type");

                    // Validate field type
                    if (!fieldType.equals("number") && !fieldType.equals("decimal")) {
                        throw new JSONException("Invalid field type for field: " + fieldName +
                                ". Field type was: " + fieldType);
                    }

                    JSONArray fieldUnitsJsonArray = field.getJSONArray("units");
                    RealmList<String> fieldUnits = new RealmList<>();
                    for(int k = 0; k < fieldUnitsJsonArray.length(); k++) {
                        fieldUnits.add(fieldUnitsJsonArray.getString(k));
                    }
                    // Validate units
                    List<String> lengthUnitList = Arrays.asList(CacheConstants.lengthUnits.keySet().toArray(new String[0]));
                    List<String> weightUnitList = Arrays.asList(CacheConstants.weightUnits.keySet().toArray(new String[0]));
                    for(String unit : fieldUnits) {
                        if(!lengthUnitList.contains(unit) && !weightUnitList.contains(unit)) {
                            throw new JSONException("Invalid field units");
                        }
                    }
                    shapeFields.add(new ShapeTypeFieldInfo(fieldName, fieldType, fieldUnits));
                }
                // Construct final shape type and add it to list
//                shapeTypes.put(shapeName, new ShapeType(shapeName, shapeIconResourceId, shapeDimPicResourceId, shapeVolumeCalculation, shapeFields));
                shapeTypes.put(shapeName, new ShapeType(shapeName, shapeIconResourceId,
                shapeDimPicResourceId, shapeVolumeCalculation, shapeSurfaceAreaCalculation, shapeFields));
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

}
