#!/bin/bash
docker pull alpine
docker build -t ffprobe:2.8 --rm .
docker build -t ffprobe:latest --rm .
