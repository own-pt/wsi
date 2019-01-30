#!/bin/bash

# must specify environment variables (see dockerfile for examples)
for f in ${WSI_INDEX_FORMATS//,/ } ; do
    bash "$WSI_CREATE_INDEX" -f "$f" -n -o "$WSI_INDEX_PATH" "$WSI_PATH_TO_INDEX" ;
done

java -Xmx400m -Xss4m \
     -Djava.awt.headless=true \
     -jar "$JETTY_RUNNER_PATH" --port 9897 "$WSI_WAR_PATH"
