## How to use ?

### 1. Build the image

```sh
docker build -t extractkeyframes .
```

### 2. Run

```sh
docker run -e FORMAT=frames_%3d.jpg  -v `pwd`/input:/input -v `pwd`/output:/output --rm extractkeyframes demo.flv 
```
The env `FORMAT` is the output format.
