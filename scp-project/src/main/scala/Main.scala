import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.HashPartitioner

object Main extends App{
   val spark = SparkSession.builder
      .appName("scp-project")
      .config("spark.executor.memory", "5g")    // each node has access to 5GB of memory (Amount of memory to use per executor process)
      .config("spark.executor.cores", "4")      // each node has access to 4 CPU cores
      .config("spark.driver.memory", "4g")      // assigns 4GB of RAM to the driver (Amount of memory to use for the driver process)
      .getOrCreate()

   val sc = spark.sparkContext
   val bucketName = "scp-bucket"       // MODIFY: enter your bucket name

   val startSystemTime = System.nanoTime()      // used to obtain the execution time
   val inputPath = "gs://" + bucketName + "/order_products.csv"
   val outputPath = "gs://" + bucketName + "/output"

   // Dataset loading
   val data: RDD[(Int, Int)] = sc.textFile(inputPath)
      .map(line => {
         val row = line.split(",")
         (row(0).toInt, row(1).toInt)
      }
   )

   // The number of partitions is dynamically determined based on the number of cluster and cores
   val nCores = spark.conf.get("spark.executor.cores", "4").toInt    // cores per node
   val nNodes = 4       // MODIFY: enter the executing cluster's number of nodes
   val nPartitions = math.max(nCores * nNodes * 2, sc.defaultParallelism * 2)

   val partData: RDD[(Int, Int)] = data.partitionBy(new HashPartitioner(nPartitions))
   val groupedByOrder: RDD[(Int, Iterable[Int])] = partData.groupByKey()

   // Creating product pairs within the same order and assigning a counter of 1 to each pair
   val productPairs: RDD[((Int, Int), Int)] = groupedByOrder
      .flatMap { case (_, products) =>
         val productList = products.toSeq
         for {
            i <- productList
            j <- productList if i < j
         } yield ((i, j), 1)
      }

   // Counting pairs
   val countProductPairs: RDD[((Int, Int), Int)] = productPairs
      .reduceByKey(_ + _)

   // Printing the results in the right format: (product1, product2, count)
   val result: RDD[String] = countProductPairs
      .map { case ((product1, product2), count) =>
         s"$product1,$product2,$count"
      }

   // Saving the result as a single document
   val finalResult = result.repartition(1)
   finalResult.saveAsTextFile(outputPath)

   val endSystemTime = System.nanoTime()
   val totalTime = (endSystemTime - startSystemTime) / 1e9     // execution time in seconds

   val T1 = 702.81      // single node time

   println(f"Executors number: $nNodes")
   println(f"Cores number per node: $nCores")
   println(f"Partitions: $nPartitions")
   println(f"Required time: $totalTime%.2f seconds")

   spark.stop()
}