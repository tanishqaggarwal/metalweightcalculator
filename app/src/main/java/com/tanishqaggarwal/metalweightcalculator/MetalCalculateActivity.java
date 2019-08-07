package com.tanishqaggarwal.metalweightcalculator;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.util.SparseArray;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MetalCalculateActivity extends AppCompatActivity {

    List<String> currentPhotoPath;
    Map<String, Double> densities;

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
        ShapeTypeInfo shapeData = MainActivity.shapeTypes.get(shapeType);
        ImageView dimPic = findViewById(R.id.dimPic);
        dimPic.setImageResource(shapeData.shapeIcon);


        // Get all form elements
        vDensity = this.findViewById(R.id.densityField);

        // Read in JSON data on material densities
        densities = readDensities(getAssets());
        // Add metal types to dropdown and create handler
        createMetalSpinner();

        // Add form elements for this shape
        LinearLayout ll = findViewById(R.id.customFields);
        for(int i = 0; i < shapeData.shapeFields.size(); i++) {
            ShapeTypeInfo.ShapeTypeFieldInfo field = shapeData.shapeFields.get(i);
            String fieldName = field.fieldName;
            String fieldType = field.fieldType;

            int fieldTypeNum = InputType.TYPE_NUMBER_FLAG_DECIMAL;
            if (fieldType == "number") {
                fieldTypeNum = InputType.TYPE_CLASS_NUMBER;
            }
            else {
                System.out.println("Invalid field type for shape input field.");
            }

            EditText formElement = new EditText(getApplicationContext());
            formElement.setHintTextColor(Color.GRAY);
            formElement.setTextColor(Color.BLACK);
            formElement.setHint(fieldName);
            formElement.setInputType(fieldTypeNum);
            ll.addView(formElement);

            // TODO populate spinner
        }

        // Initialize photos path
        currentPhotoPath = new ArrayList<>();
    }

    public static Map<String, Double> readDensities(AssetManager assets) {
        Map<String, Double> densities = new HashMap<>();

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
                densities.put(materialName, materialDensity);
            }

            return densities;
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
                ".jpg",         /* suffix */
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
