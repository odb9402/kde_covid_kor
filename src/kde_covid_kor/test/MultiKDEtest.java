package kde_covid_kor.test;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.stat.KernelDensity;

public class MultiKDEtest {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("JavaKernelDensityEstimationExample").setMaster("local");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        // an RDD of sample data
        JavaRDD<Double> data = jsc.parallelize(Arrays.asList(1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0, 5.0, 6.0, 7.0, 8.0, 9.0, 9.0));
        // and a standard deviation for the Gaussian kernels
        KernelDensity kd = new KernelDensity().setSample(data).setBandwidth(3.0);
        // Find density estimates for the given values
        double[] densities = kd.estimate(new double[] { -1.0, 2.0, 5.0 });
        System.out.println(Arrays.toString(densities));
        
        // $example off$
        jsc.stop();
            
    }
}