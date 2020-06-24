package kde_covid_kor.covidDB;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

public class TrajectoryData{
    private SparkSession spark;
    private List<Trajectory> trajectories;
    private Encoder<Trajectory> trajectoryEncoder;
    private Dataset<Trajectory> trajectoryBeanDS;

    public TrajectoryData(){
        this.trajectories = new ArrayList<Trajectory>();
        loadTrajectories();
        spark = SparkSession
            .builder()
            .appName("covid_kde")
            .config("spark.master","local")
            .getOrCreate();
        trajectoryEncoder = Encoders.bean(Trajectory.class); 
        trajectoryBeanDS = spark.createDataset(trajectories, trajectoryEncoder);
        trajectoryBeanDS.show();
        System.out.println(trajectoryBeanDS.dtypes());
    }

    public void loadTrajectories(){
        CovidDBManager covdb = new CovidDBManager();
        this.trajectories = covdb.selectPatientTrajectory();
    }

    public Dataset<Trajectory> getTrajectories(){
        return this.trajectoryBeanDS;
    }

    public List<Trajectory> getTrajectoriesList(){
        return this.trajectories;
    }

    public Encoder<Trajectory> getTrajectoryEncoder(){
        return this.trajectoryEncoder;
    }
}