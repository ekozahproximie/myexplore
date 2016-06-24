package com.trimble.agmantra.filecodec.shp;

import java.util.Vector;

import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.GeoPoint;


public interface ShpParseListener {
   public boolean UpdateHeaderData (int iShapeType, BoundingBox shpFileBB);
   public boolean UpdatePolygonFeature (int fieldID, BoundingBox shpPolyBB, Vector<Vector<GeoPoint>> vecPolygons);
   public boolean UpdatePolyLineFeature (int fieldID, Vector<Vector<GeoPoint>> vecLines);
   public boolean UpdatePointFeature (int fieldID, Vector<GeoPoint> vecGeoPoints);
}
