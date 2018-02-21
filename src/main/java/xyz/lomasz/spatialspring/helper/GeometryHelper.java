package xyz.lomasz.spatialspring.helper;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

public class GeometryHelper {

    public static org.wololo.geojson.Geometry convertJtsGeometryToGeoJson(Geometry geometry) {
        return new GeoJSONWriter().write(geometry);
    }

    public static Geometry convertGeoJsonToJtsGeometry(org.wololo.geojson.Geometry geoJson) {
        return new GeoJSONReader().read(geoJson);
    }

    public static Geometry transformGeometry(Integer inCrs, Integer outCrs, Geometry geometry) throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:" + inCrs);
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + outCrs);
        geometry.setSRID(inCrs);
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry transformedGeometry = JTS.transform(geometry, transform);
        transformedGeometry.setSRID(outCrs);
        return transformedGeometry;
    }
}
