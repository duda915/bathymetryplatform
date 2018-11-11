Usage of this data is in accordance to your user profile terms of use.  The 
terms of use can be found here: http://www.epsg-registry.org/help/xml/Terms_Of_Use.html

To install this package, run the included scripts in the following order:
1) PostgreSQL script to create the database tables (PostgreSQL_Table_Script.sql)
2) PostgreSQL script to insert the data (PostgreSQL_Data_Script.sql)
3) PostgreSQL script to create the database table constraints PostgreSQL_FKey_Script.sql)

Note that these scripts have been tested on PostgreSQL 9.6  and use the UTF-89 
encoding.  If your database uses a different encoding you may encounter problems 
with international characters.

If you have any comments or feedback, a link to a contact form can be found at 
the geodetic registry website: http://www.epsg-registry.org
