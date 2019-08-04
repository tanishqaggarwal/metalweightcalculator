package com.tanishqaggarwal.metalweightcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

        ShapeTypeAdapter sa = new ShapeTypeAdapter(getShapeList());
        recList.setAdapter(sa);
    }

    public List<ShapeTypeInfo> getShapeList() {
        ArrayList<ShapeTypeInfo> shapeList = new ArrayList<>();
        shapeList.add(new ShapeTypeInfo("shape", getDrawable(R.drawable.pic1pro)));
        return shapeList;
    }

    public void goToMetalCalculate(View v) {
        Intent intent = new Intent(this, MetalCalculateActivity.class);
        startActivity(intent);
    }
}
