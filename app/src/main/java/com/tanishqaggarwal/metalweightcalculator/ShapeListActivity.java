package com.tanishqaggarwal.metalweightcalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    public final String[] longLengthUnits = {"ft", "m"};
    public final String[] shortLengthUnits = {"mm", "cm", "in"};
    public final String[] weightUnits = {"kg", "lb"};

    public List<ShapeTypeInfo> getShapeList() {
        ArrayList<ShapeTypeInfo> shapeList = new ArrayList<>();

        List<ShapeTypeInfo.ShapeTypeFieldInfo> fields_list = new ArrayList<>();

        ShapeTypeInfo.ShapeTypeFieldInfo shape1 = new ShapeTypeInfo.ShapeTypeFieldInfo(
                "Width (A)",
                "decimal",
                Arrays.asList(shortLengthUnits));
        fields_list.add(shape1);

        shapeList.add(new ShapeTypeInfo("shape", getDrawable(R.drawable.pic1pro), getDrawable(R.drawable.pic1pro), fields_list));
        return shapeList;
    }
}
