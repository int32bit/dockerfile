## How to use ?

### 1. Build the image

```sh
docker build -t fmtconverter .
```

### 2. Run

```sh
docker run -e FORMAT=mp4  -v `pwd`/input:/input -v `pwd`/output:/output --rm fmtconverter src
```

** You must set `FORMAT` environment to specifed target format.**
