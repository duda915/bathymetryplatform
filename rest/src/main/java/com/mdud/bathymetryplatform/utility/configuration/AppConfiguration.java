package com.mdud.bathymetryplatform.utility.configuration;

public interface AppConfiguration {
    String getGDALTargetLocation();

    String getDBHost();
    String getDBPort();
    String getDBName();
    String getDBUsername();
    String getDBPassword();

    String getGeoServerHost();
    String getGeoServerUser();
    String getGeoServerPassword();
    String getGeoServerWorkspaceName();
    String getGeoServerCoverageStoresPath();
}
