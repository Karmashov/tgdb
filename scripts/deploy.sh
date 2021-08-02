#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa \
    target/tgdb-0.0.1-SNAPSHOT.jar \
    ubuntu@3.68.226.248:/home/ubuntu/java

echo 'Restart server...'

#pgrep java | xargs kill -9
ssh -i ~/.ssh/id_rsa ubuntu@3.68.226.248 << EOF
killall java &
nohup java -jar ./java/tgdb-0.0.1-SNAPSHOT.jar > ./java/log.txt &
EOF

echo 'Bye'