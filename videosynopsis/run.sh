#!/bin/bash
jar=$(ls `pwd`/lib/*.jar)
export CLASSPATH=`echo $jar | tr ' ' ':'`:$CLASSPATH
if [ -z "$(ls /input)" ]
then
	echo "The input directory is empty, no any video file is found!"
	exit 0
fi
javac EntryPoint.java && java EntryPoint
ffmpeg -y -f image2  -i /output/%d.jpg -vcodec libx264 -crf 25  /output/out.avi
rm -rf /output/*.jpg
