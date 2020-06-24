package kde_covid_kor.covidDB;


/*
Convert GPS to X, Y axis using Mercator Projections.

https://www.baeldung.com/java-convert-latitude-longitude
*/

public abstract class Mercator {
    final static double RADIUS_MAJOR = 6378137.0;
    final static double RADIUS_MINOR = 6356752.3142;
 
    abstract double yAxisProjection(double input);
    abstract double xAxisProjection(double input);
}
