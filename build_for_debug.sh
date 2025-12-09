#!/bin/bash 

eval "$(ssh-agent -s)" && ssh-add ~/.ssh/id_rsa

./gradlew -PadultScrape aND && scp /home/leeroy/projects/Archos/Video/build/outputs/apk/noamazon/debug/com.archos.media-*-arm*.apk leeroy@192.168.1.200:/mnt/leeroys_stuff/Media/
