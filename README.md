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

To download a file using BASH and cURL, you may run

```
cd curl
sh download.sh http://get.skype.com/go/getskype
```

Note that the CPU usage is mostly being reported as low as 0.0%. This indicates that downloading a file
and saving it to disk is not supposed to be CPU intensive on a modern computer.

# My own attempt, using Java

I've done most of my professional programming using Java, so I decided to use it for my own attempt.

I used [Gradle](https://gradle.org) as build system this time, so you'll need that installed. 

Build the application and run it using the provided script to get CPU percentage

   
```
cd java
gradle build
sh download.sh http://get.skype.com/go/getskype
```

Note that the CPU usage is significantly higher in Java than using cURL (15-30% vs 0-2% on my computer).

Please note that there is virtually no error checking in this code, so now you're warned! 
Don't complain to me if you mess this up by typing in anything else than proper URLs ;)

# Future plans

I may have fun at a later time, optimizing the Java code, or trying the same task in other languages, 
like JavaScript, Go or Ruby. In that case, I will update this project. 
