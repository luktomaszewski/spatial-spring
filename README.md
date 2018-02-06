# Spatial Spring

Spring Boot + Hibernate Spatial + PostGIS

## Goals
- [ ] CRUD for geospatial data (geometry as GeoJSON)
    - [x] [POST] : `/location/`
    - [x] [GET] by ID : `/location/{id}`
    - [x] [PUT] : `/location/{id}`
    - [ ] [PATCH]
    - [x] [DELETE] : `/location/{id}`
    - [x] [GET] : `/locations/`
    - [x] [POST] Get locations within specified geometry (GeoJSON) : `/locations/within`
- [ ] Transform — return a new geometry with its coordinates transformed to a different spatial reference.
- [ ] Spatial Analysis (for example: Geometry buffer) 