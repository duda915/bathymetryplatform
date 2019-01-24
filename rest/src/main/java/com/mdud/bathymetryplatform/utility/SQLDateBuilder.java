package com.mdud.bathymetryplatform.utility;

import java.sql.Date;

public class SQLDateBuilder {
    public static Date now() {
        return new Date(new java.util.Date().getTime());
    }
}
