# Desktop Client
Chattitude desktop client.

## Run
```bash
./gradlew run
```
By default the client tries to connect to a backend running on `ws://localhost:8080`. This can be changed by setting the environment variable `SERVER_URL` i.e.:
```bash
SERVER_URL=wss://some.url:port ./gradlew run
```

## Build

#### Runnable Jar
```bash
# build
./gradlew jar

# run
java -jar ./app/build/libs/desktop-client.jar
```

#### Zip/Tar
```bash
# build
./gradlew assembleDist

# run
unzip app/build/distributions/desktop-client.zip
./desktop-client/bin/app
```
