package kde_covid_kor.test;

import kde_covid_kor.covidDB.*;

public class TrajectoryTest {
    public static void main(String[] args){
        System.out.println("Test");
        TrajectoryData newData = new TrajectoryData();
        newData.loadTrajectories();
        
        System.out.println("SIZE OF THE TRAJECTORIES " + newData.getTrajectories().size());

    }
}