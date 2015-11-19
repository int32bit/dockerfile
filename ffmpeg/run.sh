#!/bin/bash
docker pull alpine
docker build -t krystism/ffmpeg:2.8 --rm .
docker build -t krystism/ffmpeg:latest --rm .
