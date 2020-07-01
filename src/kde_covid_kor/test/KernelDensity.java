package kde_covid_kor.test;

import java.util.Arrays;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.Point;
import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.*;
import org.apache.spark.sql.SparkSession;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;


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
    //private GaussianDistribution gaussian;
    /*
     * h > 0 is a smoothing parameter called the bandwidth.
     */
    private double h;
    /**
     * The mean value.
     */
    private double weight_sum;
     

    private double mean;
    /**
     * The standard deviation.
     */

    private double sdy;
    private double variancey;

    private double sd;
    /**
     * The variance.
     */
    private double variance;

    private double[][] data;
    /**
     * Constructor. The bandwidth of kernel will be estimated by the rule of thumb.
     * @param x the samples to estimate the density function.
     */
    private JavaRDD<double[]> myRdd;
    
    public KernelDensity(JavaSparkContext sc,double[][] data ) {
        
        
        double[][] data_ = new double[data.length][3];
        double[] x = new double[data.length];
        double[] y = new double[data.length];
        for (int i =0 ;i<data.length; i++){
            x[i]=data[i][0];
            y[i]=data[i][1];
            data_[i][0] = data[i][0];
            data_[i][1] = data[i][1];
            data_[i][2] = 1;
        }
        this.x = x;
        this.y = y;
        this.data = data_;
        this.mean = mean(x);
        this.weight_sum = data.length;
        this.variance = Math.pow(sd(x),2);
        this.sd = Math.sqrt(variance);
        this.variancey = Math.pow(sd(y),2);
        this.sdy = Math.sqrt(variancey);
        

        Arrays.sort(x);
        Arrays.sort(y);

        int n = x.length;
        double iqr = 1./2. * (x[n*3/4] - x[n/4]  +  y[n*3/4] - y[n/4]);
        h = 1.06 * Math.min( Math.sqrt(sdy*sd), iqr/1.34) / Math.pow(x.length, 0.2);
        
        //gaussian = new GaussianDistribution(0, h);
        myRdd = sc.parallelize(Arrays.asList(this.data));
        
    }

    public KernelDensity(JavaSparkContext sc,double[][] data ,double[]  weight_ ) {          // weight overloading
        
        
        double weightSum = 0;
        double[] x = new double[data.length];
        double[] y = new double[data.length];
        
        double[][] data_ = new double[data.length][3];

        for (int i =0 ;i<data.length; i++){
            x[i]=data[i][0];
            y[i]=data[i][1];
            data_[i][0] = data[i][0];
            data_[i][1] = data[i][1];
            data_[i][2] = weight_[i];
            weightSum += weight_[i];
        }
        this.x = x;
        this.y = y;

        this.weight_sum = weightSum;

        this.data = data_;
        this.mean = mean(x);
        this.variance = Math.pow(sd(x),2);
        this.sd = Math.sqrt(variance);
        this.variancey = Math.pow(sd(y),2);
        this.sdy = Math.sqrt(variancey);
        

        Arrays.sort(x);
        Arrays.sort(y);

        int n = x.length;
        double iqr = 1./2. * (x[n*3/4] - x[n/4]  +  y[n*3/4] - y[n/4]);
        h = 1.06 * Math.min( Math.sqrt(sdy*sd), iqr/1.34) / Math.pow(x.length, 0.2);
        //h = 1.06 *  Math.sqrt(sdy*sd) / Math.pow(x.length, 0.2);
        
        
        myRdd = sc.parallelize(Arrays.asList(this.data));
        
    }

    /**
     * Constructor.
     * @param x the samples to estimate the density function.
     * @param h a bandwidth parameter for smoothing.
     */
    public KernelDensity(JavaSparkContext sc, double[][] data, double h) {
        if (h <= 0) {
            throw new IllegalArgumentException("Invalid bandwidth: " + h);
        }
        double[][] data_ = new double[data.length][3];
        double[] x = new double[data.length];
        double[] y = new double[data.length];
        for (int i =0 ;i<data.length; i++){
            x[i]=data[i][0];
            y[i]=data[i][1];
            data_[i][0] = data[i][0];
            data_[i][1] = data[i][1];
            data_[i][2] = 1;
        }
        this.x = x;
        this.y = y;
        this.h = h;
        this.weight_sum = data.length;
        this.data = data_;
        this.mean = mean(x);    // mean of vector
        this.variance = Math.pow(sd(x),2);
        this.sd = Math.sqrt(variance);
       
        myRdd = sc.parallelize(Arrays.asList(this.data));
        Arrays.sort(x);
        Arrays.sort(y);
    }

    public KernelDensity(JavaSparkContext sc, double[][] data, double h , double[] weight_) {  // weight overloading
        if (h <= 0) {
            throw new IllegalArgumentException("Invalid bandwidth: " + h);
        }
        
        double weightSum = 0;
        double[][] data_ = new double[data.length][3];
        double[] x = new double[data.length];
        double[] y = new double[data.length];
        for (int i =0 ;i<data.length; i++){
            x[i]=data[i][0];
            y[i]=data[i][1];
            data_[i][0] = data[i][0];
            data_[i][1] = data[i][1];
            data_[i][2] = weight_[i];
            weightSum += weight_[i];
        }
        this.x = x;
        this.y = y;
        this.h = h;
        this.weight_sum = weightSum;
        this.data = data_;
        this.mean = mean(x);    // mean of vector
        this.variance = Math.pow(sd(x),2);
        this.sd = Math.sqrt(variance);
       // gaussian = new GaussianDistribution(0, h);
        myRdd = sc.parallelize(Arrays.asList(this.data));
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

  /*  
        for (int i = 0; i < 100; i++) {
           
            //double gausDist =  gaussian.p( this.x[i]-x ) * gaussian.p( this.y[j]-y ) ;
            p +=  Math.exp(-1/(2*h*h)*( Math.pow( this.x[i] - x ,2) + Math.pow( this.y[i]-y ,2) ) ) / (2*Math.PI * h*h );
            
        }
        
*/
        double h1 = this.h;
        double weightSum = this.weight_sum;

        p = myRdd.map(myarray->          
            1000000000*(myarray[2]/weightSum)* Math.exp(-1/(2*h1*h1)*( Math.pow( myarray[0] - x ,2) + Math.pow( myarray[1]-y ,2) ) ) / (2*Math.PI * h1*h1 )   // weight
        ).reduce((a,b)-> a+b);
        
        

        return p / (this.x.length );
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