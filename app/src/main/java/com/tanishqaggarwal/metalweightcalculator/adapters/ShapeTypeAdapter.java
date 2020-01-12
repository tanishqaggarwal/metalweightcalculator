package com.tanishqaggarwal.metalweightcalculator.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanishqaggarwal.metalweightcalculator.MetalCalculateActivity;
import com.tanishqaggarwal.metalweightcalculator.MyApplication;
import com.tanishqaggarwal.metalweightcalculator.R;
import com.tanishqaggarwal.metalweightcalculator.models.ShapeType;

import java.util.List;

/**
 * Adapter that allows listing of available metal shapes.
 */
public class ShapeTypeAdapter extends RecyclerView.Adapter<ShapeTypeAdapter.ShapeTypeCardHolder> {

    private List<ShapeType> shapeTypeList;

    public ShapeTypeAdapter(List<ShapeType> shapeTypeList) {
        this.shapeTypeList = shapeTypeList;
    }

    @Override
    public int getItemCount() {
        return shapeTypeList.size();
    }

    /**
     * Populates newly recycled view holder with data from a previous view holder.
     *
     * @param shapeTypeCardHolder Old view holder.
     * @param i                   Index of new view holder.
     */
    @Override
    public void onBindViewHolder(ShapeTypeCardHolder shapeTypeCardHolder, int i) {
        ShapeType si = shapeTypeList.get(i);
        shapeTypeCardHolder.vShapeName.setText(si.shapeName);
        shapeTypeCardHolder.vShapeImg.setImageResource(si.shapeIcon);
    }

    /**
     * Upon creation of a view holder, inflate it with the shape type layout.
     *
     * @param viewGroup Provided view holder.
     * @param i         Index of element in recycler view list.
     * @return Shape type layout.
     */
    @NonNull
    @Override
    public ShapeTypeCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_shape_type, viewGroup, false);

        return new ShapeTypeCardHolder(itemView);
    }

    /**
     * Decomposes the ViewHolder provided by RecyclerView into the data fields relevant to a shape
     * type.
     */
    class ShapeTypeCardHolder extends RecyclerView.ViewHolder {
        private TextView vShapeName;
        private ImageView vShapeImg;

        private ShapeTypeCardHolder(View v) {
            super(v);
            vShapeName = v.findViewById(R.id.shapeName);
            vShapeImg = v.findViewById(R.id.shapeImg);
            final Context ctx = v.getContext();
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open metal calculator and tell it the shape that was selected
                    String chosenShape = vShapeName.getText().toString();
                    MyApplication.shapeType=chosenShape;
                    Intent intent = new Intent(ctx, MetalCalculateActivity.class);
                    intent.putExtra("shape", chosenShape);
                    ctx.startActivity(intent);
                }
            });
        }
    }
}
