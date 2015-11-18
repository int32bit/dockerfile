#!/bin/sh
SRC=$1
if [ -z "$SRC" ]; then
	echo "The video source is not specifed."
	exit 1
fi
if [ $1 = "-h" -o $1 = "--help" ]; then
	echo "docker run --rm -v your_input:/input -v your_output:/output -e FORMAT='frame%3d.jpg' extractkeyframes youfile.mp4"
	echo "FORMAT: output format,not required!"
	exit 0
fi
echo "Video source file is '$SRC'"
if [ ! -f "$SRC" ]; then
	echo "video source file is not exits."
	exit 1
fi
FILENAME=$(basename $SRC)
PREFIX=${FILENAME%.*}
FORMAT=${FORMAT:-"${PREFIX}_%3d"}.jpg
OUTPUT=/output
ffmpeg  -i $SRC  -q:v 2 -vf select="eq(pict_type\,PICT_TYPE_I)" -vsync 0 $OUTPUT/$FORMAT
