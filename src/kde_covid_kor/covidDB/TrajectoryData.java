package kde_covid_kor.covidDB;

import java.util.ArrayList;

public class TrajectoryData{
    private ArrayList<Trajectory> trajectories;
    
    public TrajectoryData(){
        this.trajectories = new ArrayList<Trajectory>();
    }

    public void loadTrajectories(){
        CovidDBManager covdb = new CovidDBManager();
        this.trajectories = covdb.selectPatientTrajectory();
    }

    public ArrayList<Trajectory> getTrajectories(){
        return this.trajectories;
    }
}