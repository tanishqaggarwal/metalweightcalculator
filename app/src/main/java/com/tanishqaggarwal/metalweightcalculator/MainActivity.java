package com.tanishqaggarwal.metalweightcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Application entry point.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 12, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 10, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 13, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 124, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 125, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 121, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 11, 12, 12, 12));
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
}
