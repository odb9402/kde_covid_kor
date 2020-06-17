package covidDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

private class CovidDBManager{
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://172.17.0.5/covid_kor";
    
    private final String USER_NAME = "ro_covid";
    private final String PASSWORD = "PNUdmb0227";
    
    private Connection conn = null;
    private Statement state = null;
    
    public CovidDBManager(){ 
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            System.out.println("[MySQL Connection success]\n");
            state = conn.createStatement();
        }
        catch(ClassNotFoundException e){
            System.out.println("Driver loading failure");
        }
        catch(SQLException e){
            System.out.println("Error: " + e);
        } 
    }
    
    public void closeConnection(){
        state.close(); 
        conn.close();
        System.out.println("[MySQL Connection is closed]\n");
    }
    
    public PatientTrajectory selectPatientTrajectory(){
        PatientTrajectory trajectory = new PatientTrajectory();
        // get values using SQL qurey
        
        return trajectory
    }
}