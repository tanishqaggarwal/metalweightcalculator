package com.tanishqaggarwal.metalweightcalculator;

/**
 * Data of a saved metal piece.
 */
public class SavedPiece {
    protected ShapeType shapeType;
    protected String widthDescription;
    protected double pieceLength; // mm
    protected double pieceWeight; // kg
    protected int numPieces;
    protected double pieceCost;

    /**
     * Constructor.
     *
     * @param shapeTypename
     * @param widthDescription
     * @param pieceLength
     * @param pieceWeight
     * @param numPieces
     * @param pieceCost
     */
    public SavedPiece(String shapeTypename, String widthDescription, double pieceLength,
                      double pieceWeight, int numPieces, double pieceCost) {
        // When saving a piece, we don't need the full shape data, so we just save the name.
        this.shapeType = new ShapeType(shapeTypename, 0, 0,
                null, null);
        // We construct the rest of the fields as-is
        this.widthDescription = widthDescription;
        this.pieceLength = pieceLength;
        this.pieceWeight = pieceWeight;
        this.numPieces = numPieces;
        this.pieceCost = pieceCost;
    }
}
