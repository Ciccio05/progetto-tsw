# AthliX

AthliX è un'applicazione web e-commerce dedicata ad articoli sportivi,
sviluppata per il progetto di Tecnologie Software per il Web.

## Tecnologie

- Java 17
- Servlet / JSP
- Apache Tomcat 9
- MySQL
- HikariCP
- HTML, CSS, JavaScript
- AJAX
- architettura MVC

Il progetto è un Eclipse Dynamic Web Project e non utilizza Maven.

## Requisiti

- Eclipse IDE for Enterprise Java and Web Developers
- JDK 17 o superiore
- Apache Tomcat 9
- MySQL 8.x

Il progetto utilizza `javax.servlet`, quindi deve essere eseguito con
Tomcat 9 e non con Tomcat 10/11.

## Database

Gli script si trovano in:

    database/database_athlix.sql
    database/popolamento.sql

Per una nuova installazione eseguirli nell'ordine:

    1. database_athlix.sql
    2. popolamento.sql

ATTENZIONE:
`database_athlix.sql` contiene `DROP DATABASE IF EXISTS athlix_db`.

Va quindi eseguito solo per una nuova installazione o per un reset
completo del database.

Durante il normale utilizzo del progetto NON è necessario rieseguire
gli script SQL.

Per riprodurre lo stato corrente del progetto consegnato,
importare il file:

database/athlix_db_final.sql

Questo dump contiene sia la struttura del database sia i dati
attualmente presenti nell'applicazione.

## Configurazione Eclipse

Importare il progetto con:

    File -> Import...
    General -> Existing Projects into Workspace

Configurazione prevista:

    Java: 17
    Dynamic Web Module: 4.0
    Runtime: Apache Tomcat 9
    Context root: athlix

Deployment Assembly:

    /src/main/java   -> /WEB-INF/classes
    /src/main/webapp -> /

## Configurazione MySQL

La password del database non è salvata nel codice sorgente.

Aggiungere ai VM Arguments di Tomcat:

    -DATHLIX_DB_USER=root
    -DATHLIX_DB_PASSWORD=LA_TUA_PASSWORD_MYSQL

In alternativa è possibile utilizzare un utente MySQL dedicato.

URL JDBC opzionale:

    -DATHLIX_DB_URL=jdbc:mysql://localhost:3306/athlix_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Rome

## Immagini

Le immagini demo sono contenute in:

    src/main/webapp/images/products/

Se un prodotto non possiede un'immagine, tutte le viste utilizzano:

    /images/products/default-product.svg

Le immagini caricate dall'Admin possono essere conservate nella
cartella inclusa nel progetto:

    uploads/prodotti/

Nei VM Arguments di Tomcat impostare:

    -DATHLIX_UPLOAD_DIR="PERCORSO_DEL_PROGETTO\athlix_final\uploads\prodotti"
## Avvio

In Eclipse:

    Project -> Clean...
    Tomcat -> Clean
    Tomcat -> Publish
    Start

Aprire:

    http://localhost:8080/athlix/home

## Credenziali demo

Admin:

    Email: admin@athlix.it
    Password: Admin2026!

## Funzionalità principali

- registrazione e login
- catalogo e ricerca AJAX
- filtri prodotti
- carrello e checkout
- storico ordini
- recensioni
- modifica profilo
- gestione prodotti Admin
- sconti
- gestione ordini e spedizioni
- upload immagini
- assistenza tramite bot

## Note

Le dipendenze runtime sono già presenti in:

    src/main/webapp/WEB-INF/lib

La Servlet API viene fornita da Tomcat 9.

Il progetto non utilizza Maven.
