package kde_covid_kor.covidDB;


import java.io.Serializable;
import java.util.Date;

public class Trajectory implements Serializable {
    private double x;
    private double y;
    private double time;
    private boolean oversea;
    private String type;
    private int contactNum;
    private long id;
    
    public Trajectory(double x, double y, double time, String type, int contactNum,
        long id, boolean oversea){
        this.x = x;
        this.y = y;
        this.time = time/100000;
        this.type = type;
        this.contactNum = contactNum;
        this.id = id;
        this.oversea = oversea;
    }
    public double getX(){
        return this.x;
    }
    public void setX(double x){
        this.x = x;
    }
    public double getY(){
        return this.y;
    }
    public void setY(double y){
        this.y = y;
    }
    public double getTime(){
        return this.time;
    }
    public void setTime(double time){
        this.time = time;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public int getContactNum(){
        return this.contactNum;
    }
    public void setContactNum(int contactNum){
        this.contactNum = contactNum;
    }
    public long getId(){
        return this.id;
    }
    public void setId(long id){
        this.id = id;
    }
    public boolean getOversea(){
        return this.oversea;
    }
    public void setOversea(boolean oversea){
        this.oversea = oversea;
    }
}