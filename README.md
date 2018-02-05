# Spatial Spring

Spring Boot + Hibernate Spatial + PostGIS

Swagger: (http://localhost:4326/swagger-ui.html)

##Goals
- [ ] CRUD for geospatial data
    - [x] [POST] : `/location/`
    - [x] [GET] : `/locations/`
    - [x] [GET] by ID : `/location/{id}`
    - [ ] [GET] by location
    - [x] [PUT] : `/location/{id}`
    - [ ] [PATCH] 
    - [x] [DELETE] : `/location/{id}`
- [ ] Transform — return a new geometry with its coordinates transformed to a different spatial reference.
- [ ] Spatial Analysis (for example: Geometry buffer) 