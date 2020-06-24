package kde_covid_kor.test;

import kde_covid_kor.covidDB.*;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.examples.mllib.KernelDensityEstimationExample;
import org.apache.spark.graphx.*;
import org.apache.spark.mllib.stat.KernelDensity;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import org.apache.spark.api.java.JavaRDD;

public class MultiKDEtest {
    public static void main(String[] args){
        SparkSession spark = SparkSession
            .builder()
            .appName("covid_kde")
            .config("spark.master","local")
            .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        TrajectoryData data = new TrajectoryData();
        data.loadTrajectories();
        Dataset<Trajectory> trajectory = data.getTrajectories();


        trajectory.show();

        System.out.print("hhhh");

        trajectory.printSchema();

        spark.close();

    }

    public void KDE(){

    }

    public void estimate(){
        

    }
   
}