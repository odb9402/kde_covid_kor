package kde_covid_kor.KDE;

import java.lang.Math;

public class GaussianDistribution {
    

    private static final double LOG2PIE_2 = Math.log(2 * Math.PI * Math.E) / 2;
    private static final double LOG2PI_2 = Math.log(2 * Math.PI) / 2;
    private static final GaussianDistribution singleton = new GaussianDistribution(0.0, 1.0);

    /** The mean. */
    public final double mu;
    /** The standard deviation. */
    public final double sigma;
    private double variance;

    private double entropy;
    private double pdfConstant;

    /**
     * Constructor
     * @param mu mean.
     * @param sigma standard deviation.
     */
    public GaussianDistribution(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
        variance = sigma * sigma;

        entropy = Math.log(sigma) + LOG2PIE_2;
        pdfConstant = Math.log(sigma) + LOG2PI_2;
    }

    /**
     * Estimates the distribution parameters by MLE.
     */
    public static GaussianDistribution fit(double[] data) {
        double mu = mean(data);
        double sigma = sd(data);
        return new GaussianDistribution(mu, sigma);
    }

    public static GaussianDistribution getInstance() {
        return singleton;
    }

    public int length() {
        return 2;
    }

    public double mean() {
        return mu;
    }

    public double variance() {
        return variance;
    }

    public double sd() {
        return sigma;
    }

    public double entropy() {
        return entropy;
    }

    public String toString() {
        return String.format("Gaussian Distribution(%.4f, %.4f)", mu, sigma);
    }


    public double p(double x) {
        if (sigma == 0) {
            if (x == mu) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

        return Math.exp(logp(x));
    }

    public double logp(double x) {
        if (sigma == 0) {
            if (x == mu) {
                return 0.0;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }

        double d = x - mu;
        return -0.5 * d * d / variance - pdfConstant;
    }

    
    public static double mean(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        return sum / m.length;
    }

    
    public static double sd(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        double mean = sum / m.length;

        double sd=0;

        for (int i = 0; i < m.length; i++) {
            sd += Math.pow(m[i]-mean ,2);
        }

        return Math.sqrt(sd);
    }
}