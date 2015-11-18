#!/bin/bash
docker pull alpine
docker build -t ffmpeg:2.8 --rm .
docker build -t ffmpeg:latest --rm .
docker run --rm ffmpeg --help
