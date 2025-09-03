#!/bin/bash 

eval "$(ssh-agent -s)" && ssh-add ~/.ssh/id_rsa

./gradlew -PadultScrape aNR && scp /home/leeroy/projects/LeeroyFlix/Video/build/outputs/apk/noamazon/release/org.leeroy.media-*-arm*.apk leeroy@192.168.1.200:/mnt/leeroys_stuff/Media/
