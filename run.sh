#!/bin/bash

for f in ${WSI_INDEX_FORMATS//,/ } ; do
    ./create-index -f "$f" -n -o '/home/milen/wsi/test/' "$WSI_PATH_TO_INDEX" ;
done

java -Xmx400m -Xss4m \
     -Djava.awt.headless=true \
     -jar "$JETTY_RUNNER_PATH" --port 9897 wsi/trunk/generic-gui/target/test.war
