#!/usr/bin/env bash

file_url=$1
file_name="output.dat"

download_command="./download $file_url $file_name"

# Run download command in background
$download_command &
pid=$!

echo "Outputting CPU usage for process $pid..."
while ps -p $pid > /dev/null
do
    ps -p $pid -o %cpu | tail -n +2
    sleep 0.2
done

rm -f $file_name
