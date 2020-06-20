package kde_covid_kor.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionTest{
    public static void main(String[] args){
        Connection conn = null;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://172.17.0.5/covid_kor";
            
            conn = DriverManager.getConnection(url, "ro_covid","PNUdmb0227");
            System.out.println("Connection success");
        }
        catch(ClassNotFoundException e){
            System.out.println("Driver loading failure");
        }
        catch(SQLException e){
            System.out.println("Error: " + e);
        }
        finally{
            try{
                if(conn != null && !conn.isClosed()){
                    conn.close();
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }

        }
    }
}