USE athlix_db;

START TRANSACTION; /* Raggruppa le operazioni di popolamento in un’unica transazione. */

-- 1. CATEGORIE SPORT

INSERT INTO CATEGORIA_SPORT (Nome_categoria)
VALUES
('Calcio'), ('Basket'), ('Running'), ('Fitness'), ('Tennis'), ('Nuoto'), ('Ciclismo'), ('Pallavolo');

-- 2. SCONTI

INSERT INTO SCONTO
(Percentuale, Data_inizio, Data_fine)
VALUES
(10.00, '2026-09-01', '2026-09-30'),
(20.00, '2026-09-01', '2026-10-15'),
(15.00, '2026-10-01', '2026-10-31'),
(25.00, '2026-11-01', '2026-11-30');

-- 3. METODI DI PAGAMENTO

INSERT INTO METODO_PAGAMENTO
(Tipo_carta)
VALUES
('Visa'), ('Mastercard'), ('American Express'), ('PayPal');

-- 4. BOT ASSISTENZA

INSERT INTO BOT_ASSISTENZA
(Nome_bot)
VALUES
('Athlix Bot');

-- 5. UTENTI

INSERT INTO UTENTE
( Tipo_utente, Nome_utente, Cognome_utente, Email, Password, Data_nascita, Indirizzo_utente, Numero_telefono)
VALUES
('Admin', 'Francesco','Molisse','admin@athlix.it','pbkdf2$120000$JRlvsq/CICbrSwjhwErdOA==$pdmomKTXlgY9ERwveA5/a+poaW8yVsnN8jSQc6y3T2A=', '2005-03-18', 'Via Provinciale Nocera Sarno 107, Nocera Inferiore','3701042864'),
('Cliente','Luca','Bianchi','luca.bianchi@email.it','pbkdf2$120000$bAfql3bFSXov26rAiW6lgA==$STb9Iteqnv99mHyODoVD/EpOsQHigcEhSk7cLg/JOWc=','2000-03-15','Via Milano 25, Milano','3331112222'),
('Cliente','Giulia','Verdi','giulia.verdi@email.it','pbkdf2$120000$xHhheB2PsniUAJLt1sDgPw==$5eRy9ZvKvi9XKZXe/7J524lssDZIRNtNlodRYJXvAWo=','1999-07-22','Via Torino 15, Torino','3333334444'),
('Cliente','Marco','Neri','marco.neri@email.it','pbkdf2$120000$uuWMOUOO33Zop/j6uy82UA==$gYl27XYHs2LYgHsEISJQe5bC60NIbIGcUbgY67wsuRM=','2001-11-05','Via Napoli 8, Napoli','3335556666'),
('Cliente','Alessandro','Romano','alessandro.romano@email.it','pbkdf2$120000$88YhBqB4Y4ERP+QHJBAaJA==$vBDayzcMwb3qscAC6CQ8qPiEePXUKE6RNR2xBMkIkPE=','1998-02-18','Via Firenze 20, Firenze','3337778888'),
('Cliente','Francesca','Conti','francesca.conti@email.it','pbkdf2$120000$uqwg/k70Kyjyyll2Cbgx8w==$LV5YLK/aIUO56czc6EYs5tSiJwkl6YAMj0y9Odt1HXA=','2002-09-12','Via Venezia 12, Venezia','3339990000');
-- 6. PRODOTTI

INSERT INTO PRODOTTO
(Nome,Prezzo,Descrizione,Immagine,Visibile,Disponibilita,Quantita,ID_categoria,ID_sconto)
VALUES
-- CALCIO
('Pallone da Calcio Pro',29.90,'Pallone da calcio professionale adatto ad allenamenti e partite.','/images/products/pallone-da-calcio-pro.svg',TRUE,TRUE,50,1,NULL),
('Guanti da Portiere Pro',59.90,'Guanti da portiere con ottima presa e protezione delle dita.','/images/products/guanti-da-portiere-pro.svg',TRUE,TRUE,20,1,NULL),

-- BASKET
('Canotta Basket Pro',39.90,'Canotta da basket traspirante e leggera.','/images/products/canotta-basket-pro.svg',TRUE,TRUE,25,2,NULL),
('Pallone Basket Official',34.90,'Pallone da basket da interno ed esterno.','/images/products/pallone-basket-official.svg',TRUE,TRUE,40,2,2),
('Scarpe Basket Air',109.90,'Scarpe da basket con ammortizzazione avanzata.','/images/products/scarpe-basket-air.svg',TRUE,TRUE,18,2,NULL),

