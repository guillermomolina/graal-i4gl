FROM ghcr.io/graalvm/graalvm-ce:java11-20.3.0

LABEL org.opencontainers.image.authors "Guillermo Adrián Molina <guillermoadrianmolina@hotmail.com>"

ARG USERNAME=vscode
ARG USER_UID=66641
ARG USER_GID=100
#ARG USER_GID=$USER_UID

RUN microdnf install -y git maven unzip shadow-utils sudo
#RUN gu install native-image

# Create the user
# RUN groupadd --gid $USER_GID $USERNAME && \
RUN useradd --uid $USER_UID --gid $USER_GID -m $USERNAME && \
    echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME && \
    chmod 0440 /etc/sudoers.d/$USERNAME

RUN export SQUIRRELSQL_VERSION=4.1.0 && \
    curl -sLo /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip \
        http://downloads.sourceforge.net/squirrel-sql/squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    unzip -d /opt /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    ln -s /opt/squirrelsql-${SQUIRRELSQL_VERSION}-base /opt/squirrel-sql && \
    rm -f /squirrelsql-${SQUIRRELSQL_VERSION}-base.zip && \
    curl -sLo /opt/squirrel-sql/lib/sqlite-jdbc-3.36.0.3.jar \
        https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar

ADD files/chinook.tar.xz /database
COPY files/SQLDrivers.xml /home/vscode/.squirrel-sql/SQLDrivers.xml
COPY files/SQLAliases23.xml /home/vscode/.squirrel-sql/SQLAliases23.xml

# RUN git clone https://github.com/guillermomolina/graal-i4gl /i4gl && \
#     cd i4gl/ && \
#     mvn -B dependency:resolve && \
#     mvn -B package -DskipTests

# RUN gu install -L /i4gl/component/i4gl-component.jar

# WORKDIR /workspaces

# ENTRYPOINT ["/opt/graalvm-ce-java11-20.3.0/bin/i4gl"]

# [Optional] Set the default user. Omit if you want to keep the default as root.
USER $USERNAME
