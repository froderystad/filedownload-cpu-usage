I got inspired by a Facebook post by Bjørn Borud March 8th 2017, where he complained 
about a process using more than 50% CPU on downloading a file and saving it to disk. 
He was using a 3.4 GHz CPU and a 35 Mbit connection, and was of the opinion that the 
high CPU usage implied bad programming. I totally agreed, but out of instinct. 
Then I got curious on how well I would do myself. And since I had nothing better to do that day 
(I was in hospital), I decided to give it a try :)

# Establishing a baseline (BASH + cURL)

First, I wanted to establish a baseline. It was most likely not going to be 50% CPU, so i decided to
use cURL, which i know is a good choice for downloading files effectively.

I should mention that I wrote this on my Mac. To get milliseconds available in Bash, i needed a 
[Brew](http://docs.brew.sh) package called `coreutils`. 
This gave me `gdate`, which supports nanosecond output.
So if you decide to try my scripts on a Mac, you will have to run `brew install coreutils` first. 
If you're on a Linux system with access to `date` with nanosecond support, you should modify `date_alias` 
in `download.sh`.

To download a really large file using BASH and cURL, you may run

```
$ cd curl
$ sh download.sh http://ftp.uio.no/pub/linux/centos/7.3.1611/isos/x86_64/CentOS-7-x86_64-NetInstall-1611.iso
Outputting CPU usage for process 94777...
  0.0
  1.9
  4.8
  9.2
 11.7
 13.1
 13.3
 13.6
 10.2
 10.6
 12.3
 13.2
 13.5
 13.6
 13.7
...
```

Note that the CPU usage stabilizes around 13%. This indicates that downloading a file
and saving it to disk is not supposed to be CPU intensive on a modern computer.

# My own attempt, using Java

I've done most of my professional programming using Java, so I decided to use it for my own attempt.

I used [Gradle](https://gradle.org) as build system this time, so you'll need that installed. 

Build the application and run it using the provided script to get CPU percentage

   
```
$ cd java
$ gradle build
$ sh download.sh http://ftp.uio.no/pub/linux/centos/7.3.1611/isos/x86_64/CentOS-7-x86_64-NetInstall-1611.iso
Outputting CPU usage for process 96008...
  0.0
 41.5
 38.9
 29.5
103.3
141.7
148.3
 99.4
 47.9
 38.3
 27.3
 24.9
 22.8
 21.6
 21.3
 21.7
 21.7
 21.2
 21.4
 21.6
...
```

Note that the CPU usage stabilizes around 20%.

Experimenting with buffer sizes (1-16 KB) did not yield significant changes in CPU utilization or 
 download time. I also tried another, allegedly more effective strategy using NIO Channels, but to
 no observable improvement.

Please note that there is virtually no error checking in this code, so now you're warned! 
Don't complain to me if you mess this up by typing in anything else than proper URLs ;)

# Go

I wanted to make a second go at this download challenge, and _obviously_ had to choose 
[Go](https://golang.org/) :) I have some experience from Go, a couple of years back. I find it
very good for quickly writing small tools like this.

```
$ cd go
$ go build download.go
$ sh download.sh http://ftp.uio.no/pub/linux/centos/7.3.1611/isos/x86_64/CentOS-7-x86_64-NetInstall-1611.iso
Outputting CPU usage for process 95202...
  0.0
  8.7
 13.2
 20.5
 24.6
 25.5
 26.5
 27.4
 27.3
 27.6
 27.5
 26.9
 27.6
 27.8
 26.8
...
```

Note that the CPU usage stabilizes around 27%.

# Summary

I tested downloading a large file (Linux ISO image) on my 2012 MacBook Air in three different ways.
The cURL command is least CPU intensive with 13%. Then Java came in second; after a CPU peak during startup, 
it stabilized around 21%. Go placed third, with very stable performance around 27%.

NOTE: More execution runs would give more accurate numbers, so please don't consider these numbers as 
 being exact.

# Future plans

I may have fun at a later time, further optimizing the code, or trying the same task in other languages, 
like JavaScript or Ruby. In that case, I will update this project. 
