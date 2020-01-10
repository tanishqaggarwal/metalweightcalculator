package com.tanishqaggarwal.metalweightcalculator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


/**
 * Adapter that allows binding of data of a saved metal piece to the RecyclerView
 * in the main activity that displays all saved pieces.
 */
public class SavedPieceAdapter extends RecyclerView.Adapter<SavedPieceAdapter.SavedPieceCardHolder> {
    public List<SavedPiece> savedPiecesList;

    public SavedPieceAdapter() {
        this.savedPiecesList = new LinkedList<>();
    }

    @Override
    public int getItemCount() {
        return savedPiecesList.size();
    }

    /**
     * Populates newly recycled view holder with data from a previous view holder.
     *
     * @param savedPieceCardHolder Old view holder.
     * @param i                    Index of new view holder.
     */
    @Override
    public void onBindViewHolder(SavedPieceAdapter.SavedPieceCardHolder savedPieceCardHolder, int i) {
        SavedPiece spi = savedPiecesList.get(i);
        savedPieceCardHolder.vShapeType.setText("Type:" + spi.ShapeName);
        savedPieceCardHolder.width_a.setText("Width (A):" + spi.widthA + " " + spi.widthAU);
        savedPieceCardHolder.width_w.setText("Width (W): " + spi.widthW + " " + spi.widthWU);

        savedPieceCardHolder.length.setText("Length: " + spi.length + " " + spi.lengthU);
        savedPieceCardHolder.weight.setText("Piece weight: " + spi.weight + " " + spi.weightU);

        savedPieceCardHolder.diameter_d.setText("Diameter (D):" + spi.diameterD + " " + spi.diameterDU);
        savedPieceCardHolder.diameter_s.setText("Diameter (S): " + spi.diameterS + " " + spi.diameterSU);

        savedPieceCardHolder.internal_daimeter.setText("Internal Daimeter: " + spi.internalDaimeter + " " + spi.internalDaimeterU);
        savedPieceCardHolder.outer_daimeter.setText("Outer Daimeter:" + spi.outerDiameter + " " + spi.outerDiameterU);

        savedPieceCardHolder.side_a.setText("Side (A):" + spi.sideA + " " + spi.sideAU);
        savedPieceCardHolder.side_b.setText("Side (B):" + spi.sideB + " " + spi.sideBU);

        savedPieceCardHolder.thickness.setText("Thickness (T):" + spi.thicknessT + " " + spi.thicknessTU);

        savedPieceCardHolder.piece_input_value.setText("Piece input value: " + spi.pieceInputVal);
        savedPieceCardHolder.value_per_kg.setText("Value per kg: " + spi.kgInputVal);
        savedPieceCardHolder.density.setText("Density:" + spi.density);

    }

    /**
     * Inflates a RecyclerView-provided view with the layout of a saved piece.
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public SavedPieceAdapter.SavedPieceCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_saved_piece, viewGroup, false);

        return new SavedPieceAdapter.SavedPieceCardHolder(itemView);
    }

    /**
     * Decomposes the ViewHolder provided by RecyclerView into the data fields relevant to a saved
     * piece.
     */
    public class SavedPieceCardHolder extends RecyclerView.ViewHolder {
        protected TextView vShapeType;
        protected TextView width_a, width_w, density;
        protected TextView length, piece_input_value, value_per_kg;
        protected TextView outer_daimeter, side_a, side_b, thickness;
        protected TextView weight, diameter_d, diameter_s, internal_daimeter;

        public SavedPieceCardHolder(View v) {
            super(v);
            vShapeType = v.findViewById(R.id.shapeType);
            width_a = v.findViewById(R.id.width_a);
            width_w = v.findViewById(R.id.width_w);
            length = v.findViewById(R.id.length);
            weight = v.findViewById(R.id.weight);
            diameter_d = v.findViewById(R.id.diameter_d);
            diameter_s = v.findViewById(R.id.diameter_s);
            internal_daimeter = v.findViewById(R.id.internal_daimeter);
            outer_daimeter = v.findViewById(R.id.outer_daimeter);
            side_a = v.findViewById(R.id.side_a);
            side_b = v.findViewById(R.id.side_b);
            thickness = v.findViewById(R.id.thickness);
            piece_input_value = v.findViewById(R.id.piece_input_value);
            value_per_kg = v.findViewById(R.id.value_per_kg);
            density = v.findViewById(R.id.density);
        }
    }
}
