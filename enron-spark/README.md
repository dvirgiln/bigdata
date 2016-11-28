# EXERCICE

## Locale the data and storage

Locate the Enron email data on AWS. The data is big (210 GB), so a challenge you need to solve is to work with this data without trying to download all of it to your own machine. Hints:

1. Create an EC2 instance with sufficient RAM and processing power for what you are trying to do

2. Attach an EBS volume sized 210 GB with the snapshot ID snap-d203feb5 while defining your EC2 instance.

3. Once you log in remotely to your EC2 instance (Linux command line) you can mount the EBS and you will find all the ENRON data there. You can choose the file format you find easier to work with: PST or XML.

4. It’s OK to test your code on sample data locally, but you should try to run your code on the entire dataset.

Use AWS documentation if required.

## TASKS

Use Scala, Python or Java to answer the following questions:

1. What is the average length, in words, of the emails? (Ignore attachments)

2. Which are the top 100 recipient email addresses? (An email sent to N recipients would could N times -

count “cc” as 50%)


# SOLUTION 


## SETUP LOCAL ENVIRONMENT
Steps followed. First lets try the processing on a local standalone cluster with HDFS.

1. Firstly it has been created an EC2 account with the free tier, except for the volume storage, that it requires 210GB. 

2. Download the key pair .pem file (just following the EC2 AWS instructions)

3. Connect with ssh to your EC2 instance: 

        ssh -i "david.pem" ec2-user@ec2-54-147-194-204.compute-1.amazonaws.com


4. Mount the volume to your EC2 instance:


        lsblk
        sudo mount /dev/xvdb /data


5. Now all the 250GB of enron data are accesible on /data directory.
 
6. Download a few of the zip files with the XML format. Before testing the code on EC2 it is better to perform the test on the local spark instance.


        scp -i "david.pem" ec2-user@ec2-54-147-194-204.compute-1.amazonaws.com:/data/edrm-enron-v2/edrm-enron-v2_allen-p_xml.zip ./
        scp -i "david.pem" ec2-user@ec2-54-147-194-204.compute-1.amazonaws.com:/data/edrm-enron-v2/edrm-enron-v2_arnold-j_xml.zip ./


7. Terminate the EC2 instance, so the amount to pay to Amazon is small. Now we can start writting code, and testing on our local machine.

8. Configure in local the same environment that the spark-ec2 instance would configure
	8.1. Download spark. In our case we are using 2.0.2: http://spark.apache.org/downloads.html
        8.2. Download hadoop 2.7.3: http://www-eu.apache.org/dist/hadoop/common/hadoop-2.7.3/

9. Setup Hadoop. 

	9.1. Modify your .profile or .bashrc:

        export HADOOP_HOME=/home/dave/dev/apps/hadoop-2.7.3
        export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin
        export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$HADOOP_HOME/lib/native/
        export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop

	9.2. Modify the $HADOOP_HOME/etc/hadoop/hadoop-env.sh and set the JAVA_HOME:

        export JAVA_HOME=/home/dave/dev/apps/jdk1.8.0_73


	9.3. Install a SSH Server in case you do not have one install:

        sudo apt-get install openssh-server


	9.5. Modify the $HADOOP_HOME/etc/hadoop/core-site.xml:

        <configuration>
          <property>
            <name>fs.defaultFS</name>
            <value>hdfs://localhost:9000</value>
          </property>
        </configuration>


	9.6. Modify the $HADOOP_HOME/etc/hadoop/core-site.xml:

        <configuration>
          <property>
            <name>fs.defaultFS</name>
            <value>hdfs://localhost:9000</value>
          </property>
        </configuration>

	9.7. Start the HDFS Daemon:
	
        $HADOOP_HOME/hadoop namenode -format      --DO THIS TO AVOID A CONNECTION REFUSED ERROR: http://stackoverflow.com/questions/10918269/unable-to-check-nodes-on-hadoop-connection-refused
        $HADOOP_HOME/sbin/.start-dfs.sh


10. Run the script that unzip the zip files and insert the xml files into hdfs. In the directory where you have the zip files from the edrm-enron dataset place the extractAndInsertOnHDFS.sh script:

        chmod +x extractAndInsertOnHDFS.sh
        ./extractAndInsertOnHDFS.sh


## Partial Solution 
The code included has not been tested to deploy on a Amazon EC2 instance. 

It has been included a solution for the second question of the exercice. Generate the most popular emails.

