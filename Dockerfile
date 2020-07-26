FROM tomcat:8.5

MAINTAINER Alexandre Rademaker

COPY wsi-src/src/common-gui/target/common-gui-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/demo.war
ADD data /root/data

