# ffmpeg
#
# VERSION               2.8
#
FROM debian:jessie
MAINTAINER    krystism <krystism@gmail.com>
# ADD sources.list /etc/apt/sources.list
RUN apt-get -y update \
	&& apt-get -y install curl gcc g++ make
RUN apt-get update && apt-get install -y --no-install-recommends \
		curl \
	&& rm -rf /var/lib/apt/lists/*
ENV LIVE555_DOWNLOAD_URL "http://www.live555.com/liveMedia/public/live555-latest.tar.gz"
RUN buildDeps='gcc make' \
	&& set -x \
	&& apt-get update && apt-get install -y $buildDeps --no-install-recommends \
	&& rm -rf /var/lib/apt/lists/* \
	&& mkdir -p /usr/src/live555 \
	&& curl -sSL "$LIVE555_DOWNLOAD_URL" -o live555.tar.gz \
	&& tar -xzf live555.tar.gz -C /usr/src/live555 --strip-components=1 \
	&& rm live555.tar.gz \
	&& cd /usr/src/live555 \
	&& ./genMakefiles linux \
	&& make \
	&& make install \
	&& cd / \
	&& rm -r /usr/src/live555 \
	&& apt-get purge -y --auto-remove $buildDeps
EXPOSE 80
CMD ["live555MediaServer"]
