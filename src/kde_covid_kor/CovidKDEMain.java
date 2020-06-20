package kde_covid_kor;

import kde_covid_kor.covidDB.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class CovidKDEMain{
    public static void main(String[] args) throws IOException {
        FileOutputStream output = new FileOutputStream("./kde_out.txt");
         
        output.close();
    }
}