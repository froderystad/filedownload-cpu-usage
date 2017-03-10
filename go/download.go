package main

import (
	"fmt"
	"net/http"
	"log"
	"time"
	"os"
)

func main() {
	url := os.Args[1]
	filename := os.Args[2]

	startTime := time.Now()

	resp, err := http.Get(url)
	if err != nil {
		log.Fatal(err)
	}
	defer resp.Body.Close()

	outFile, err := os.Create(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer outFile.Close()

	resp.Write(outFile)

	executionTime := time.Since(startTime)

	fileInfo, err := outFile.Stat()
	if err != nil {
		log.Fatal(err)
	}

	fileSizeBytes := fileInfo.Size()
	downloadSpeedKbps := 8 * fileSizeBytes / (executionTime.Nanoseconds() / 1000000)

	fmt.Printf("Downloaded and saved %d kB in %s (%d kbps)\n",
		fileSizeBytes / 1000, executionTime, downloadSpeedKbps)
}
