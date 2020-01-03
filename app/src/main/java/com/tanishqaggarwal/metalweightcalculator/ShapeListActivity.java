package com.tanishqaggarwal.metalweightcalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShapeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_list);
        RecyclerView recList = findViewById(R.id.shapeList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        List<ShapeType> shapeTypes = new ArrayList<>(CacheConstants.shapeTypes.values());
        ShapeTypeAdapter sa = new ShapeTypeAdapter(shapeTypes);
        recList.setAdapter(sa);
    }
}
