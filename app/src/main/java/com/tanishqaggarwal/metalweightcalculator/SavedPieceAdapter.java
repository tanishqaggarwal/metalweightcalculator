package com.tanishqaggarwal.metalweightcalculator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class SavedPieceAdapter extends RecyclerView.Adapter<SavedPieceAdapter.SavedPieceCardHolder> {
    public List<SavedPieceInfo> savedPieceInfoList;

    public SavedPieceAdapter() {
        this.savedPieceInfoList = new LinkedList<>();
    }

    @Override
    public int getItemCount() {
        return savedPieceInfoList.size();
    }

    @Override
    public void onBindViewHolder(SavedPieceAdapter.SavedPieceCardHolder savedPieceCardHolder, int i) {
        SavedPieceInfo spi = savedPieceInfoList.get(i);
        savedPieceCardHolder.vShapeType.setText(spi.shapeType.shapeName);
        savedPieceCardHolder.vWidthDescription.setText(spi.widthDescription);
        savedPieceCardHolder.vPieceLength.setText("Length: " + spi.pieceLength + " ft");
        savedPieceCardHolder.vPieceWeight.setText("Piece weight: " + spi.pieceWeight + " lb");
        savedPieceCardHolder.vTotalWeight.setText("Total weight (" + spi.numPieces + " pieces): " + spi.pieceWeight * spi.numPieces + " lb");
        savedPieceCardHolder.vTotalCost.setText("Total cost: " + spi.numPieces * spi.pieceCost);
    }

    @Override
    public SavedPieceAdapter.SavedPieceCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_saved_piece, viewGroup, false);

        return new SavedPieceAdapter.SavedPieceCardHolder(itemView);
    }

    public class SavedPieceCardHolder extends RecyclerView.ViewHolder {
        protected TextView vShapeType;
        protected TextView vWidthDescription;
        protected TextView vPieceLength;
        protected TextView vPieceWeight;
        protected TextView vTotalWeight;
        protected TextView vTotalCost;

        public SavedPieceCardHolder(View v) {
            super(v);
            vShapeType = v.findViewById(R.id.shapeType);
            vWidthDescription = v.findViewById(R.id.pieceWidthDescription);
            vPieceLength = v.findViewById(R.id.pieceLength);
            vPieceWeight = v.findViewById(R.id.pieceWeight);
            vTotalWeight = v.findViewById(R.id.totalWeight);
            vTotalCost = v.findViewById(R.id.totalCost);
        }
    }
}
