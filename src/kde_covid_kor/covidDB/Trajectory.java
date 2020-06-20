package kde_covid_kor.covidDB;

import java.util.Date;

public class Trajectory{
    private double x;
    private double y;
    private Date date;
    private boolean oversea;
    private String type;
    private int contactNum;
    private long id;
    
    public Trajectory(){
        
    }
    public Trajectory(double x, double y, Date date, String type, int contactNum,
        long id, boolean oversea){
        this.x = x;
        this.y = y;
        this.date = date;
        this.type = type;
        this.contactNum = contactNum;
        this.id = id;
        this.oversea = oversea;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public Date getDate(){
        return this.date;
    }
    public String getType(){
        return this.type;
    }
    public int getContactNum(){
        return this.contactNum;
    }
    public long getId(){
        return this.id;
    }
    public boolean getOversea(){
        return this.oversea;
    }
}