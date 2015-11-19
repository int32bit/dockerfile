#!/bin/bash
docker pull alpine
docker build -t krystism/ffserver:2.8 --rm .
docker build -t krystism/ffserver:latest --rm .
