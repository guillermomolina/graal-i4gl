FROM ghcr.io/graalvm/graalvm-ce:java11-21.3

LABEL org.opencontainers.image.authors "Guillermo Adrián Molina <guillermoadrianmolina@hotmail.com>"

RUN microdnf install -y git maven unzip
RUN gu install native-image

ARG MAVEN_VERSION=3.8.2
ARG SHA=b0bf39460348b2d8eae1c861ced6c3e8a077b6e761fb3d4669be5de09490521a74db294cf031b0775b2dfcd57bd82246e42ce10904063ef8e3806222e686f222
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

# 5- Create the directories, download maven, validate the download, install it, remove downloaded file and set links
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && echo "Downlaoding maven" \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  \
  && echo "Checking download hash" \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  \
  && echo "Unziping maven" \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

RUN export SQUIRRELSQL_VERSION=4.1.0 && \
    curl -sLo /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip \
        http://downloads.sourceforge.net/squirrel-sql/squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    unzip -d /opt /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    ln -s /opt/squirrelsql-${SQUIRRELSQL_VERSION}-base /opt/squirrel-sql && \
    rm -f /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    curl -sLo /opt/squirrel-sql/lib/sqlite-jdbc-3.36.0.3.jar \
        https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar

RUN git clone https://github.com/guillermomolina/graal-i4gl /i4gl
RUN cd i4gl/; mvn -B dependency:resolve
RUN cd /i4gl/; mvn -B package -DskipTests
RUN gu install -L /i4gl/component/i4gl-component.jar

RUN alternatives --remove i4gl /opt/graalvm-ce-java11-21.3.0/bin/i4gl

WORKDIR /sources

ENTRYPOINT ["/opt/graalvm-ce-java11-21.3.0/bin/i4gl"]