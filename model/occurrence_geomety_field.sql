-- ajout d'une colonne geometrique dans la table occurrence

ALTER TABLE occurrence ADD COLUMN geom geometry(POINT,4326); --projection WGS84
-- ALTER TABLE occurrence ADD COLUMN geomLambert geometry(POINT,29702); --projection 29702


-- Update the geometry column:
UPDATE occurrence SET geom = ST_SetSRID(ST_MakePoint(cast(decimalLongitude as float), cast(decimalLatitude as float)),4326);
-- UPDATE occurrence SET geomLambert = ST_SetSRID(ST_MakePoint(cast(decimalLongitude as float), cast(decimalLatitude as float)),29702);

-- création d'une index sur la nouvelle colonne
CREATE INDEX idx_occurrence_geom ON occurrence USING GIST(geom);
-- CREATE INDEX idx_occurrence_geomLambert ON occurrence USING GIST(geomLambert);

-- SELECT id, ST_AsText(geom) as lambertCoord, ST_AsText(geom) as WGS84Coord FROM occurrence GROUP BY id ORDER BY id;

-- SELECT o.id, ST_AsText(o.geomLambert), o.stateprovince FROM occurrence o, lim_region_aout06_29702 reg WHERE ST_CONTAINS(reg.geom, o.geomLambert)='t'


