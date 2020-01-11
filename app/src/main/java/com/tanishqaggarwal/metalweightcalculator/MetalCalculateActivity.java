package com.tanishqaggarwal.metalweightcalculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

import static com.tanishqaggarwal.metalweightcalculator.Utils.roundDecimal;

public class MetalCalculateActivity extends AppCompatActivity {
    // Metadata of currently displayed shape
    ShapeType currShapeData;

    // Mappings from shape's fields to form elements created for the fields
    Map<ShapeTypeFieldInfo, EditText> shapeFieldSelectedValues;
    Map<ShapeTypeFieldInfo, Spinner> shapeFieldSelectedUnits;

    // Manages calculation UI elements and computation based on selected radio button
    RadioViewController calculationController;
    // All photos taken in the current session
    List<String> currentPhotoPath;
    // Densities used by density spinner
    Map<String, Double> densities;
    /**
     * Dynamically initialize form elements for the chosen shape.
     *
     * @param savedInstanceState
     */
    String shapeType;
    double widthA = 0.0, diameterD = 0.0, diameterS = 0.0, thicknessT = 0.0,
            sideA = 0.0, sideB = 0.0, widthW = 0.0, internalDaimeter = 0.0, outerDiameter = 0.0, length = 0.0, weight = 0.0;
    String widthAU = "", diameterDU = "", diameterSU = "", thicknessTU = "",
            sideAU = "", sideBU = "", widthWU = "", internalDaimeterU = "", outerDiameterU = "", lengthU = "", weightU = "";
    double density = 0.0;
    double pieceInputVal = 0.0;
    double kgInputVal = 0.0;
    // Editable density field
    EditText fieldDataLength, kgPriceLength, fieldDataWigth, noofPieces, vDensity;
    Spinner fieldUnitsLength, fieldUnitsWigth;
    TextView resultsText;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metal_calculate);

        // Get selected shape type and add image to screen
        Bundle bundle = getIntent().getExtras();
        shapeType = bundle.getString("shape");
        currShapeData = CacheConstants.shapeTypes.get(shapeType);
        ImageView dimPic = findViewById(R.id.dimPic);
        dimPic.setImageResource(currShapeData.shapeDimPic);

        // Get all form elements
        vDensity = this.findViewById(R.id.densityField);
        fieldDataLength = findViewById(R.id.fieldDataLength);
        kgPriceLength = findViewById(R.id.kgPriceLength);
        fieldUnitsLength = findViewById(R.id.fieldUnitsLength);
        fieldDataWigth = findViewById(R.id.fieldDataWigth);
        fieldUnitsWigth = findViewById(R.id.fieldUnitsWigth);
        noofPieces = findViewById(R.id.noofPieces);
        resultsText = findViewById(R.id.resultsText);

        // Read in JSON data on material densities
        densities = CacheConstants.readDensities(getAssets());
        // Add metal types to dropdown and create handler
        createMetalSpinner();

        // Add form elements for this shape
        shapeFieldSelectedValues = new HashMap<>();
        shapeFieldSelectedUnits = new HashMap<>();
        LinearLayout fieldsView = findViewById(R.id.shapeFieldsView);
        for (int i = 0; i < currShapeData.shapeFields.size(); i++) {

            View fieldView = getLayoutInflater().inflate(R.layout.shape_field, null);

            ShapeTypeFieldInfo field = currShapeData.shapeFields.get(i);
            assert field != null;
            String fieldName = field.fieldName;
            // Set input field values
            EditText numericInput = fieldView.findViewById(R.id.fieldData);
            numericInput.setHintTextColor(Color.GRAY);
            numericInput.setTextColor(Color.BLACK);
            numericInput.setHint(fieldName);

            // Populate spinner with available units
            Spinner unitInput = fieldView.findViewById(R.id.fieldUnits);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, field.fieldUnits);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            unitInput.setAdapter(adapter);

            // Attach field to mapping between shape field descriptions and actual fields
            shapeFieldSelectedValues.put(field, numericInput);
            shapeFieldSelectedUnits.put(field, unitInput);

            // Add field to main view
            fieldsView.addView(fieldView);
        }

        // Initialize photos path
        currentPhotoPath = new ArrayList<>();
        // Add radio button listener and set default option to be "calculate by length"
        radioGroup = findViewById(R.id.calculationOptionRadioGroup);
        calculationController = new RadioViewController(this);
        radioGroup.setOnCheckedChangeListener(calculationController);
        radioGroup.check(R.id.calculateByLengthRadioBtn);
    }

    /**
     * When the user selects "by weight" or "by length" calculations, this helper class does the
     * magic to change the UI and the data elements backing the UI.
     */
    private class RadioViewController implements RadioGroup.OnCheckedChangeListener {
        /**
         * Activity context (provided by constructor), used by field construction methods
         */
        Context ctx;

        public RadioViewController(Context ctx) {
            this.ctx = ctx;
        }

        /**
         * Executes whenever the choice of calculation is changed.
         *
         * @param group     UI element of the calculation choice radio group.
         * @param checkedId ID of selected UI element.
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // checkedId is the RadioButton selected
            switch (checkedId) {
                case R.id.calculateByLengthRadioBtn:
                    fieldDataLength.setVisibility(View.VISIBLE);
                    kgPriceLength.setVisibility(View.VISIBLE);
                    fieldDataWigth.setVisibility(View.GONE);
                    fieldUnitsLength.setVisibility(View.VISIBLE);
                    fieldUnitsWigth.setVisibility(View.GONE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx,
                            android.R.layout.simple_spinner_dropdown_item,
                            CacheConstants.lengthUnits.keySet().toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fieldUnitsLength.setAdapter(adapter);
                    break;
                case R.id.calculateByWeightRadioBtn:
                    fieldDataLength.setVisibility(View.GONE);
                    kgPriceLength.setVisibility(View.GONE);
                    fieldDataWigth.setVisibility(View.VISIBLE);
                    fieldUnitsLength.setVisibility(View.GONE);
                    fieldUnitsWigth.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ctx,
                            android.R.layout.simple_spinner_dropdown_item,
                            CacheConstants.weightUnits.keySet().toArray(new String[0]));
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fieldUnitsWigth.setAdapter(adapter2);
                    break;
            }

        }
    }


    public boolean getDataFromFields() {
        // TODO
        for (int i = 0; i < currShapeData.shapeFields.size(); i++) {
            ShapeTypeFieldInfo field = currShapeData.shapeFields.get(i);
            EditText editText = shapeFieldSelectedValues.get(field);
            Spinner spinner = shapeFieldSelectedUnits.get(field);
            String spinnerItem = spinner.getSelectedItem().toString();
            String hint = editText.getHint().toString();
            if (!editText.getText().toString().isEmpty()) {
                switch (hint) {
                    case "Width (A)":
                        widthA = Double.parseDouble(editText.getText().toString());
                        widthAU = spinnerItem;
                        break;
                    case "Diameter (D)":
                        diameterD = Double.valueOf(editText.getText().toString());
                        diameterDU = spinnerItem;
                        break;
                    case "Thickness (S)":
                        diameterS = Double.valueOf(editText.getText().toString());
                        diameterSU = spinnerItem;
                        break;
                    case "Thickness (T)":
                        thicknessT = Double.valueOf(editText.getText().toString());
                        thicknessTU = spinnerItem;
                        break;
                    case "Side (A)":
                        sideA = Double.valueOf(editText.getText().toString());
                        sideAU = spinnerItem;
                        break;
                    case "Side (B)":
                        sideB = Double.valueOf(editText.getText().toString());
                        sideBU = spinnerItem;
                        break;
                    case "Width (W)":
                        widthW = Double.valueOf(editText.getText().toString());
                        widthWU = spinnerItem;
                        break;
                    case "Internal Diameter (ID)":
                        internalDaimeter = Double.valueOf(editText.getText().toString());
                        internalDaimeterU = spinnerItem;
                        break;
                    case "Outer Diameter (OD)":
                        outerDiameter = Double.valueOf(editText.getText().toString());
                        outerDiameterU = spinnerItem;
                        break;
                }
            } else {
                Toast.makeText(MetalCalculateActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        int selectedCalculation = radioGroup.getCheckedRadioButtonId();
        switch (selectedCalculation) {
            case R.id.calculateByLengthRadioBtn:
                if (checkValue(fieldDataLength) != 0.0 && checkValue(kgPriceLength) != 0.0) {
                    length = checkValue(fieldDataLength);
                    lengthU = fieldUnitsLength.getSelectedItem().toString();
                    length *= CacheConstants.lengthUnits.get(lengthU);
                    kgInputVal = checkValue(kgPriceLength);
                } else {
                    return false;
                }
                break;
            case R.id.calculateByWeightRadioBtn:
                if (checkValue(fieldDataWigth) != 0.0) {
                    weight = checkValue(fieldDataWigth);
                    weightU = fieldUnitsWigth.getSelectedItem().toString();
                    weight *= CacheConstants.weightUnits.get(weightU);
                } else {
                    return false;
                }
                break;
        }
        if (checkValue(noofPieces) != 0.0 && checkValue(vDensity) != 0.0) {
            pieceInputVal = checkValue(noofPieces);
            density = checkValue(vDensity);
        } else {
            return false;
        }

        Log.d(">>>", "widthA " + widthA + " /diameterD " + diameterD +
                "/diameterS " + diameterS + " /thicknessT " + thicknessT +
                " /sideA " + sideA + " /sideB " + sideB + "/widthW " + widthW +
                "outerDiameter/internalDaimeter " + internalDaimeter + " /outerDiameter " + outerDiameter);
        Log.d(">>>", "length " + length + " /weight" + weight);
        Log.d(">>>", "pieceInputVal " + pieceInputVal + " /kgInputVal " + kgInputVal);
        return true;
    }

    public double checkValue(EditText editText) {
        if (!editText.getText().toString().isEmpty()) {
            return Double.parseDouble(editText.getText().toString());
        } else {
            Toast.makeText(MetalCalculateActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
            return 0.0;
        }
    }

    /**
     * Create metal choice selector.
     */
    public void createMetalSpinner() {
        Spinner spinner = findViewById(R.id.metalSelect);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(densities.keySet()));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMaterial = ((AppCompatTextView) selectedItemView).getText().toString();
                vDensity.setText(densities.get(selectedMaterial).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                vDensity.setText("");
            }
        });
    }

    /**
     * Save piece info to local storage so that it can be displayed on the main screen.
     *
     * @param v Button that was clicked to save information.
     */
    public void savePieceInfo(View v) {
        if (getDataFromFields())
            addPiece(shapeType, widthA, widthAU, diameterD, diameterDU, diameterS, diameterSU, thicknessT, thicknessTU, sideA, sideAU, sideB, sideBU, widthW, widthWU, internalDaimeter, internalDaimeterU, outerDiameter, outerDiameterU, length, lengthU, weight, weightU, pieceInputVal, kgInputVal, density);
    }

    private void addPiece(String ShapeName, double widthA, String widthAU, double diameterD, String diameterDU, double diameterS, String diameterSU, double thicknessT, String thicknessTU, double sideA, String sideAU, double sideB, String sideBU, double widthW, String widthWU, double internalDaimeter, String internalDaimeterU, double outerDiameter, String outerDiameterU, double length, String lengthU, double weight, String weightU, double pieceInputVal, double kgInputVal, double density) {
        final SavedPiece object = new SavedPiece(ShapeName, widthA, widthAU, diameterD, diameterDU, diameterS, diameterSU, thicknessT, thicknessTU, sideA, sideAU, sideB, sideBU, widthW, widthWU, internalDaimeter, internalDaimeterU, outerDiameter, outerDiameterU, length, lengthU, weight, weightU, pieceInputVal, kgInputVal, density);
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        realm.copyToRealm(object);
                        Toast.makeText(getApplicationContext(), "Item Saved", Toast.LENGTH_SHORT).show();
                    } catch (RealmPrimaryKeyConstraintException e) {
                        Toast.makeText(getApplicationContext(), "Primary Key exists, Press Update instead", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    /**
     * Calculate and store results for the piece based on stored values.
     *
     * @param v
     */
    public void calculatePieceInfo(View v) {
        if (!getDataFromFields())
            return;
        List<Object> fieldValues = new LinkedList<>();
        for (ShapeTypeFieldInfo fInfo : currShapeData.shapeFields) {
            // Get unit-adjusted value of field
            double fieldValue;
            try {
                fieldValue = Double.parseDouble(shapeFieldSelectedValues.get(fInfo).getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                fieldValue = 0;
            }
            String fieldUnits = shapeFieldSelectedUnits.get(fInfo).getSelectedItem().toString();
            fieldValue *= CacheConstants.lengthUnits.get(fieldUnits);
            fieldValues.add(fieldValue);
        }
        Object[] calculationArgList = fieldValues.toArray();
        double area = runCalculation(currShapeData.areaCalculation, calculationArgList);
        double perimeter = runCalculation(currShapeData.perimeterCalculation, calculationArgList);

        // Inputs and outputs of calculation.
        double density;
        try {
            density = Double.parseDouble(vDensity.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            density = 0;
        }
        double kg_price = kgInputVal;
        double num_pieces = pieceInputVal;
        double length_per_piece = 0;
        // Outputs
        double price_per_piece = 0;
        double volume_per_piece = 0;
        double surface_area_per_piece = 0;
        double mass_per_piece = 0;
        double total_mass = 0;
        double total_surface_area = 0;
        double total_price = 0;

        // Get calculation option and run calculation with field inputs
        int selectedCalculation = radioGroup.getCheckedRadioButtonId();
        switch (selectedCalculation) {
            case R.id.calculateByLengthRadioBtn:
                // Compute volume
                length_per_piece = length; // TODO replace with field input
                volume_per_piece = length_per_piece * area;
                mass_per_piece = volume_per_piece * density;

                break;
            case R.id.calculateByWeightRadioBtn:
                mass_per_piece = weight; // TODO replace with field input
                volume_per_piece = mass_per_piece / density;
                length_per_piece = volume_per_piece / area;

                break;
        }
        price_per_piece = kg_price * mass_per_piece; //Here is the issue
        total_price = price_per_piece * num_pieces;
        surface_area_per_piece = length_per_piece * perimeter;
        total_surface_area = surface_area_per_piece * num_pieces;
        total_mass = mass_per_piece * num_pieces;
        // TODO print results to screen, and add values to saved piece info
        resultsText.setText("Price per piece:" + roundDecimal(price_per_piece) + "\n" +
                "Total Price:" + roundDecimal(total_price) + "\n" +
                "Surface Area per piece:" + roundDecimal(surface_area_per_piece) + "\n" +
                "Total Surface Area:" + roundDecimal(total_surface_area) + "\n" +
                "Total Mass:" + roundDecimal(total_mass)
        );
    }

    /**
     * Helper method that interprets the area calculation provided as a string of Javascript in the
     * shape descriptor JSON file in order to compute the area.
     *
     * @param calculation String containing the javascript describing the calculation. The shape's
     *                    field parameters will be provided, in order, to the function.
     * @param args        Field parameters provided to the calculation.
     * @return Value of cross-sectional area.
     */
    public static double runCalculation(String calculation, Object[] args) {
        double area;

        org.mozilla.javascript.Context context = org.mozilla.javascript.Context.enter();
        context.setOptimizationLevel(-1);
        try {
            org.mozilla.javascript.ScriptableObject scope = context.initStandardObjects();
            org.mozilla.javascript.Scriptable that = context.newObject(scope);
            org.mozilla.javascript.Function fct = context.compileFunction(scope, calculation,
                    "script", 1, null);
            Object result = fct.call(context, scope, that, args);

            area = Double.parseDouble(org.mozilla.javascript.Context.jsToJava(result,
                    double.class).toString());
        } finally {
            org.mozilla.javascript.Context.exit();
        }
        return area;
    }

    /**
     * Read barcode in order to get piece information.
     *
     * @param v
     */
    public void readBarcode(View v) {
        // TODO this is currently dummy code that reads a hardcoded barcode

        Bitmap myBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.barcode);

        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if (!detector.isOperational()) {
            System.out.println("Could not set up the detector!");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        Barcode thisCode = barcodes.valueAt(0);
        System.out.println(thisCode.rawValue);
    }

    /**
     * Take picture and associate it with the current metal piece that will be saved.
     *
     * @param v
     */
    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tanishqaggarwal.metalweightcalculator",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
        // TODO link current photo path with current saved piece info object
    }

    /**
     * Helper function to save image to a file with a unique name.
     *
     * @return Reference to file object that was created.
     * @throws IOException Thrown if image file cannot be created.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath.add(image.getAbsolutePath());
        return image;
    }

}
