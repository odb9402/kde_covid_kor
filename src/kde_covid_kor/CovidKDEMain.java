package kde_covid_kor;

import kde_covid_kor.KDE.KernelDensityEstimator;
import kde_covid_kor.covidDB.Trajectory;
import kde_covid_kor.covidDB.TrajectoryData;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.sparkproject.dmg.pmml.support_vector_machine.Kernel;
import org.apache.spark.api.java.JavaSparkContext;

public class CovidKDEMain{
    public static void main(String[] args) throws IOException {
        SparkSession spark = SparkSession
            .builder()
            .appName("covid_kde")
            .config("spark.master","local")
            .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        
        TrajectoryData data = new TrajectoryData();
        data.loadTrajectories();
        Dataset<Trajectory> trajectory = data.getTrajectories().sort("time");
        int idx = (int) (trajectory.count()*0.8);
        final double splitTime = trajectory.takeAsList(idx).get(idx-1).getTime();
        
        Dataset<Trajectory> testTrajectory = trajectory.where("time > "+splitTime);
        Dataset<Trajectory> trainTrajectory = trajectory.where("time <= " +splitTime);
        trainTrajectory.show();
        testTrajectory.show();
        
        List<Trajectory> trainData = trainTrajectory.collectAsList();
        List<Trajectory> testData = testTrajectory.collectAsList();
        
        double[][] trainPoint = new double[(int) trainTrajectory.count()][3];
        double[][] testPoint = new double[(int) testTrajectory.count()][3];
        double overseaWeight = 3.0;
        for(int i = 0; i < trainPoint.length; i++){
            trainPoint[i][0] = trainData.get(i).getX();
            trainPoint[i][1] = trainData.get(i).getY();
            if(trainData.get(i).getOversea())
                trainPoint[i][2] = overseaWeight;
            else
                trainPoint[i][2] = 1.0;
        }
        for(int i = 0; i < testPoint.length; i++){
            testPoint[i][0] = testData.get(i).getX();
            testPoint[i][1] = testData.get(i).getY();
            if(testData.get(i).getOversea())
                testPoint[i][2] = overseaWeight;
            else
                testPoint[i][2] = 1.0;
        }
        KernelDensityEstimator estimator = new KernelDensityEstimator();
        estimator.fit(sc, trainPoint, 300);
        estimator.getDensities("/home/dmb/dongpinoh/COVID_KDE/src/density.txt");
        estimator.findHotspot();
        estimator.writePvalues("/home/dmb/dongpinoh/COVID_KDE/src/pvalues.txt");
        estimator.writePoints("/home/dmb/dongpinoh/COVID_KDE/src/trainPoints.txt", trainPoint, true); 
        estimator.writePoints("/home/dmb/dongpinoh/COVID_KDE/src/testPoints.txt", testPoint, true); 
        System.out.println("DENSITY:::" + estimator.averageDensity(testPoint));
        System.out.println("SUM DENSITY:::" + estimator.getGlobalSumDensity());
    }

}
