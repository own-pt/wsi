#!/bin/bash

cd /home/milen/wsi
for f in ${WSI_INDEX_FORMATS//,/ } ; do
    ./create-index -f "$f" -n -o '/home/milen/wsi/test' "$WSI_PATH_TO_INDEX" ;
done
cd /home/milen
java -Xmx400m -Xss4m \
     -Djava.awt.headless=true \
     -jar jetty-runner.jar --port 9897 wsi/trunk/generic-gui/target/test.war
