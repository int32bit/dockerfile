#!/bin/bash
docker pull alpine
docker build -t krystism/ffprobe:2.8 --rm .
docker build -t krystism/ffprobe:latest --rm .
