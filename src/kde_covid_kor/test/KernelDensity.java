package kde_covid_kor.test;

import java.util.Arrays;



public class KernelDensity  {
    private static final long serialVersionUID = 2L;

    /**
     * The samples to estimate the density function.
     */
    private double[] x;
    /**
     * The samples to estimate the density function.
     */
    private double[] y;
    /**
     * The kernel -- a symmetric but not necessarily positive function that
     * integrates to one. Here we just Gaussian density function.
     */
    private GaussianDistribution gaussian;
    /*
     * h > 0 is a smoothing parameter called the bandwidth.
     */
    private double h;
    /**
     * The mean value.
     */
    private double mean;
    /**
     * The standard deviation.
     */
    private double sd;
    /**
     * The variance.
     */
    private double variance;

    /**
     * Constructor. The bandwidth of kernel will be estimated by the rule of thumb.
     * @param x the samples to estimate the density function.
     */
    public KernelDensity(double[][] data) {
        double[] x = new double[data.length];
        double[] y = new double[data.length];
        for (int i =0 ;i<data.length; i++){
            x[i]=data[i][0];
            y[i]=data[i][1];
        }
        this.x = x;
        this.y = y;
        this.mean = mean(x);
        this.variance = Math.pow(sd(x),2);
        this.sd = Math.sqrt(variance);

        Arrays.sort(x);
        Arrays.sort(y);

        int n = x.length;
        double iqr = x[n*3/4] - x[n/4];
        h = 1.06 * Math.min(sd, iqr/1.34) / Math.pow(x.length, 0.2);
        gaussian = new GaussianDistribution(0, h);
    }

    /**
     * Constructor.
     * @param x the samples to estimate the density function.
     * @param h a bandwidth parameter for smoothing.
     */
    public KernelDensity(double[][] data, double h) {
        if (h <= 0) {
            throw new IllegalArgumentException("Invalid bandwidth: " + h);
        }
        double[] x = new double[data.length];
        double[] y = new double[data.length];
        for (int i =0 ;i<data.length; i++){
            x[i]=data[i][0];
            y[i]=data[i][1];
        }
        this.x = x;
        this.y = y;
        this.h = h;
        this.mean = mean(x);
        this.variance = Math.pow(sd(x),2);
        this.sd = Math.sqrt(variance);
        gaussian = new GaussianDistribution(0, h);

        Arrays.sort(x);
        Arrays.sort(y);
    }



    //@Override
    public double p (double x , double y) {
        int start = Arrays.binarySearch(this.x, x-3*h);
        if (start < 0) {
            start = -start - 1;
        }

        int end = Arrays.binarySearch(this.x, x+3*h);
        if (end < 0) {
            end = -end - 1;
        }
        int starty = Arrays.binarySearch(this.y, y-3*h);
        if (starty < 0) {
            starty = -starty - 1;
        }

        int endy = Arrays.binarySearch(this.y, y+3*h);
        if (endy < 0) {
            endy = -endy - 1;
        }

        if ( end >this.x.length)
            end = this.x.length;
        if ( endy >this.y.length)
            endy = this.y.length;

        
        double p = 0.0;
        for (int i = start; i < end; i++) {
            for (int j = starty; j<end; j++){
                double norm =  Math.pow( this.x[i]-x ,2) + Math.pow( this.y[j]-y ,2) ;
                p += gaussian.p( norm );
            }
        }

        return p / (this.x.length *this.y.length);
    }

    
    public static double mean(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        return sum / m.length;
    }
    public static double sd(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        double mean = sum / m.length;

        double sd=0;

        for (int i = 0; i < m.length; i++) {
            sd += Math.pow(m[i]-mean ,2);
        }

        return Math.sqrt(sd);
    }
}