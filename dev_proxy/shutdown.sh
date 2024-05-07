#!/bin/sh

docker stop nginx
docker rm nginx
docker ps -a

rm -f nginx_generated.conf

echo
echo '@@@ Complete shutdown for shopl nginx @@@'