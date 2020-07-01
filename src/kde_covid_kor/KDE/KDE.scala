package kde_covid_kor.KDE

import kde_covid_kor.covidDB._

import com.github.fommil.netlib.BLAS.{getInstance => blas}
import org.apache.spark.annotation.Since
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

final class KDE(implicit sparkSession: SparkSession, 
      encoder: Encoder[Row]) {
  /**
    * Applied the trained KDE to a set of validation data
    * @param trainingDS  Training data sets
    * @param validationRdd Validation data sets
    * @return Datasets of probability densities
    */
  def estimate(
    trainingDS: Dataset[Obs], 
    validationDS: Dataset[Obs], 
    bandwidth: Double = 1.0): Dataset[Double] = {
    import math._, sparkSession.implicits._
    val validation_brdcast = sparkSession.sparkContext
            .broadcast[Array[Obs]](validationDS.collect)

    trainingDS.mapPartitions((iter: Iterator[Obs]) => {
      val seqObs = iter.toArray
      val scale = 0.5 * seqObs.size* log(2 * Pi)
      val validation = validation_brdcast.value

      val (densities, count) = seqObs.aggregate(
        (new Array[Double](validation.length), 0L) ) (
        {        // seqOp (U, T) => U
          case ((x, z), y) => {
            var i = 0
            while (i < validation.length) {   
               // Call the pdf function for the normal distribution
              x(i) += multiNorm(y, bandwidth, scale, validation(i))
              i += 1
            }
            (x, z + 1)// Update  count & validation values
          }
        },
        {         // combOp: (U, U) => U
          case ((u, z), (v, t)) => { 
                // Combiner calls vectorization z <- a.x + y
            blas.daxpy(validation.length, 1.0, v, 1, u, 1)
            (u, z + t)
          }
        }
      )

      val invCount: Double = 1.0 / count
      blas.dscal(validation.length, invCount, densities, 1)  
          // Rescale the density using LINPACK z <- a.x
      densities.iterator
    })
  }
}

final object KDE {
  import math._
  type Obs = Array[Double]

  @throws(classOf[IllegalArgumentException])
  def multiNorm(
      means: Obs, 
      bandWidth: Double, 
      scale: Double, 
      x: Obs): Double = {
    require(x.length == means.length, 
        "Dimension of means and observations differs")

    exp(
      -scale - (0 until means.length).map(n => {
        val sx = (means(n) - x(n)) / bandWidth
        -0.5 * sx * sx
      }).sum
    )
  }
}