FROM ubuntu:14.04

RUN apt-get update -y \
    && apt-get install -y --no-install-recommends --no-install-suggests \
       default-jre default-jdk git curl emacs24-nox \
       maven graphviz \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -ms /bin/bash milen

USER milen

WORKDIR /home/milen

RUN curl http://repo2.maven.org/maven2/org/mortbay/jetty/jetty-runner/7.6.9.v20130131/jetty-runner-7.6.9.v20130131.jar > jetty-runner.jar

RUN git clone --depth 1 https://github.com/own-pt/wsi.git
#COPY --chown=milen:milen ./ /home/milen/wsi/

RUN bash /home/milen/wsi/compile && mkdir /home/milen/wsi/test

# have it install dependencies before git clone/ADD
RUN cd /home/milen/wsi/trunk/generic-gui \
    && mvn compile \
    && mvn war:war

EXPOSE 9897

ENV WSI_INDEX_FORMATS='mrs,eds' \
    WSI_PATH_TO_INDEX='/home/milen/wsi/example/00110.gz' \
    JETTY_RUNNER_PATH='/home/milen/jetty-runner.jar' \
    WSI_CREATE_INDEX='/home/milen/wsi/create-index' \
    WSI_INDEX_PATH='/home/milen/wsi/test/' \
    WSI_WAR_PATH='/home/milen/wsi/trunk/generic-gui/target/test.war'

CMD bash /home/milen/wsi/run.sh
