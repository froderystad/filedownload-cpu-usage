#!/usr/bin/env bash

# NOTE Requires 'brew install coreutils' on MacOS
date_alias="gdate" # Change to 'date' on Linux systems

file_url=$1
file_name="output.dat"

# Curl options s: silent, L: follow redirects, -o: output file
download_command="curl -s -L $file_url -o $file_name"

start_ms=$($date_alias +%s%3N)

# Run download command in background
$download_command &
pid=$!

echo "Outputting CPU usage for process $pid..."
while ps -p $pid > /dev/null && end_ms=$($date_alias +%s%3N)
do
    ps -p $pid -o %cpu | tail -n +2
    sleep 0.2
done

running_time_ms=$(expr $end_ms - $start_ms)
file_size_KibiB=$(du -k $file_name | cut -f1)
file_size_kB=$(expr 1024 \* $file_size_KibiB / 1000)
download_speed_kbps=$(expr 8000 \* $file_size_kB / $running_time_ms)
echo "Downloaded and saved $file_size_kB kB in $running_time_ms ms ($download_speed_kbps kbps)"

rm -f $file_name
