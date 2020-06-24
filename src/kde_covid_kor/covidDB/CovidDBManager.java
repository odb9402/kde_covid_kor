package kde_covid_kor.covidDB;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class CovidDBManager{
    /*
    MySQL database on the Docker container : covid-sql
    */
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://172.17.0.5/covid_kor";
    
    private final String USER_NAME = "ro_covid";
    private final String PASSWORD = "PNUdmb0227";
    
    private Connection conn = null;
    private Statement state = null;
    private ResultSet rs = null;
    
    public CovidDBManager(){ 
        try{
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            System.out.println("[MySQL Connection success]\n");
            this.state = conn.createStatement();
        }
        catch(ClassNotFoundException e){
            System.out.println("Driver loading failure");
        }
        catch(SQLException e){
            System.out.println("Error: " + e);
        } 
    }
    
    public void closeConnection(){
        try{
            state.close(); 
            conn.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("[MySQL Connection is closed]\n");
    }
    
    public List<Trajectory> selectPatientTrajectory(){
        List<Trajectory> trajectories = new ArrayList<Trajectory>();
        Mercator projecter = new EllipticalMercator();

        // get values using SQL qurey
        final String query = "SELECT info.patient_id, info.disease, route.date, "
            + "info.contact_number, route.type, route.latitude, route.longitude, "
            + "info.infection_case "
            + "FROM patientInfo as info "
            + "JOIN patientRoute as route "
            + "ON (info.patient_id = route.patient_id AND "
            + "info.infection_case IS NOT NULL)";
        try{
            rs = state.executeQuery(query);
                    
            double x, y;
            boolean oversea;

            while(rs.next()){
                x = projecter.xAxisProjection(rs.getDouble(6) + rs.getDouble(7));
                y = projecter.yAxisProjection(rs.getDouble(6));

                if(rs.getString(8).equals("overseas inflow")){
                    oversea = true;
                }
                else if(rs.getString(8).equals("contact with patient")){
                    oversea = false;
                }
                else{
                    continue;
                }
                trajectories.add(new Trajectory(x, y, rs.getDate(3).getTime()
                    , rs.getString(5), rs.getInt(4), rs.getLong(1), oversea));
                //System.out.println("Date:" + rs.getDate(3).getTime());
                }
        }
        catch(SQLException e){
            System.out.println("SQL Error : " + e);
        } finally {
            try{
                if(rs != null && !rs.isClosed()){
                    rs.close();
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        return trajectories;
    }
}