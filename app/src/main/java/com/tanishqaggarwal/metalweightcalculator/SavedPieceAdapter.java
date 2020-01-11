package com.tanishqaggarwal.metalweightcalculator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;


/**
 * Adapter that allows binding of data of a saved metal piece to the RecyclerView
 * in the main activity that displays all saved pieces.
 */
public class SavedPieceAdapter extends RecyclerView.Adapter<SavedPieceAdapter.SavedPieceCardHolder> {
    public List<SavedPiece> savedPiecesList;
    private  SavedPiece spi;

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
        spi = savedPiecesList.get(i);
        savedPieceCardHolder.vShapeType.setText("Type:" + spi.ShapeName);

        checkVal(savedPieceCardHolder.width_a, spi.widthA);
        savedPieceCardHolder.width_a.setText("Width (A):" + spi.widthA + " " + spi.widthAU);

        checkVal(savedPieceCardHolder.width_w, spi.widthW);
        savedPieceCardHolder.width_w.setText("Width (W): " + spi.widthW + " " + spi.widthWU);

        checkVal(savedPieceCardHolder.length, spi.length);
        savedPieceCardHolder.length.setText("Length: " + spi.length + " " + spi.lengthU);

        checkVal(savedPieceCardHolder.weight, spi.weight);
        savedPieceCardHolder.weight.setText("Piece weight: " + spi.weight + " " + spi.weightU);

        checkVal(savedPieceCardHolder.diameter_d, spi.diameterD);
        savedPieceCardHolder.diameter_d.setText("Diameter (D):" + spi.diameterD + " " + spi.diameterDU);

        checkVal(savedPieceCardHolder.diameter_s, spi.diameterS);
        savedPieceCardHolder.diameter_s.setText("Diameter (S): " + spi.diameterS + " " + spi.diameterSU);

        checkVal(savedPieceCardHolder.internal_daimeter, spi.internalDaimeter);
        savedPieceCardHolder.internal_daimeter.setText("Internal Daimeter: " + spi.internalDaimeter + " " + spi.internalDaimeterU);

        checkVal(savedPieceCardHolder.outer_daimeter, spi.outerDiameter);
        savedPieceCardHolder.outer_daimeter.setText("Outer Daimeter:" + spi.outerDiameter + " " + spi.outerDiameterU);

        checkVal(savedPieceCardHolder.side_a, spi.sideA);
        savedPieceCardHolder.side_a.setText("Side (A):" + spi.sideA + " " + spi.sideAU);

        checkVal(savedPieceCardHolder.side_b, spi.sideB);
        savedPieceCardHolder.side_b.setText("Side (B):" + spi.sideB + " " + spi.sideBU);

        checkVal(savedPieceCardHolder.thickness, spi.thicknessT);
        savedPieceCardHolder.thickness.setText("Thickness (T):" + spi.thicknessT + " " + spi.thicknessTU);

        checkVal(savedPieceCardHolder.value_per_kg, spi.kgInputVal);
        savedPieceCardHolder.value_per_kg.setText("Value per kg: " + spi.kgInputVal);

        checkVal(savedPieceCardHolder.piece_input_value, spi.pieceInputVal);
        savedPieceCardHolder.piece_input_value.setText("Piece input value: " + spi.pieceInputVal);

        checkVal(savedPieceCardHolder.density, spi.density);
        savedPieceCardHolder.density.setText("Density:" + spi.density);

        savedPieceCardHolder.delItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(spi.ShapeName);
            }
        });
    }

    private void checkVal(TextView tv, double data) {
        if (data == 0)
            tv.setVisibility(View.GONE);
    }

    private void deleteRecord(final String name) {
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SavedPiece piece = realm.where(SavedPiece.class).equalTo("ShapeName", name).findFirst();
                if (piece != null) {
                    piece.deleteFromRealm();
                }
            }
        });
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
        LinearLayout delItem;

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
            delItem = v.findViewById(R.id.del_item);
        }
    }
}
