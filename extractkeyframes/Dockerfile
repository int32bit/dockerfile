# Video format converter
#
# VERSION               2.8
#
FROM          krystism/ffmpeg
MAINTAINER    krystism <krystism@gmail.com>
ENV VERSION=2.8
RUN mkdir -p /output /input
ADD entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["--help"]
