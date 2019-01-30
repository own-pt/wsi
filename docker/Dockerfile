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

RUN git clone --depth 1 https://github.com/own-pt/wsi.git \
    && mkdir /home/milen/wsi/test

RUN bash /home/milen/wsi/compile

RUN cd /home/milen/wsi/trunk/generic-gui \
    && mvn compile \
    && mvn war:war

RUN curl http://repo2.maven.org/maven2/org/mortbay/jetty/jetty-runner/7.6.9.v20130131/jetty-runner-7.6.9.v20130131.jar > jetty-runner.jar

EXPOSE 9897

ENV WSI_INDEX_FORMATS='mrs' \
    WSI_PATH_TO_INDEX='/home/milen/wsi/example/00110.gz'

CMD cd /home/milen/wsi \
    && for format in ${WSI_INDEX_FORMATS//,/ } do \
       ./create-index -f "$format" -n -o '/home/milen/wsi/test' "$WSI_PATH_TO_INDEX" ; done \
    && cd /home/milen \
    && java -Xmx400m -Xss4m \
      -Djava.awt.headless=true \
      -jar jetty-runner.jar --port 9897 wsi/trunk/generic-gui/target/test.war
#java -Xmx400m -Xss4m -Djava.awt.headless=true -jar jetty-runner.jar --port 9897 wsi/trunk/generic-gui/target/test.war