-- RUNNING
('Scarpe Running Air',119.90,'Scarpe da running ad alte prestazioni.','/images/products/scarpe-running-air.svg',TRUE,TRUE,20,3,2),
('Maglia Running Pro',44.90,'Maglia tecnica traspirante per attività di running.','/images/products/maglia-running-pro.svg',TRUE,TRUE,10,3,NULL),
('Pantaloncini Running',34.90,'Pantaloncini leggeri e traspiranti.','/images/products/pantaloncini-running.svg',TRUE,TRUE,30,3,1),

-- FITNESS
('Tappetino Fitness',24.90,'Tappetino antiscivolo per allenamento e stretching.','/images/products/tappetino-fitness.svg',TRUE,TRUE,40,4,NULL),
('Set Manubri Fitness',79.90,'Set di manubri per allenamento domestico.','/images/products/set-manubri-fitness.svg',TRUE,TRUE,15,4,3),
('Banda Elastica',14.90,'Banda elastica per esercizi di resistenza.','/images/products/banda-elastica.svg',TRUE,TRUE,50,4,NULL),

-- TENNIS
('Racchetta Tennis Pro',149.90,'Racchetta da tennis professionale.','/images/products/racchetta-tennis-pro.svg',TRUE,TRUE,15,5,3),
('Palline Tennis Pack',12.90,'Confezione da tre palline da tennis.','/images/products/palline-tennis-pack.svg',TRUE,TRUE,60,5,NULL),
('Borsa Tennis Pro',69.90,'Borsa sportiva porta racchette.','/images/products/borsa-tennis-pro.svg',TRUE,TRUE,20,5,4),

-- NUOTO
('Occhialini da Nuoto',19.90,'Occhialini da nuoto professionali anti-appannamento.','/images/products/occhialini-da-nuoto.svg',TRUE,TRUE,35,6,NULL),
('Cuffia Nuoto Pro',9.90,'Cuffia da nuoto in silicone.','/images/products/cuffia-nuoto-pro.svg',TRUE,FALSE,0,6,NULL),
('Costume Nuoto Uomo',39.90,'Costume tecnico da nuoto.','/images/products/costume-nuoto-uomo.svg',TRUE,TRUE,25,6,1),

-- CICLISMO
('Casco Ciclismo Pro',79.90,'Casco da ciclismo leggero e resistente.','/images/products/casco-ciclismo-pro.svg',TRUE,TRUE,18,7,NULL),
('Guanti Ciclismo',24.90,'Guanti da ciclismo con imbottitura sul palmo.','/images/products/guanti-ciclismo.svg',TRUE,TRUE,30,7,NULL),
('Borraccia Sport',12.90,'Borraccia sportiva da 750 ml.','/images/products/borraccia-sport.svg',TRUE,TRUE,45,7,2),

-- PALLAVOLO
('Pallone Pallavolo Pro',34.90,'Pallone da pallavolo professionale.','/images/products/pallone-pallavolo-pro.svg',TRUE,TRUE,22,8,NULL),
('Ginocchiere Pallavolo',29.90,'Ginocchiere protettive per pallavolo.','/images/products/ginocchiere-pallavolo.svg',TRUE,TRUE,25,8,NULL),
('Scarpe Volley Pro',94.90,'Scarpe professionali per pallavolo indoor.','/images/products/scarpe-volley-pro.svg',TRUE,TRUE,15,8,4);

-- 7. CARRELLI

INSERT INTO CARRELLO
(Data_creazione,Stato,ID_utente)
VALUES
('2026-09-01','ATTIVO',2),
('2026-09-02','ATTIVO',3),
('2026-09-03','ATTIVO',4),
('2026-09-04','ATTIVO',5),
('2026-09-05','ATTIVO',6);

-- 8. PRODOTTI NEI CARRELLI

INSERT INTO INCLUDERE
(ID_carrello,ID_prodotto,Quantita_ordine)
VALUES
(1, 1, 2), (1, 2, 1), (1, 10, 1),
(2, 7, 1),(2, 11, 2),
(3, 13, 1),(3, 14, 2),
(4, 19, 1),(4, 20, 1),
(5, 22, 2),(5, 23, 1);

-- 9. PAGAMENTI

INSERT INTO PAGAMENTO
(Stato,Data_pagamento,ID_metodo)
VALUES
('COMPLETATO','2026-09-04',1),
('COMPLETATO','2026-09-05',2),
('IN ATTESA','2026-09-06',4),
('COMPLETATO','2026-09-06',1);

