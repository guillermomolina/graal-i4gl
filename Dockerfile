FROM ghcr.io/graalvm/graalvm-ce:java11-21.3.0

RUN microdnf install -y git maven unzip curl
RUN gu install native-image

RUN export SQUIRRELSQL_VERSION=4.1.0 && \
    curl -sLo /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip \
        http://downloads.sourceforge.net/squirrel-sql/squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    unzip -d /opt /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    ln -s /opt/squirrelsql-${SQUIRRELSQL_VERSION}-base /opt/squirrel-sql && \
    rm -f /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    curl -sLo /opt/squirrel-sql/lib/sqlite-jdbc-3.36.0.3.jar \
        https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar

RUN git clone --depth 1 --branch 0.2.0-SNAPSHOT https://github.com/guillermomolina/graal-i4gl /i4gl
#RUN cd /i4gl; mvn -B dependency:resolve
RUN cd /i4gl; mvn -B package -DskipTests

RUN gu install -L /i4gl/component/i4gl-component.jar
RUN alternatives --remove i4gl /opt/graalvm-ce-java11-21.3.0/bin/i4gl

WORKDIR /sources

ENTRYPOINT ["/opt/graalvm-ce-java11-21.3.0/bin/i4gl"]