
FROM mono:latest

ENV DNX_VERSION 1.0.0-beta5-11749
ENV DNX_USER_HOME /opt/dnx
ENV DNX_FEED https://www.myget.org/F/aspnetvnext/api/v2

RUN apt-get -qq update && apt-get -qqy install unzip

RUN curl -sSL https://raw.githubusercontent.com/aspnet/Home/dev/dnvminstall.sh | DNX_USER_HOME=$DNX_USER_HOME DNX_BRANCH=v$DNX_VERSION sh
RUN bash -c "source $DNX_USER_HOME/dnvm/dnvm.sh \
	&& dnvm install $DNX_VERSION -a default \
	&& dnvm alias default | xargs -i ln -s $DNX_USER_HOME/runtimes/{} $DNX_USER_HOME/runtimes/default"

# Install libuv for Kestrel from source code (binary is not in wheezy and one in jessie is still too old)
RUN apt-get -qqy install \
	autoconf \
	automake \
	build-essential \
	libtool
RUN LIBUV_VERSION=1.4.2 \
	&& curl -sSL https://github.com/libuv/libuv/archive/v${LIBUV_VERSION}.tar.gz | tar zxfv - -C /usr/local/src \
	&& cd /usr/local/src/libuv-$LIBUV_VERSION \
	&& sh autogen.sh && ./configure && make && make install \
	&& rm -rf /usr/local/src/libuv-$LIBUV_VERSION \
	&& ldconfig

ENV PATH $PATH:$DNX_USER_HOME/runtimes/default/bin
#RUN yes | certmgr -ssl -m https://nugetgallery.blob.core.windows.net  https://nuget.org https://www.myget.org https://www.myget.org/F/aspnetvnext/api/v2 https://go.microsoft.com https://www.myget.org
#RUN mozroots --import --machine --sync --quiet

RUN mkdir -p ~/.config/NuGet/
RUN curl -o ~/.config/NuGet/NuGet.Config -sSL https://gist.githubusercontent.com/Tazer/1ad0e439846ee26ef9a5/raw/12bc6b02b74af55154e456ec232ef3c01ec960e6/Nuget.config

COPY . /app
WORKDIR /app
RUN dnu restore -s https://www.myget.org/F/aspnetvnext/

EXPOSE 5005
ENTRYPOINT ["dnx", ".", "kestrel"]
