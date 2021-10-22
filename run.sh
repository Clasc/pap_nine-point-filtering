#!/bin/bash
if [ -z $1 ]; then
    echo "no image file specified, setting to IMAGE1.JPG"
    input=IMAGE1.JPG
    else
      input=$1
fi

if [ -z $2 ]; then
    echo "no out file specified, setting to out.txt"
    output=out.txt
      else
      output=$2
fi
java -classpath out/ TestImageFilter $input $output