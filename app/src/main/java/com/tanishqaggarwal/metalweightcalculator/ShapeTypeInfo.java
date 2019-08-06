package com.tanishqaggarwal.metalweightcalculator;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shape type data.
 */
public class ShapeTypeInfo {
    protected String shapeName;
    protected List<ShapeTypeFieldInfo> shapeFields;
    protected Drawable shapeIcon;
    protected Drawable shapeImg;

    /**
     * Constructor.
     *
     * @param shapeName
     * @param shapeImg
     * @param shapeFields
     */
    public ShapeTypeInfo(String shapeName, Drawable shapeIcon, Drawable shapeImg, List<ShapeTypeFieldInfo> shapeFields) {
        this.shapeName = shapeName;
        this.shapeIcon = shapeIcon;
        this.shapeImg = shapeImg;
        this.shapeFields = shapeFields;
    }

    /**
     * Metadata fields required to fully describe a shape numerically.
     */
    public static class ShapeTypeFieldInfo {
        String fieldName;
        String fieldType;
        List<String> fieldUnits;
        ShapeTypeFieldInfo(String fieldName, String fieldType, List<String> fieldUnits) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldUnits = fieldUnits;
        }
    }

    /**
     * Reads shape data from XML file and returns it, for consumption by ShapeListActivity.
     *
     * @param assets AssetManager used to get access to XML file.
     * @return Map from shape name to shape image and field data.
     */
    public static Map<String, ShapeTypeInfo> readShapeData(AssetManager assets) {
        XmlPullParser xmlParser = Xml.newPullParser();
        try {
            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(assets.open("shape_type_data.xml"), null);
        }
        catch(XmlPullParserException e) {
            System.out.println("XML parser exception");
        }
        catch(IOException e) {
            System.out.println("Can't read shape type data file");
        }

        // TODO
        return new HashMap<>();
    }
}