-- 10. ORDINI
-- il totale verrà calcolato automaticamente dopo
INSERT INTO ORDINE
(Data_ordine,Stato_o,Totale,ID_utente,ID_pagamento)
VALUES
('2026-09-04','CONSEGNATO',0.00,2,1),
('2026-09-05','SPEDITO',0.00,3,2),
('2026-09-06','IN LAVORAZIONE',0.00,4,3),
('2026-09-06','CONSEGNATO',0.00,5,4);

-- 11. PRODOTTI NEGLI ORDINI

INSERT INTO CONTENERE
(ID_ordine,ID_prodotto,Quantita_ordine,Prezzo_acquisto,IVA_acquisto)
SELECT
    r.ID_ordine, r.ID_prodotto, r.Quantita_ordine,

    -- Prezzo effettivamente pagato
    ROUND(p.Prezzo *(1 - COALESCE(s.Percentuale, 0) / 100), 2)
        AS Prezzo_acquisto,

    -- IVA del prodotto al momento dell'ordine
    p.IVA AS IVA_acquisto

FROM
(
    -- ORDINE 1
    SELECT 1 AS ID_ordine, 1 AS ID_prodotto, 2 AS Quantita_ordine

    UNION ALL
    SELECT 1, 2, 1

    UNION ALL
    SELECT 1, 10, 1

    -- ORDINE 2
    UNION ALL
    SELECT 2, 7, 1

    UNION ALL
    SELECT 2, 11, 2

    -- ORDINE 3
    UNION ALL
    SELECT 3, 13, 1

    -- ORDINE 4
    UNION ALL
    SELECT 4, 19, 3

    UNION ALL
    SELECT 4, 20, 2

) AS r

INNER JOIN PRODOTTO p
    ON p.ID_prodotto = r.ID_prodotto

INNER JOIN ORDINE o
    ON o.ID_ordine = r.ID_ordine

LEFT JOIN SCONTO s
    ON s.ID_sconto = p.ID_sconto AND o.Data_ordine
    BETWEEN s.Data_inizio AND s.Data_fine;

-- CALCOLO AUTOMATICO DEL TOTALE DEGLI ORDINI, Totale = SOMMA(Quantita_ordine * Prezzo_acquisto)
SET SQL_SAFE_UPDATES = 0; -- ora posso eseguire aggiornamenti massivi senza la where
UPDATE ORDINE o
INNER JOIN
(
    SELECT ID_ordine,
        ROUND( SUM(Quantita_ordine * Prezzo_acquisto), 2 ) AS Totale_calcolato
	FROM CONTENERE
    GROUP BY ID_ordine
) AS t
    ON t.ID_ordine = o.ID_ordine

SET o.Totale = t.Totale_calcolato;

SET SQL_SAFE_UPDATES = 1; -- riporto tutto alla norma
-- 12. SPEDIZIONI

INSERT INTO SPEDIZIONE
(Indirizzo,Stato_s,Data_partenza,Data_consegna,ID_ordine)
VALUES
('Via Milano 25, Milano','CONSEGNATA','2026-09-05','2026-09-07',1),
('Via Torino 15, Torino','SPEDITA','2026-09-06',NULL,2),
('Via Napoli 8, Napoli','IN PREPARAZIONE',NULL,NULL,3),
('Via Firenze 20, Firenze','CONSEGNATA','2026-09-07','2026-09-09',4);

-- 13. RECENSIONI

INSERT INTO RECENSIONE
(Data_recensione,Voto,Commento,ID_utente,ID_prodotto)
VALUES
('2026-09-06',5,'Ottimo prodotto, molto resistente.',2,1),
('2026-09-06',4,'Maglia tecnica comoda e traspirante.',3,7),
('2026-09-07',5,'Palline di ottima qualità, le consiglio.',4,13),
('2026-09-08',4,'Guanti comodi e ben imbottiti.',5,19);

-- 14. CHAT

INSERT INTO CHAT
(Data_inizio,Stato_c,ID_utente,ID_bot)
VALUES
('2026-09-04','Chiusa',2,1),
('2026-09-05','Chiusa',3,1),
('2026-09-06','Aperta',4,1),
('2026-09-07','Chiusa',5,1);

-- 15. METODI DI PAGAMENTO DEGLI UTENTI

INSERT INTO INSERIRE
(ID_utente,ID_metodo,Ultime_quattro,Intestatario,Scadenza)
VALUES
(2,1,'1111','Luca Bianchi','12/28'),
(3,2,'4444','Giulia Verdi','06/29'),
(4,1,'0002','Marco Neri','10/27'),
(5,4,NULL,'Alessandro Romano',NULL);

COMMIT; /* Conferma le modifiche e termina la transazione. */

-- FINE SCRIPT
