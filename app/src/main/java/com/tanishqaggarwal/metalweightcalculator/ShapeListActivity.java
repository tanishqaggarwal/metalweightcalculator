package com.tanishqaggarwal.metalweightcalculator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tanishqaggarwal.metalweightcalculator.adapters.ShapeTypeAdapter;
import com.tanishqaggarwal.metalweightcalculator.models.ShapeType;
import com.tanishqaggarwal.metalweightcalculator.utils.CacheConstants;

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
