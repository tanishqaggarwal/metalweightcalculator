package com.tanishqaggarwal.metalweightcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recList = findViewById(R.id.savedPiecesList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        SavedPieceAdapter sa = new SavedPieceAdapter();
        recList.setAdapter(sa);

        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 12, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 10, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 13, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 124, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 125, 12, 12, 12));
        sa.savedPieceInfoList.add(new SavedPieceInfo("shape", "Width: 12 in", 12, 12, 12, 12));
    }

    public void goToAddItem(View v) {
        Intent intent = new Intent(this, ShapeListActivity.class);
        startActivity(intent);
    }
}
