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
import android.text.InputType;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MetalCalculateActivity extends AppCompatActivity {
    // Metadata of currently displayed shape
    ShapeType currShapeData;

    // Mappings from shape's fields to form elements created for the fields
    Map<ShapeType.ShapeTypeFieldInfo, EditText> shapeFieldSelectedValues;
    Map<ShapeType.ShapeTypeFieldInfo, Spinner> shapeFieldSelectedUnits;

    // Manages calculation UI elements and computation based on selected radio button
    RadioViewController calculationController;
    // All photos taken in the current session
    List<String> currentPhotoPath;
    // Densities used by density spinner
    Map<String, Double> densities;
    // Editable density field
    EditText vDensity;

    /**
     * Dynamically initialize form elements for the chosen shape.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metal_calculate);

        // Get selected shape type and add image to screen
        Bundle bundle = getIntent().getExtras();
        String shapeType = bundle.getString("shape");
        currShapeData = CacheConstants.shapeTypes.get(shapeType);
        ImageView dimPic = findViewById(R.id.dimPic);
        dimPic.setImageResource(currShapeData.shapeDimPic);

        // Get all form elements
        vDensity = this.findViewById(R.id.densityField);

        // Read in JSON data on material densities
        densities = CacheConstants.readDensities(getAssets());
        // Add metal types to dropdown and create handler
        createMetalSpinner();

        // Add form elements for this shape
        shapeFieldSelectedValues = new HashMap<>();
        shapeFieldSelectedUnits = new HashMap<>();
        LinearLayout fieldsView = findViewById(R.id.shapeFieldsView);
        for(int i = 0; i < currShapeData.shapeFields.size(); i++) {
            View fieldView = getLayoutInflater().inflate(R.layout.shape_field, null);
            ShapeType.ShapeTypeFieldInfo field = currShapeData.shapeFields.get(i);

            String fieldName = field.fieldName;
            String fieldType = field.fieldType;

            int fieldTypeNum;
            if (fieldType.equals("number")) {
                fieldTypeNum = InputType.TYPE_CLASS_NUMBER;
            }
            else if (fieldType.equals("decimal")) {
                fieldTypeNum = InputType.TYPE_NUMBER_FLAG_DECIMAL;
            }
            else {
                System.out.println("Invalid field type for shape input field.");
                return;
            }
            // Set input field values
            EditText numericInput = fieldView.findViewById(R.id.fieldData);
            numericInput.setHintTextColor(Color.GRAY);
            numericInput.setTextColor(Color.BLACK);
            numericInput.setHint(fieldName);
            numericInput.setInputType(fieldTypeNum);

            // Populate spinner with available units
            Spinner unitInput = fieldView.findViewById(R.id.fieldUnits);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    field.fieldUnits);
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
        RadioGroup radioGroup = findViewById(R.id.calculationOptionRadioGroup);
        calculationController = new RadioViewController(this);
        radioGroup.setOnCheckedChangeListener(calculationController);
        radioGroup.check(R.id.calculateByLengthRadioBtn);
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
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                String selectedMaterial =
                        ((AppCompatTextView) selectedItemView).getText().toString();
                vDensity.setText(densities.get(selectedMaterial).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                vDensity.setText("");
            }
        });
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

    /**
     * Save piece info to local storage so that it can be displayed on the main screen.
     *
     * @param v Button that was clicked to save information.
     */
    public void savePieceInfo(View v) {
        // TODO
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Calculate and store results for the piece based on stored values.
     *
     * @param v
     */
    public void calculatePieceInfo(View v) {
        List<Object> fieldValues = new LinkedList<>();
        for(ShapeType.ShapeTypeFieldInfo fInfo : currShapeData.shapeFields) {
            // Get unit-adjusted value of field
            double fieldValue;
            try {
                fieldValue = Double.parseDouble(
                        shapeFieldSelectedValues.get(fInfo).getText().toString());
            }
            catch(NumberFormatException e) {
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
        // Inputs
        double density;
        try {
            density = Double.parseDouble(vDensity.getText().toString());
        }
        catch(NumberFormatException e) {
            e.printStackTrace();
            density = 0;
        }
        double kg_price = 1.0;
        int num_pieces = 1;
        double length_per_piece = 0;
        // Outputs
        double price_per_piece = 0;
        double volume_per_piece = 0;
        double surface_area_per_piece = 0;
        double mass_per_piece = 0;
        double total_mass = 0;
        double total_surface_area = 0;
        double total_price = 0;
        String results_str;

        // Get calculation option and run calculation with field inputs
        RadioGroup calculationOptionRadioGroup = findViewById(R.id.calculationOptionRadioGroup);
        int selectedCalculation = calculationOptionRadioGroup.getCheckedRadioButtonId();
        switch(selectedCalculation) {
            case R.id.calculateByLengthRadioBtn:
                // Compute volume
                length_per_piece = 1.0; // TODO replace with field input
                volume_per_piece = length_per_piece * area;
                mass_per_piece = volume_per_piece * density;
                break;
            case R.id.calculateByWeightRadioBtn:
                mass_per_piece = 1.0; // TODO replace with field input
                volume_per_piece = mass_per_piece / density;
                length_per_piece = volume_per_piece / area;
                break;
        }
        price_per_piece = kg_price * mass_per_piece;
        total_price = price_per_piece * num_pieces;
        surface_area_per_piece = length_per_piece * perimeter;
        total_mass = mass_per_piece * num_pieces;

        // TODO print results to screen, and add values to saved piece info
    }

    /**
     * Helper method that interprets the area calculation provided as a string of Javascript in the
     * shape descriptor JSON file in order to compute the area.
     *
     * @param calculation String containing the javascript describing the calculation. The shape's
     *                    field parameters will be provided, in order, to the function.
     * @param args Field parameters provided to the calculation.
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
        }
        finally {
            org.mozilla.javascript.Context.exit();
        }
        return area;
    }


    /**
     * When the user selects "by weight" or "by length" calculations, this helper class does the
     * magic to change the UI and the data elements backing the UI.
     */
    private class RadioViewController implements RadioGroup.OnCheckedChangeListener {
        /**
         * Lists populated upon construction that contain the elements to display
         * for each choice of calculation.
         */
        List<View> calculateByLengthViewList;
        List<View> calculateByWeightViewList;

        /**
         * Currently displayed list of fields
         */
        List<View> currentViewList;

        /**
         * Activity context (provided by constructor), used by field construction methods
         */
        Context ctx;


        public RadioViewController(Context ctx) {
            currentViewList = new LinkedList<>();
            this.ctx = ctx;

            calculateByLengthViewList = createViewsForCalculateByLength();
            calculateByWeightViewList = createViewsForCalculateByWeight();
        }

        /**
         * Executes whenever the choice of calculation is changed.
         *
         * @param group UI element of the calculation choice radio group.
         * @param checkedId ID of selected UI element.
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            LinearLayout allFields = findViewById(R.id.optionFieldsView);

            for(View v : currentViewList) {
                allFields.removeView(v);
            }

            // checkedId is the RadioButton selected
            switch(checkedId) {
                case R.id.calculateByLengthRadioBtn:
                    currentViewList = calculateByLengthViewList;
                    break;
                case R.id.calculateByWeightRadioBtn:
                    currentViewList = calculateByWeightViewList;
                    break;
            }

            for(View v : currentViewList) {
                allFields.addView(v);
            }
        }

        private List<View> createViewsForCalculateByLength() {
            List<View> viewList = new LinkedList<>();

            // Create length view
            View lengthView = getLayoutInflater().inflate(R.layout.shape_field, null);
            EditText numericInput = lengthView.findViewById(R.id.fieldData);
            numericInput.setHint("Length");
            numericInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            Spinner unitInput = lengthView.findViewById(R.id.fieldUnits);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx,
                    android.R.layout.simple_spinner_dropdown_item,
                    CacheConstants.lengthUnits.keySet().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            unitInput.setAdapter(adapter);
            viewList.add(lengthView);

            viewList.add(createPiecesView());

            // Create kg price view
            EditText kgPriceInput = new EditText(ctx);
            kgPriceInput.setHint("Kg Price");
            kgPriceInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            viewList.add(kgPriceInput);

            return viewList;
        }

        private List<View> createViewsForCalculateByWeight() {
            List<View> viewList = new LinkedList<>();

            // Create mass view
            View massView = getLayoutInflater().inflate(R.layout.shape_field, null);
            EditText numericInput = massView.findViewById(R.id.fieldData);
            numericInput.setHint("Weight");
            numericInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            Spinner unitInput = massView.findViewById(R.id.fieldUnits);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx,
                    android.R.layout.simple_spinner_dropdown_item,
                    CacheConstants.weightUnits.keySet().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            unitInput.setAdapter(adapter);
            viewList.add(massView);

            viewList.add(createPiecesView());

            return viewList;
        }

        private View createPiecesView() {
            // Create length view
            EditText piecesInput = new EditText(ctx);
            piecesInput.setHint("Pieces");
            piecesInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            return piecesInput;
        }
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

        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if(!detector.isOperational()) {
            System.out.println("Could not set up the detector!");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        Barcode thisCode = barcodes.valueAt(0);
        System.out.println(thisCode.rawValue);
    }
}
