package com.tanishqaggarwal.metalweightcalculator.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Data of a saved metal piece.
 */
public class SavedPiece extends RealmObject {
    public double widthA = 0.0, diameterD = 0.0, diameterS = 0.0, thicknessT = 0.0,
            sideA = 0.0, sideB = 0.0, widthW = 0.0, internalDaimeter = 0.0, outerDiameter = 0.0, length = 0.0, weight = 0.0;
    public String widthAU = "", diameterDU = "", diameterSU = "", thicknessTU = "",
            sideAU = "", sideBU = "", widthWU = "", internalDaimeterU = "", outerDiameterU = "", lengthU = "", weightU = "";
    public double density = 0.0;
    public double pieceInputVal = 0.0;
    public double kgInputVal = 0.0;
    public String ShapeName = "";
    public String FinalResult = "";
    @PrimaryKey
    public int id;

    public SavedPiece() {
    }

    public SavedPiece(int id, String ShapeName, double widthA, String widthAU, double diameterD, String diameterDU, double diameterS, String diameterSU, double thicknessT, String thicknessTU, double sideA, String sideAU, double sideB, String sideBU, double widthW, String widthWU, double internalDaimeter, String internalDaimeterU, double outerDiameter, String outerDiameterU, double length, String lengthU, double weight, String weightU, double pieceInputVal, double kgInputVal, double density, String finalResult) {
        this.id = id;

        this.ShapeName = ShapeName;
        this.widthA = widthA;
        this.diameterD = diameterD;
        this.diameterS = diameterS;
        this.thicknessT = thicknessT;
        this.sideA = sideA;
        this.sideB = sideB;
        this.widthW = widthW;
        this.internalDaimeter = internalDaimeter;
        this.outerDiameter = outerDiameter;
        this.length = length;
        this.weight = weight;

        this.widthAU = widthAU;
        this.diameterDU = diameterDU;
        this.diameterSU = diameterSU;
        this.thicknessTU = thicknessTU;
        this.sideAU = sideAU;
        this.sideBU = sideBU;
        this.widthWU = widthWU;
        this.internalDaimeterU = internalDaimeterU;
        this.outerDiameterU = outerDiameterU;
        this.lengthU = lengthU;
        this.weightU = weightU;

        this.density = density;
        this.pieceInputVal = pieceInputVal;
        this.kgInputVal = kgInputVal;

        this.FinalResult = finalResult;
    }
}
