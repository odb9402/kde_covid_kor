package kde_covid_kor;

import kde_covid_kor.covidDB.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.JavaSparkContext;

public class CovidKDEMain{
    public static void main(String[] args) throws IOException {
        FileOutputStream output = new FileOutputStream("./kde_out.txt");
        SparkSession spark = SparkSession
            .builder()
            .appName("covid_kde")
            .config("spark.master","local")
            .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        
        
        TrajectoryData data = new TrajectoryData();
        data.loadTrajectories();
        Dataset<Trajectory> trajectory = data.getTrajectories();
        
        
        output.close();
    }
}