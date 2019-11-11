# Backend
Server of Chattitude

## Running
* Add all jars in libs folder to "Project structure" > Modules > + > Jars Or Directories
* Alle Jars auch unter Artifacts als extracted hinzufügen
* Build artifact
* `docker-compose up`, sollte dann den MySQL-Server und den Chattitude-Server starten
* Man kann auch den MySQL-Server in Docker laufen lassen und den Chattidue-Server direkt ohne Docker starten, dann muss man aber die MySQL-Credentials anpassen, die in den Enviroment-Variablen übergeben werden
