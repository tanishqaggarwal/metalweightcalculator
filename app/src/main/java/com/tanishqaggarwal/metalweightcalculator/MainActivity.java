package com.tanishqaggarwal.metalweightcalculator;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tanishqaggarwal.metalweightcalculator.adapters.SavedPieceAdapter;
import com.tanishqaggarwal.metalweightcalculator.listners.RecyclerClickListner;
import com.tanishqaggarwal.metalweightcalculator.models.SavedPiece;
import com.tanishqaggarwal.metalweightcalculator.utils.CacheConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Application entry point.
 */
public class MainActivity extends AppCompatActivity {
    Realm mRealm;
    SavedPieceAdapter sa;
    private static final int BUFFER = 80000;
    ArrayList<String> images = new ArrayList<>();
    File file;

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
        CacheConstants.readShapeData(getAssets(), this);
        RecyclerView recList = findViewById(R.id.savedPiecesListView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        sa = new SavedPieceAdapter(new RecyclerClickListner() {
            @Override
            public void onPositionClicked(int position) {
                Toast.makeText(MainActivity.this, "Loomg Press to del item", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClicked(int position) {
                Log.d(">>>", sa.savedPiecesList.get(position).ShapeName);
                deleteRecord(sa.savedPiecesList.get(position).id);
                readRecords();
            }
        });
        recList.setAdapter(sa);
        readRecords();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readRecords();
    }

    private void deleteRecord(final int id) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                SavedPiece piece = realm.where(SavedPiece.class).equalTo("id", id).findFirst();
                if (piece != null) {
                    piece.deleteFromRealm();
                }
            }
        });
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
            images.clear();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<SavedPiece> results = realm.where(SavedPiece.class).findAll();
                    saveResulttoCSV(results);
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Need permission of this operation", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResulttoCSV(RealmResults<SavedPiece> results) {
        file = new File(Environment.getExternalStorageDirectory(), "/" + "/itemFile.csv");
        CsvWriter csvWriter = new CsvWriter();
        try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < results.size(); i++) {
                SavedPiece val = results.get(i);
                if (i == 0) {
                    csvAppender.appendLine("Shape Type", "Width (A)", "Diameter (D)", "Diameter (S)", "Thickness (T)", "Side (A)", "Side (B)"
                            , "Width (W)", "Internal Daimeter", "Outer Diameter", "Length", "Weight", "No of piece", "Weight (Kg)", "Density", "Result");
                }
                assert val != null;
                csvAppender.appendLine(val.ShapeName, val.widthA + val.widthAU, val.diameterD + val.diameterDU
                        , val.diameterS + val.diameterSU, val.thicknessT + val.thicknessTU, val.sideA + val.sideAU
                        , val.sideB + val.sideBU, val.widthW + val.widthWU
                        , val.internalDaimeter + val.internalDaimeterU, val.outerDiameter + val.outerDiameterU
                        , val.length + val.lengthU, val.weight + val.weightU,
                        String.valueOf(val.pieceInputVal), String.valueOf(val.kgInputVal), String.valueOf(val.density), val.FinalResult);
                changeFileName(val.metalPieceImages, i);
            }
            csvAppender.endLine();

            String inputPath = Environment.getExternalStorageDirectory().getPath() + "/metalImages.zip";

            //realm already update
            if (results.size() > 0 && zip(images, inputPath))
                shareFile(inputPath, file.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void changeFileName(RealmList<String> metalPieceImages, int csvRow) {
        csvRow++;
        for (int ii = 0; ii < metalPieceImages.size(); ii++) {
            try {
                int subfileName = ii;
                String sourcePath = metalPieceImages.get(ii);
                String destinationPath = Environment.getExternalStorageDirectory() + "/" + csvRow + "-" + ++subfileName + ".jpg";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Path source = Paths.get(sourcePath);
                    Path destination = Paths.get(destinationPath);
                    File fdelete = new File(destination.toUri());
                    if (fdelete.exists()) {
                        fdelete.delete();
                        Files.copy(source, destination);
                    } else
                        Files.copy(source, destination);
                } else {
                    try (InputStream in = new FileInputStream(sourcePath)) {
                        try (OutputStream out = new FileOutputStream(destinationPath)) {
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                }

                Log.d(">>>", destinationPath);
                images.add(destinationPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean zip(ArrayList<String> _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void shareFile(String zipfile, String csvfile) {
        Log.d(">>>", "Share File path" + file);
        File zipfile_ = new File(zipfile);
        File csvfile_ = new File(csvfile);
        Uri zippath_ = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName(), zipfile_);
        Uri csvpath_ = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName(), csvfile_);

        Log.d(">>>", "Share Uri zip" + zippath_);
        Log.d(">>>", "Share Uri csv" + csvpath_);

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        // set the type to 'email'
        emailIntent.setType("*/email");
        // the attachment
        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(zippath_);
        uris.add(csvpath_);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share file");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void readRecords() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
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
