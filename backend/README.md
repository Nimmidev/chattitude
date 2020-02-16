# Backend
Server of Chattitude.

Requires a MariaDB database, see 'Running' for specifing connection details.

## Running
* Add all jars in libs folder to "Project structure" > Modules > Dependencies > + > Jars Or Directories
* Alle Jars auch unter Artifacts als extracted hinzufügen
* Build artifact
* `docker-compose up`, sollte dann den MySQL-Server und den Chattitude-Server starten
    * Alternativ: Man kann auch den MySQL-Server in Docker laufen lassen und den Chattitude-Server direkt ohne Docker starten, dann muss man aber die MySQL-Credentials anpassen, die in den Enviroment-Variablen übergeben werden
        * MySQL starten: `docker run --env-file .env -e MYSQL_RANDOM_ROOT_PASSWORD='yes' -p 3306:3306 mariadb` (Inhalt von DB bleibt nur bis zum beenden erhalten)
        * Edit configuration > In 'Enviroment Variables' folgendes einfügen:
        `MYSQL_HOSTNAME=localhost;MYSQL_DATABASE=chattitude;MYSQL_USER=chattitudeUser;MYSQL_PASSWORD=1234`

## Debugging
* Remove all tables (incomplete): 
    ```
    USE chattitude;
    SET FOREIGN_KEY_CHECKS = 0;
    DROP TABLE ChatMessage;
    DROP TABLE Conversation;
    DROP TABLE ConversationMember;
    DROP TABLE User;
    SET FOREIGN_KEY_CHECKS = 1;
    
    ```
