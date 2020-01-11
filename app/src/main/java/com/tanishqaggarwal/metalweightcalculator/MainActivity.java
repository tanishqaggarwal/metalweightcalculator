package com.tanishqaggarwal.metalweightcalculator;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import io.realm.Realm;
import io.realm.RealmResults;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Application entry point.
 */
public class MainActivity extends AppCompatActivity {
    Realm mRealm;
    SavedPieceAdapter sa;
    String fileName = "PieceCsv";
    public final int WRITE_PERMISSON_REQUEST_CODE = 1;

    /**
     * Function that is run upon initialization of application.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getDefaultInstance();
        checkPermission();
        // Read available shape types and make the data available to all classes
        // via the static class variables
        CacheConstants.readShapeData(getAssets(), getApplicationContext());
//        addPiece();
        // Create recycler view for saved pieces
        RecyclerView recList = findViewById(R.id.savedPiecesListView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        sa = new SavedPieceAdapter();
        recList.setAdapter(sa);
        readRecords();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readRecords();
    }

    /**
     * Allows user to add item to the calculator.
     *
     * @param v
     */
    public void goToAddItem(View v) {
        Intent intent = new Intent(this, ShapeListActivity.class);
        startActivity(intent);
    }

    /**
     * Exports current saved piece list to online location.
     *
     * @param v Button.
     */
    public void exportList(View v) {
        if (checkPermission()) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<SavedPiece> results = realm.where(SavedPiece.class).findAll();
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    File file = new File(path, "/" + "foo.csv");
                    CsvWriter csvWriter = new CsvWriter();
                    try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
                        for (int i = 0; i < results.size(); i++) {
                            SavedPiece val = results.get(i);
                            if (i == 0) {
                                csvAppender.appendLine("Shape Type.Width (A)", "Diameter (D)", "Diameter (S)", "Thickness (T)", "Side (A)", "Side (B)"
                                        , "Width (W)", "Internal Daimeter", "Outer Diameter", "Length", "Weight", "No of piece", "Weight (Kg)", "Density");
                            }
                            csvAppender.appendLine(val.ShapeName, val.widthA + val.widthAU, val.diameterD + val.diameterDU
                                    , val.diameterS + val.diameterSU, val.thicknessT + val.thicknessTU, val.sideA + val.sideAU
                                    , val.sideB + val.sideBU, val.widthW + val.widthWU
                                    , val.internalDaimeter + val.internalDaimeterU, val.outerDiameter + val.outerDiameterU
                                    , val.length + val.lengthU, val.weight + val.weightU,
                                    String.valueOf(val.pieceInputVal), String.valueOf(val.kgInputVal), String.valueOf(val.density));
                        }
                        csvAppender.endLine();
                        shareFile(file);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Need permission of this operation", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFile(File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(">>>", "Share File" + file.getAbsolutePath());

        Uri path = Uri.fromFile(file);
        Log.d(">>>", "Share Uri" + path);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
        emailIntent.setType("*/email");
// the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share file");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void readRecords() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                sa.savedPiecesList.clear();
                RealmResults<SavedPiece> results = realm.where(SavedPiece.class).findAll();
                sa.savedPiecesList.addAll(results);
                sa.notifyDataSetChanged();
            }
        });
    }

    private boolean checkPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need camera and storage permission", 100, perms);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
