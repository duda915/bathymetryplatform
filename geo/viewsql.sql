SELECT bathymetry_meta.id, bathymetry_meta.name, bathymetry_meta.collection_date, bathymetry_meta.author,
bathymetry.coords, bathymetry.measure
FROM bathymetry
INNER JOIN bathymetry_meta ON bathymetry.meta_id = bathymetry_meta.id WHERE bathymetry_meta.Id IN ( %selection% )


-- selection regexp
-- ^[0-9\,]+$

-- url param example
-- &viewparams=selection:2%5C,1