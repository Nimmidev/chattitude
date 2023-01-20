# Backend
Chattitude backend.

## Run

#### Docker Compose
```bash
docker-compose up
```
Will start mariadb and the backand at once.

#### Development/Database only
On linux/macos:
```bash
./start_mysql_docker.sh
```
or in general
```bash
docker run \
    --name mysql \
    --rm \
    --env-file .env \
    -e MYSQL_RANDOM_ROOT_PASSWORD='yes' \
    -p 3306:3306 \
    mariadb
```
Will only run mariadb. The backend can then be started manually with:
```bash
./gradlew run
```

The backend and mariadb will use the credentials specified by the environment variables `MYSQL_HOSTNAME`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`. If not provided they will fallback to the values in the `.env` file.

## Build
The backend can be build/exported in two different ways:

#### Runnable Jar
```bash
# build
./gradlew shadowJar

# run
java -jar ./app/build/libs/backend-all.jar
```

#### Zip/Tar
```bash
# build
./gradlew assembleDist

# run
unzip app/build/distributions/backend.zip
./backend/bin/app
```

## Note
It could be the case that on the first `docker compose` start, the backend errors out because it could not connect to the database. This is because of the initial setup time for all tables etc. If this happens just restart and everything should work from now on.
