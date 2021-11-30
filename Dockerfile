
# Development image

FROM ghcr.io/graalvm/graalvm-ce:java11-21.3.0

RUN microdnf install -y git maven unzip curl
#RUN gu install native-image

RUN export SQUIRRELSQL_VERSION=4.1.0 && \
    curl -sLo /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip \
        http://downloads.sourceforge.net/squirrel-sql/squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    unzip -d /opt /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    ln -s /opt/squirrelsql-${SQUIRRELSQL_VERSION}-base /opt/squirrel-sql && \
    rm -f /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    curl -sLo /opt/squirrel-sql/lib/sqlite-jdbc-3.36.0.3.jar \
        https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar

# Force clone
ADD https://api.github.com/repos/guillermomolina/graal-i4gl/git/refs/heads/master version.json        
RUN git clone https://github.com/guillermomolina/graal-i4gl /i4gl
#RUN cd /i4gl; mvn -B dependency:resolve
RUN cd /i4gl; mvn -B package -DskipTests

# Compiler image

FROM ghcr.io/graalvm/graalvm-ce:java11-21.3.0
LABEL org.opencontainers.image.authors "Guillermo Adrián Molina <guillermoadrianmolina@hotmail.com>"

RUN microdnf install -y unzip curl
#RUN gu install native-image

RUN export SQUIRRELSQL_VERSION=4.1.0 && \
    curl -sLo /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip \
        http://downloads.sourceforge.net/squirrel-sql/squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    unzip -d /opt /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    ln -s /opt/squirrelsql-${SQUIRRELSQL_VERSION}-base /opt/squirrel-sql && \
    rm -f /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    curl -sLo /opt/squirrel-sql/lib/sqlite-jdbc-3.36.0.3.jar \
        https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar

COPY --from=0 /i4gl/component/i4gl-component.jar /i4gl/component/i4gl-component.jar
RUN gu install -L /i4gl/component/i4gl-component.jar
RUN alternatives --remove i4gl /opt/graalvm-ce-java11-21.3.0/bin/i4gl

WORKDIR /sources

ENTRYPOINT ["/opt/graalvm-ce-java11-21.3.0/bin/i4gl"]