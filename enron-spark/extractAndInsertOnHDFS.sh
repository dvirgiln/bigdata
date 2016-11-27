#!/bin/bash
FILES=./*_xml.zip
mkdir -p ./temp_dir/text
echo "Extracting XML files"
for f in $FILES
do
  # Unzipping the content of the zip file, just taking in account the xml of the first level.
  unzip $f '*.xml' -d './temp_dir'
  unzip $f 'text_*/*' -d './temp_dir/text'
done
echo "XML files extracted successfully"
hdfs dfs -mkdir -p /data/enron/text
echo "Inserting in HDFS"
XML_FILES=./temp_dir/*.xml
for f in $XML_FILES
do
 echo "Inserting $f in hdfs /data/enron"
 hdfs dfs -put $f /data/enron
done

TXT_FILES=./temp_dir/text/*
for f in $TXT_FILES
do
 echo "Inserting $f in hdfs /data/enron/text"
 hdfs dfs -put $f /data/enron/text
done



echo "Inserted the XML successfully in HDFS"

echo "Removing temporal directory"
rm -R ./temp_dir
echo "Script Successfully executed."
