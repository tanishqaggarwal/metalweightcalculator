package com.tanishqaggarwal.metalweightcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;


/**
 * Application entry point.
 */
public class MainActivity extends AppCompatActivity {
    Realm mRealm;
    SavedPieceAdapter sa;
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
        // TODO
    }

    private void readRecords() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SavedPiece> results = realm.where(SavedPiece.class).findAll();
                sa.savedPiecesList.addAll(results);
            }
        });
    }
    private void deleteRecord() {
//        mRealm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                SavedPiece employee = realm.where(SavedPiece.class).equalTo(SavedPiece.PROPERTY_NAME, inName.getText().toString()).findFirst();
//                if (employee != null) {
//                    employee.deleteFromRealm();
//                }
//            }
//        });
    }
}
