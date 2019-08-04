package com.tanishqaggarwal.metalweightcalculator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ShapeTypeAdapter extends RecyclerView.Adapter<ShapeTypeAdapter.ShapeTypeCardHolder> {

    List<ShapeTypeInfo> shapeTypeInfoList;

    public ShapeTypeAdapter(List<ShapeTypeInfo> shapeTypeInfoList) {
        this.shapeTypeInfoList = shapeTypeInfoList;
    }

    @Override
    public int getItemCount() {
        return shapeTypeInfoList.size();
    }

    @Override
    public void onBindViewHolder(ShapeTypeCardHolder shapeTypeCardHolder, int i) {
        ShapeTypeInfo si = shapeTypeInfoList.get(i);
        shapeTypeCardHolder.vShapeName.setText(si.shapeName);
        shapeTypeCardHolder.vShapeImg.setImageDrawable(si.shapeImg);
    }

    @Override
    public ShapeTypeCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_shape_type, viewGroup, false);

        return new ShapeTypeCardHolder(itemView);
    }

    public class ShapeTypeCardHolder extends RecyclerView.ViewHolder {
        protected TextView vShapeName;
        protected ImageView vShapeImg;

        public ShapeTypeCardHolder(View v) {
            super(v);
            vShapeName = v.findViewById(R.id.shapeName);
            vShapeImg = v.findViewById(R.id.shapeImg);
        }
    }
}
