package xyz.lomasz.spatialspring.controller;

import com.vividsolutions.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wololo.geojson.Feature;
import xyz.lomasz.spatialspring.helper.GeometryHelper;

@RestController
@RequestMapping("/geometry")
public class GeometryController {

    @RequestMapping(value = "/transform", method = RequestMethod.POST)
    public Feature transform(
            @RequestParam Integer inSrid,
            @RequestParam Integer outSrid,
            @RequestBody Feature feature) throws FactoryException, TransformException {

        org.wololo.geojson.Geometry geoJson = feature.getGeometry();
        Geometry sourceGeometry = GeometryHelper.convertGeoJsonToJtsGeometry(geoJson);
        Geometry targetGeometry = GeometryHelper.transformGeometry(inSrid, outSrid, sourceGeometry);

        org.wololo.geojson.Geometry targetGeoJson = GeometryHelper.convertJtsGeometryToGeoJson(targetGeometry);
        return new Feature(feature.getId(), targetGeoJson, feature.getProperties());
    }

}
