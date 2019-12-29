package com.tanishqaggarwal.metalweightcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Application entry point.
 */
public class MainActivity extends AppCompatActivity {
    static boolean loadedShapeData = false;
    static HashMap<String, ShapeTypeInfo> shapeTypes;

    public static Map<String, Double> lengthUnits;
    public static Map<String, Double> weightUnits;

    public static void populateUnitConversions() {
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
     * Function that is run upon initialization of application.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateUnitConversions();

        // Read available shape types and make the data available to all classes
        // via the static class variables
        readShapeData(getAssets(), getApplicationContext());

        // Create recycler view for saved pieces
        RecyclerView recList = findViewById(R.id.savedPiecesList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        SavedPieceAdapter sa = new SavedPieceAdapter();
        recList.setAdapter(sa);

        // Populate saved pieces with saved app data
        // TODO remove after adding real data
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 12, 12, 12,
                12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 10, 12, 12,
                12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 13, 12, 12,
                12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 124, 12, 12,
                12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 125, 12, 12,
                12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 121, 12, 12,
                12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape",
                "Width: 12 in", 11, 12, 12,
                12));
    }

    /**
     * Allows user to add item to the calculator.
     * @param v
     */
    public void goToAddItem(View v) {
        Intent intent = new Intent(this, ShapeListActivity.class);
        startActivity(intent);
    }

    /**
     * Exports current saved piece list to online location.
     * @param v Button.
     */
    public void exportList(View v) {
        // TODO
    }

    /**
     * Reads shape data from JSON file and returns it, for consumption by ShapeListActivity
     * and MetalCalculateActivity.
     *
     * @param assets AssetManager used to get access to XML file.
     * @return Map from shape name to shape image and field data.
     */
    public static void readShapeData(AssetManager assets, Context ctx) {
        if (loadedShapeData) return;

        try {
            InputStream is = assets.open("shape_type_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject json = new JSONObject(new String(buffer, "UTF-8"));

            shapeTypes = new HashMap<>();
            JSONArray shapeTypesJsonArray = json.getJSONArray("shapes");
            for(int i = 0; i < shapeTypesJsonArray.length(); i++) {
                JSONObject shapeJsonObj = shapeTypesJsonArray.getJSONObject(i);

                // Read raw shape data
                String shapeName = shapeJsonObj.getString("shape_name");
                String shapeIconFilename = shapeJsonObj.getString("icon");
                String shapeDimPicFilename = shapeJsonObj.getString("dim_pic");
                String shapeVolumeCalculation = shapeJsonObj.getString("area_calc");

                // Convert image filenames into Drawable objects that can be passed around
                int shapeIconResourceId = ctx.getResources().getIdentifier(shapeIconFilename,
                        "drawable", ctx.getPackageName());
                int shapeDimPicResourceId = ctx.getResources().getIdentifier(shapeDimPicFilename,
                        "drawable", ctx.getPackageName());

                // Read shape fields
                JSONArray shapeFieldsJsonArray = shapeJsonObj.getJSONArray("fields");
                List<ShapeTypeInfo.ShapeTypeFieldInfo> shapeFields = new LinkedList<>();
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
                    List<String> fieldUnits = new LinkedList<>();
                    for(int k = 0; k < fieldUnitsJsonArray.length(); k++) {
                        fieldUnits.add(fieldUnitsJsonArray.getString(k));
                    }

                    // Validate units
                    List<String> lengthUnitList = Arrays.asList(lengthUnits.keySet().toArray(new String[0]));
                    List<String> weightUnitList = Arrays.asList(weightUnits.keySet().toArray(new String[0]));
                    for(String unit : fieldUnits) {
                        if(!lengthUnitList.contains(unit) && !weightUnitList.contains(unit)) {
                            throw new JSONException("Invalid field units");
                        }
                    }

                    shapeFields.add(new ShapeTypeInfo.ShapeTypeFieldInfo(fieldName, fieldType,
                            fieldUnits));
                }

                // Construct final shape type and add it to list
                shapeTypes.put(shapeName, new ShapeTypeInfo(shapeName, shapeIconResourceId,
                        shapeDimPicResourceId, shapeVolumeCalculation, shapeFields));

            }

            // Finished loading shape data! Don't do it again.
            loadedShapeData = true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
