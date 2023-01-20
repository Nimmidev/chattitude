docker run \
    --name mysql \
    --rm \
    --env-file .env \
    -e MYSQL_RANDOM_ROOT_PASSWORD='yes' \
    -p 3306:3306 \
    mariadb
