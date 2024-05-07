#!/bin/zsh

# 환경별 도메인 주소
declare -A remote_servers
remote_servers[dev]="https://dev.foo.com"
remote_servers[qa]="https://qa.foo.com"

# 입력된 인자 (환경) 확인
ENV=$1
PORT=$2

if [[ -z "${remote_servers[$ENV]}" ]]; then
    echo "Invalid environment specified. you can only select the following values: "
    echo
    for key in ${(k)remote_servers}; do
        echo "$key"
    done
    exit 1
fi

# 선택된 도메인 주소
REMOTE_SERVER_DOMAIN=${remote_servers[$ENV]}

# Nginx 설정 파일 셋업
local_uris=($(<uri/local_uris.txt))
remote_uris=($(<uri/remote_uris.txt))

remote_pass_template=$(cat <<EOF
location \$remote_pass_uri {
    proxy_pass $REMOTE_SERVER_DOMAIN\$remote_pass_uri;
    proxy_ssl_server_name on;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
}
EOF
)

local_pass_template=$(cat <<EOF
location \$local_pass_uri {
    proxy_pass http://host.docker.internal:$PORT\$local_pass_uri;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
}
EOF
)

# 개발 서버로 보낼 URI 목록 location 블록 생성
REMOTE_PASS_URI_LIST=""
for uri in "${remote_uris[@]}"; do
    REMOTE_PASS_URI_LIST+="$(echo "$remote_pass_template" | awk -v uri="$uri" '{gsub(/\$remote_pass_uri/, uri); print}')"
    REMOTE_PASS_URI_LIST+=$'\n'
done

# 로컬 서버로 보낼 URI 목록 location 블록 생성
LOCAL_PASS_URI_LIST=""
for uri in "${local_uris[@]}"; do
    LOCAL_PASS_URI_LIST+="$(echo "$local_pass_template" | awk -v uri="$uri" '{gsub(/\$local_pass_uri/, uri); print}')"
    LOCAL_PASS_URI_LIST+=$'\n'
done

FULL_PASS_URI_LIST=$REMOTE_PASS_URI_LIST$'\n'$LOCAL_PASS_URI_LIST
export FULL_PASS_URI_LIST
envsubst '$FULL_PASS_URI_LIST' < "./nginx_template.conf" > "./nginx_generated.conf"

# 컨테이너 실행
docker run -d --network="host" -v ./nginx_generated.conf:/etc/nginx/nginx.conf --name nginx nginx
docker ps -a

echo
echo "@@@ Complete init for $ENV environment @@@"