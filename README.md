# ScalableCloudProgramming-CoPurchaseAnalysis
Project for the University of Bologna "Scalable and Cloud Programming" course (a.y. 2024-25).

## Project execution:
### Gcloud and git repo
1) Install ```gcloud``` via terminal;
2) Authenticate with your Google credentials using ```gcloud auth login``` command;
3) Create a [gcloud project](https://cloud.google.com/sdk/gcloud/reference/projects/create) and enable the ''Cloud Dataproc API'' API;
4) Create a project bucket using the ```gcloud storage buckets create gs://[BUCKET_NAME] --location=[BUCKET_LOCATION]``` command;
5) Clone the git repository using ```git clone``` command;

### Clusters creation
6) Create the gcloud **single node** cluster using the following command:
   ```
   gcloud dataproc clusters create [CLUSTER_NAME] --region=us-central1 --single-node --master-boot-disk-size 240 --project=[PROJECT_ID]
   ```
   Replacing ```[CLUSTER_NAME]``` and ```[PROJECT_ID]``` with your cluster and project names respectively;
8) Create the gcloud **multiple nodes** cluster(s) using the following command:
   ```
   gcloud dataproc clusters create [CLUSTER_NAME] --region=us-central1 --num-workers=[WORKERS_NUM] --master-boot-disk-size 240 --worker-boot-disk-size 240 --project=[PROJECT_ID]
   ```
   Replacing ```[CLUSTER_NAME]``` and ```[PROJECT_ID]``` as above and ```[WORKERS_NUM]``` with the desired number of worker nodes;

### Job execution
8) Execute the required changes within the ```Main.scala``` file. Those changes are commented out in the code with the "_MODIFY_" keywork;
9) Update the ```scp-project_2.12-0.1.jar``` package by executing the ```package``` command using the sbt shell in the project directory:
10) Load the updated package in the project bucket (instruction 4);
11) Execute the job using the following command:
    ```
    gcloud dataproc jobs submit spark --cluster=[CLUSTER_NAME] --region=us-central1 --jar=gs://[BUCKET_NAME]/scp-project_2.12-0.1.jar
    ```

### Cluster removal
12) When a cluster is no longer useful, delete it with the following command:
    ```
    gcloud dataproc clusters delete [CLUSTER_NAME] --region us-central1 --project=[PROJECT_ID]
    ```