My idea initially was to take all the information that was required from the XML files. That's why I generated a script that only puts on HDFS the xml files. Then I realized that for the first question, the details of the email were not included on the XML, so I modified my script to as well attach the txt folder in HDFS.

Then my code to read the xml files and convert them to dataframes was only useful in the case of the second part of the exercice.

For the first part of the exercice I think I could implement but it will take a bit more time. For the moment the code developed, I think shows my coding skills.

## Project

The project is an SBT project. To collect the dependencies write sbt clean compile.

All the dependencies are accesible from the ../Project/Dependencies.scala.


### Get Most popular emails
The main class to take a look is SparkEnronService

1. It has been used databricks.spark-xml library to convert the XML files into dataframes. 
2. Then it has been filtered by type of message.
3. Has been converted the dataframe into a more readable/manageable structure like Email case class. Here it is interesting the parsing of the email String into a List of EmailRecipient.
4. Then the dataframe has been cached.
5. Then it has been converted to RDD and an interesting function RDD.aggregate has been used.
6. Take a look how has been done the join of the workers results and how has been done the operation to add to the map the values.
7. Some complex pattern matching can be seen in the code.
8. Additionally has been used reading from a Config file.

My initial implementation of the conversion to dataframe was trying to use the wholeFileText funcitionality of the RDD. I realized that the XML files are so heavy, and we do not want to collect the data, as it is being done by this wholeFileText. But I have left the code I wrote into the class DeprecatedXMLProcessing.

In this class you can see some usage of Scala XML functionality to parse XML documents.

### Get Average Emails Size
My first idea is using spark. With this unstructured text data, the only option using spark is to use the function wholeTextFiles.  

But this is not a good solution as I explained before. The wholeTextFiles returns a Map with the fileName and the content of the file as a String. We can not collect all the files content (210GB)

I imagine some kind of conversion needs to be done before by Hadoop to convert this files to another format.

I think some treatment should be done to the txt files when they are uploaded to hdfs. Options:

1. HiVE: hive works with structured data. To load the data it should have a schema. In this case the txt file has no schema.
2. PIG:  as i have read it is possible to create a custom conversor in pig and convert the txt files and load them on pig.
        
        https://shrikantbang.wordpress.com/2013/11/02/apache-pig-custom-load-function-2/

Steps to follow:

1. Load the data in PIG using a custom conversor.
2. Convert the data from pig to hive.
3. Load the hive data to a Spark Dataframe. Once it is loaded all the queries can be done with no effort.


## Execution

To check that the functionality is working it is required:

1. HDFS started and containing data. You need to ensure it was executed the extractAndInsertOnHDFS.sh.
2. The values of the application.conf core-site.xml and hdfs-site.xml are correct. By default if you follow the instructions above, everything should work.
3. You can execute the TestApp object that is inside of the SparkEnronService.scala.

Results expected:


        (EmailRecipient(Ina Rangel,None),49500)
        (EmailRecipient(Arnold  John <John.Arnold@ENRON.com>,None),28750)
        (EmailRecipient(pallen70@hotmail.com,None),27400)
        (EmailRecipient(John J Lavorato,None),25550)
        (EmailRecipient(slafontaine@globalp.com @ ENRON,None),21300)
        (EmailRecipient(stagecoachmama@hotmail.com,None),20800)
        (EmailRecipient(Mike Maggi,None),20600)
        (EmailRecipient(Margaret Allen,None),19400)
        (EmailRecipient(Jennifer Fraser,None),15450)
        (EmailRecipient(Mike Grigsby,None),14500)
        (EmailRecipient(Andy Zipper,None),14400)
        (EmailRecipient(Allen  Phillip K. <Phillip.K.Allen@ENRON.com>,None),14400)
        (EmailRecipient(Keith Holst,None),14000)
        (EmailRecipient(pallen@enron.com,None),13900)
        (EmailRecipient(John Arnold,None),13000)
        (EmailRecipient(Matthew Arnold,None),12600)
        (EmailRecipient(Jennifer White <jenwhite7@zdnetonebox.com>,None),11800)
        (EmailRecipient(Jennifer Burns,None),10900)
        (EmailRecipient(Brian Hoskins,None),10600)
        (EmailRecipient(Jennifer Medcalf,None),10400)


# TO BE DONE

1. First part of the exercice to calculate the average of the messages.
2. Deploy on a EC2 instance. Instructions: https://spark.apache.org/docs/1.6.2/ec2-scripts.html
3. Include profiles (like in maven) to avoid embed the environment properties to be included directly on the configuration files.
4. Include the sbt assembly to create a fat jar.

