package kde_covid_kor.KDE;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
    private double[][] pvalues;
    private KernelDensity kd;
    private int gridNum;

    private double xmin;
    private double xmax;

    private double ymin;
    private double ymax;

    private double yscale;
    private double xscale;

    private double globalMeanDensity;
    private double globalSumDensity;
    private final double threshold = 0.05;

    public KernelDensityEstimator() {

    }

    public void fit(JavaSparkContext sc, double[][] data, int gridNum) {
        kd = new KernelDensity(sc, data, true, true, "const");
        // kd = new KernelDensity(sc,data,true,"silver");
        this.gridNum = gridNum;
        Logger.getLogger("org.apache").setLevel(Level.WARN);
        densities = new double[gridNum][gridNum];
        int len_ = data.length - 1;
        double[] tmpData0 = new double[data.length];
        double[] tmpData1 = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            tmpData0[i] = data[i][0];
            tmpData1[i] = data[i][1];
        }

        Arrays.sort(tmpData0);
        Arrays.sort(tmpData1);

        xmin = tmpData0[0];
        xmax = tmpData0[len_];

        ymin = tmpData1[0];
        ymax = tmpData1[len_];

        xscale = xmax - xmin;
        yscale = ymax - ymin;
        FileOutputStream fw;
        try {
            fw = new FileOutputStream("fitInfo.txt");
            String str = "xmin,"+xmin+"\n"+"xmax,"+xmax+"\n"+"ymin,"+ymin+"\n"+"ymax,"+ymax+"\n"+"xscale,"+xscale+"\n"+"yscale,"+yscale;
            fw.write(str.getBytes());
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //sc.close();
    }

    public double averageDensity(double[][] data){
        double averageDensity = 0.0;

        for(int i=0;i<data.length;i++)
            averageDensity = Math.round( kd.p( data[i][0] , data[i][1]  )*100000*100000 )/100000. ;
        return averageDensity/(double)data.length;
    }

    public void findHotspot(){
        double globalLambda = globalMeanDensity;
        ExponentialDistribution globalExp = new ExponentialDistribution(globalLambda);
        pvalues = new double[gridNum][gridNum];
        
        for(int i=0; i<gridNum; i++){
            for(int j=0; j<gridNum; j++){
                double meanA = 0.0;
                double meanB = 0.0;
                
                //A filter : 30 x 30 average
                int count = 0;
                int x_start = Math.max(0, i-30);
                int y_start = Math.max(0, j-30);
                int x_end = Math.min(gridNum-1, i+30);
                int y_end = Math.min(gridNum-1, j+30);
                for(int x=x_start; x<x_end; x++){
                    for(int y=y_start; y<y_end; y++){
                        meanA += densities[x][y];
                        count++;
                    }
                }
                meanA = meanA/(double)count;
                
                //B filter : 60 x 60 average
                count = 0;
                x_start = Math.max(0, i-60);
                y_start = Math.max(0, j-60);
                x_end = Math.min(gridNum-1, i+60);
                y_end = Math.min(gridNum-1, j+60);
                for(int x=x_start; x<x_end; x++){
                    for(int y=y_start; y<y_end; y++){
                        meanB += densities[x][y];
                        count++;
                    }
                }
                meanB = meanB/(double)count;
                ExponentialDistribution expA = new ExponentialDistribution(meanA);
                ExponentialDistribution expB = new ExponentialDistribution(meanB);
                double p_g = 1 - globalExp.cumulativeProbability(densities[i][j]);
                double p_a = 1 - expA.cumulativeProbability(densities[i][j]);
                double p_b = 1 - expB.cumulativeProbability(densities[i][j]);

                double p_hotspot = Math.min(p_g, Math.min(p_a, p_b));
                pvalues[i][j] = p_hotspot;
            }
        }
    }

    public void getDensities(String fileName){
        try{
            FileOutputStream fw = new FileOutputStream(fileName);
            FileOutputStream fw2 = new FileOutputStream("xydensity.txt"); 
            for(int i=gridNum-1; i>=0; i--){
                double y = -(double)i*yscale/(double)gridNum + ymax;
                for(int j=0; j<gridNum; j++){
                    double x = (double)j*xscale/(double)gridNum +xmin;
                    densities[i][j] = kd.p(x,y)*100000*100000;
                    globalSumDensity += densities[i][j];
                    String str = Math.round( densities[i][j]*100000*100000 )/100000.+"\t";
                    String str2 = x + "," + y + "," + densities[i][j] + "\n";
                    fw.write(str.getBytes());
                    fw2.write(str2.getBytes());
                }
                fw.write("\n".getBytes());
                System.out.println(gridNum-i);
            }
            globalMeanDensity = globalSumDensity / (gridNum*gridNum);
            fw.close();
            fw2.close();
        }
        catch(IOException e){
            System.exit(1);
        }
    }

    public void writePoints(String fileName, double[][] points, boolean grid) throws IOException {
        try{
            FileOutputStream pointsOutput = new FileOutputStream(fileName);
            for(int i = 0; i<points.length; i++){
                double x;   
                double y;   
                if(grid){
                    x = (points[i][0] - xmin)/xscale * 300;
                    y = (points[i][1] - ymin)/yscale * 300;
                }
                else{
                    x = points[i][0];
                    y = points[i][1];
                }
                String str = x + "," + y + "," + points[i][2];
                pointsOutput.write(str.getBytes());
                pointsOutput.write("\n".getBytes()); 
            }
            pointsOutput.close();
        }
        catch(IOException e){
            System.exit(1);
        }
    }
    public void writePvalues(String fileName){
        try{
            FileOutputStream fw = new FileOutputStream(fileName);
            
            for(int i=gridNum-1; i>=0; i--){
                for(int j=0; j<gridNum-1; j++){
                    String str = pvalues[i][j] + ",";
                    fw.write(str.getBytes());
                }
                String str = pvalues[i][gridNum-1]+"";
                fw.write(str.getBytes());
                fw.write("\n".getBytes());
            }
            fw.close();
        }
        catch(IOException e){
            System.exit(1);
        }
    }
    public double getGlobalSumDensity(){
        return this.globalSumDensity;
    }
}