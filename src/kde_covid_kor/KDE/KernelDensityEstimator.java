package kde_covid_kor.KDE;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.*;
import org.apache.spark.sql.SparkSession;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import org.apache.spark.api.java.JavaRDD;

public class KernelDensityEstimator {
    private double[][] table;
    private double[] weight;
    private double[][] densities;
    private KernelDensity kd;

        
    public KernelDensityEstimator(){
    
    }

    public void fit(JavaSparkContext sc, double[][] data, int gridNum){
        kd = new KernelDensity(sc,data);

        Logger.getLogger("org.apache").setLevel(Level.WARN);
        densities = new double[gridNum][gridNum];

        int len_ = data.length-1;
        double[] tmpData0=new double[data.length];
        double[] tmpData1=new double[data.length];
        for (int i=0;i<data.length;i++){
            tmpData0[i] = data[i][0];
            tmpData1[i] = data[i][1];
        }
        
        Arrays.sort(tmpData0);
        Arrays.sort(tmpData1);
                
        double xmin = tmpData0[0];
        double xmax = tmpData0[len_];

        double ymin = tmpData1[0];
        double ymax = tmpData1[len_];

        double xscale = xmax - xmin;
        double yscale = ymax - ymin;

        


        
        for(int i=100;i>0;i--){
            double y = -(double)i*yscale/100. +ymax;
            for(int j=0;j<100;j++){
                double x = (double)j*xscale/100. +xmin;
                System.out.print( Math.round( kd.p(x,y)*100000*100000 )/100000.+"\t");
                
                //System.out.print(Math.round(x*100)/100.0+" "+Math.round(y*100)/100.0+"      ");
            }
            System.out.println("///////");
        }
        
        



        
        sc.close();
    }

    public double likelihood(double[][] data){
        double likelihood = -1.0;

        return likelihood;
    }

    public void writeDensities(){

    }
}