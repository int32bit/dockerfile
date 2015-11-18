#!/bin/bash
docker pull alpine
docker build -t ffserver:2.8 --rm .
docker build -t ffserver:latest --rm .
