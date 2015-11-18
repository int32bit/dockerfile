# ffmpeg
#
# VERSION               2.8
#
FROM          alpine
MAINTAINER    krystism <krystism@gmail.com>
ENV FFMPEG_VERSION=2.8
WORKDIR /tmp/workdir
ADD ffmpeg-release-64bit-static.tar.xz /tmp/workdir
RUN    cp ffmpeg-2.8-64bit-static/ffmpeg /usr/bin/ \
    && cp ffmpeg-2.8-64bit-static/ffprobe /usr/bin/ \
    && cp ffmpeg-2.8-64bit-static/ffserver /usr/bin/ \
    && rm -rf ffmpeg-2.8-64bit-static
ENTRYPOINT /usr/bin/ffmpeg
CMD ['--help']
