#!/bin/sh
show_help()
{
	echo "Environment Variables:"
	echo "    SRC: The video source file."
	echo "    FORMAT: The target format."
}
process_file()
{
	TARGET=$1
	TARGET_DIR=$(dirname $OUTPUT/$TARGET)
	if [ ! -d "$TARGET_DIR" ]; then
		mkdir -p $TARGET_DIR
	fi
	ffmpeg -y -i $TARGET $OUTPUT/${TARGET%.*}.$FORMAT
	return 0
}

process_dir()
{
	TARGET=$1
	for f in $TARGET/*
	do
		echo "process $f ..."
		if [ -d $f ];
		then
			process_dir $f
		else
			process_file $f
		fi
	done
}

if [ "$1" = "-h" -o "$1" = "--help" ]; then
	show_help
	exit 0
fi
SRC=${SRC:-$@}
if [ -z "$SRC" ]; then
	echo "No video source file is specified."
	show_help
	exit 1
fi
if [ -z "$FORMAT" ]; then
	echo "No target format is specifed."
	show_help
	exit 1
fi

OUTPUT=/output

for f in $SRC
do
	echo "process $f..."
	if [ -d "$f" ]; then
		process_dir $f
	elif [ -f "$f" ]; then
		process_file $f
	else
		echo "Invalid video source file '$f'"
	fi
done
