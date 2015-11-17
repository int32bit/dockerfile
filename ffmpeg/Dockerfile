# ffmpeg
#
# VERSION               2.8
#
FROM          debian:8
MAINTAINER    krystism <krystism@gmail.com>
ENV FFMPEG_VERSION=2.8
WORKDIR /tmp/workdir
ADD ffmpeg-release-64bit-static.tar.xz /tmp/workdir
RUN find ffmpeg-2.8-64bit-static/ -type f -executable -exec cp '{}' /usr/bin/ \; \
    && rm -rf ffmpeg-2.8-64bit-static
ENTRYPOINT /usr/bin/ffmpeg
CMD ['--help']
