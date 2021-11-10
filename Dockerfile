FROM ghcr.io/graalvm/graalvm-ce:java11-20.3.0

LABEL org.opencontainers.image.authors "Guillermo Adrián Molina <guillermoadrianmolina@hotmail.com>"

RUN microdnf install -y git maven unzip
#RUN gu install native-image

ENV SQUIRRELSQL_VERSION=4.3.0

RUN curl -Lo /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip \
        http://downloads.sourceforge.net/squirrel-sql/squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    unzip -d /opt /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    ln -s /opt/squirrelsql-${SQUIRRELSQL_VERSION}-base /opt/squirrel-sql

RUN git clone https://github.com/guillermomolina/graal-i4gl /i4gl && \
    cd i4gl/ && \
    mvn -B dependency:resolve

# RUN cd /i4gl/; mvn -B package -DskipTests
# RUN gu install -L /i4gl/component/i4gl-component.jar

# WORKDIR /sources

# ENTRYPOINT ["/opt/graalvm-ce-java11-20.3.0/bin/i4gl"]