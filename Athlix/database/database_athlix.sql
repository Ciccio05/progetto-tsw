/*
 * ATTENZIONE:
 * questo script ricrea completamente il database AthliX.
 * Eseguirlo solo per la prima installazione o per un reset completo.
 * L'esecuzione cancella eventuali utenti, ordini, recensioni
 * e modifiche effettuate durante l'utilizzo del sito.
 */
DROP DATABASE IF EXISTS athlix_db;
CREATE DATABASE athlix_db
/* Permette di memorizzare correttamente caratteri speciali e simboli Unicode. */
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE athlix_db;

CREATE TABLE CATEGORIA_SPORT (
    ID_categoria INT PRIMARY KEY AUTO_INCREMENT,
    Nome_categoria VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE SCONTO (
    ID_sconto INT PRIMARY KEY AUTO_INCREMENT,
    Percentuale DECIMAL(5, 2) NOT NULL,
    Data_inizio DATE NOT NULL,
    Data_fine DATE NOT NULL,

    CONSTRAINT CHK_SCONTO_PERCENTUALE
        CHECK (Percentuale > 0 AND Percentuale <= 100), /* Controlla che la percentuale sia maggiore di 0 e non superiore a 100. */
    CONSTRAINT CHK_SCONTO_DATE
        CHECK (Data_fine >= Data_inizio) /* Controlla che la data di fine non preceda quella di inizio. */
);

CREATE TABLE METODO_PAGAMENTO (
    ID_metodo INT PRIMARY KEY AUTO_INCREMENT,
    Tipo_carta VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE BOT_ASSISTENZA (
    ID_bot INT PRIMARY KEY AUTO_INCREMENT,
    Nome_bot VARCHAR(50) NOT NULL
);

CREATE TABLE UTENTE (
    ID_utente INT PRIMARY KEY AUTO_INCREMENT,
    Tipo_utente ENUM('Admin', 'Cliente') NOT NULL,
    Nome_utente VARCHAR(50) NOT NULL,
    Cognome_utente VARCHAR(50) NOT NULL,
    Email VARCHAR(120) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Data_nascita DATE,
    Indirizzo_utente VARCHAR(255),
    Numero_telefono VARCHAR(20)
);

CREATE TABLE PRODOTTO (
    ID_prodotto INT PRIMARY KEY AUTO_INCREMENT,
    Nome VARCHAR(120) NOT NULL,
    Prezzo DECIMAL(10, 2) NOT NULL,
    IVA DECIMAL(5, 2) NOT NULL DEFAULT 22.00,
    Descrizione TEXT,
    Immagine VARCHAR(500),
    Visibile BOOLEAN NOT NULL DEFAULT TRUE, /* Indica se il prodotto deve essere mostrato nel catalogo pubblico. */
    Disponibilita BOOLEAN NOT NULL DEFAULT TRUE, /* Indica se il prodotto può essere acquistato. Visibile e Disponibilita sono separati per
       mantenere nello storico gli articoli eliminati logicamente e mostrare nel catalogo quelli temporaneamente non disponibili. */
    Quantita INT NOT NULL,
    ID_categoria INT NOT NULL,
    ID_sconto INT,

    /* Controlla che il prezzo sia positivo. */
    CONSTRAINT CHK_PRODOTTO_PREZZO
        CHECK (Prezzo > 0),

	/* Controlla che l'IVA sia compresa tra 0 e 100. */
    CONSTRAINT CHK_PRODOTTO_IVA
        CHECK (IVA >= 0 AND IVA <= 100),

	/* Controlla che la quantità non sia negativa. */
    CONSTRAINT CHK_PRODOTTO_QUANTITA
        CHECK (Quantita >= 0),

/* Collega il prodotto alla categoria. */
    CONSTRAINT FK_PRODOTTO_CATEGORIA
        FOREIGN KEY (ID_categoria)
        REFERENCES CATEGORIA_SPORT(ID_categoria)
        ON UPDATE CASCADE,

/*Collegamento tra prodotto e sconto, se uno sconto viene cancellato ID_sconto del prodotto diventa NULL.*/
    CONSTRAINT FK_PRODOTTO_SCONTO
        FOREIGN KEY (ID_sconto)
        REFERENCES SCONTO(ID_sconto)
        ON UPDATE CASCADE
        ON DELETE SET NULL

);

CREATE TABLE CARRELLO (
    ID_carrello INT PRIMARY KEY AUTO_INCREMENT,
    Data_creazione DATE NOT NULL,
    Stato VARCHAR(50) NOT NULL,
    ID_utente INT NOT NULL,

/* Ogni utente ha un solo carrello. */
    CONSTRAINT UQ_CARRELLO_UTENTE UNIQUE (ID_utente),

/* Se l'utente viene eliminato, viene eliminato anche il suo carrello. */
    CONSTRAINT FK_CARRELLO_UTENTE
        FOREIGN KEY (ID_utente)
        REFERENCES UTENTE(ID_utente)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE PAGAMENTO (
    ID_pagamento INT PRIMARY KEY AUTO_INCREMENT,
    Stato VARCHAR(50) NOT NULL,
    Data_pagamento DATE NOT NULL,
    ID_metodo INT NOT NULL,

    /*collego il metodo al pagamento*/
    CONSTRAINT FK_PAGAMENTO_METODO
        FOREIGN KEY (ID_metodo)
        REFERENCES METODO_PAGAMENTO(ID_metodo)
        ON UPDATE CASCADE
);

CREATE TABLE ORDINE (
    ID_ordine INT PRIMARY KEY AUTO_INCREMENT,
    Data_ordine DATE NOT NULL,
    Stato_o VARCHAR(50) NOT NULL,
    Totale DECIMAL(10, 2) NOT NULL,
    ID_utente INT NOT NULL,
    ID_pagamento INT NOT NULL,

/*un pagamento relativo ad un solo ordine*/
    CONSTRAINT UQ_ORDINE_PAGAMENTO UNIQUE (ID_pagamento),
    /*totale ordine non può essere negativo*/
    CONSTRAINT CHK_ORDINE_TOTALE CHECK (Totale >= 0),

    CONSTRAINT FK_ORDINE_UTENTE
        FOREIGN KEY (ID_utente)
        REFERENCES UTENTE(ID_utente)
        ON UPDATE CASCADE,

/*un pagamento utilizzato da un ordine non può essere eliminato*/
    CONSTRAINT FK_ORDINE_PAGAMENTO
        FOREIGN KEY (ID_pagamento)
        REFERENCES PAGAMENTO(ID_pagamento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE SPEDIZIONE (
    ID_spedizione INT PRIMARY KEY AUTO_INCREMENT,
    Indirizzo VARCHAR(255) NOT NULL,
    Stato_s VARCHAR(50) NOT NULL,
    Data_partenza DATE,
    Data_consegna DATE,
    ID_ordine INT NOT NULL UNIQUE,

/*check sulle date*/
    CONSTRAINT CHK_SPEDIZIONE_DATE
        CHECK (
            Data_partenza IS NULL
            OR Data_consegna IS NULL
            OR Data_consegna >= Data_partenza
        ),
/*se elimino un ordine elimino anche la relativa spedizione*/
    CONSTRAINT FK_SPEDIZIONE_ORDINE
        FOREIGN KEY (ID_ordine)
        REFERENCES ORDINE(ID_ordine)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE CHAT (
    ID_chat INT PRIMARY KEY AUTO_INCREMENT,
    Data_inizio DATE NOT NULL,
    Stato_c VARCHAR(50) NOT NULL,
    ID_utente INT NOT NULL,
    ID_bot INT NOT NULL,

    CONSTRAINT FK_CHAT_UTENTE
        FOREIGN KEY (ID_utente)
        REFERENCES UTENTE(ID_utente)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_CHAT_BOT
        FOREIGN KEY (ID_bot)
        REFERENCES BOT_ASSISTENZA(ID_bot)
        ON UPDATE CASCADE
);

CREATE TABLE RECENSIONE (
    ID_recensione INT PRIMARY KEY AUTO_INCREMENT,
    Data_recensione DATE NOT NULL,
    Voto TINYINT NOT NULL,
    Commento VARCHAR(1000) NOT NULL,
    ID_utente INT NOT NULL,
    ID_prodotto INT NOT NULL,

/*controllo sulla recensione*/
    CONSTRAINT UQ_RECENSIONE_UTENTE_PRODOTTO
        UNIQUE (ID_utente, ID_prodotto),
    CONSTRAINT CHK_RECENSIONE_VOTO
        CHECK (Voto BETWEEN 1 AND 5),

    CONSTRAINT FK_RECENSIONE_UTENTE
        FOREIGN KEY (ID_utente)
        REFERENCES UTENTE(ID_utente)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_RECENSIONE_PRODOTTO
        FOREIGN KEY (ID_prodotto)
        REFERENCES PRODOTTO(ID_prodotto)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE INCLUDERE (
    ID_carrello INT NOT NULL,
    ID_prodotto INT NOT NULL,
    Quantita_ordine INT NOT NULL,

    PRIMARY KEY (ID_carrello, ID_prodotto),
    /* Nel carrello deve essere presente almeno un'unità del prodotto. */
    CONSTRAINT CHK_INCLUDERE_QUANTITA CHECK (Quantita_ordine > 0),

    CONSTRAINT FK_INCLUDERE_CARRELLO
        FOREIGN KEY (ID_carrello)
        REFERENCES CARRELLO(ID_carrello)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

/* Se un prodotto viene eliminato fisicamente, viene rimosso dagli eventuali carrelli. */
    CONSTRAINT FK_INCLUDERE_PRODOTTO
        FOREIGN KEY (ID_prodotto)
        REFERENCES PRODOTTO(ID_prodotto)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE CONTENERE (
    ID_ordine INT NOT NULL,
    ID_prodotto INT NOT NULL,
    Quantita_ordine INT NOT NULL DEFAULT 1,
    Prezzo_acquisto DECIMAL(10, 2) NOT NULL, /* Conserva il prezzo pagato al momento dell’ordine per garantire l’integrità storica. */
    IVA_acquisto DECIMAL(5, 2) NOT NULL,

    PRIMARY KEY (ID_ordine, ID_prodotto),
 /*controlli sull'ordine*/
    CONSTRAINT CHK_CONTENERE_QUANTITA
        CHECK (Quantita_ordine > 0),
    CONSTRAINT CHK_CONTENERE_PREZZO
        CHECK (Prezzo_acquisto >= 0),
    CONSTRAINT CHK_CONTENERE_IVA
        CHECK (IVA_acquisto >= 0 AND IVA_acquisto <= 100),

    CONSTRAINT FK_CONTENERE_ORDINE
        FOREIGN KEY (ID_ordine)
        REFERENCES ORDINE(ID_ordine)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_CONTENERE_PRODOTTO
        FOREIGN KEY (ID_prodotto)
        REFERENCES PRODOTTO(ID_prodotto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
        /* RESTRICT preserva l'integrità storica cosi che un prodotto già presente in un ordine non può essere cancellato fisicamente.*/
);

CREATE TABLE INSERIRE (
    ID_utente INT NOT NULL,
    ID_metodo INT NOT NULL,
    Ultime_quattro CHAR(4),
    Intestatario VARCHAR(100),
    Scadenza CHAR(5),

    PRIMARY KEY (ID_utente, ID_metodo),

    CONSTRAINT FK_INSERIRE_UTENTE
        FOREIGN KEY (ID_utente)
        REFERENCES UTENTE(ID_utente)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_INSERIRE_METODO
        FOREIGN KEY (ID_metodo)
        REFERENCES METODO_PAGAMENTO(ID_metodo)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
