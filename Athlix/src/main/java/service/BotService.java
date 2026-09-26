package service;

import dao.OrderDAO;
import model.BotReply;
import model.OrdineBean;
import model.UtenteBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/* RUOLO DELLA CLASSE: Servizio applicativo: contiene logica riutilizzabile indipendente dalla presentazione JSP. */
public class BotService {

    private final OrderDAO orderDAO =
            new OrderDAO();

    /* Analizza il messaggio dell’utente e costruisce la risposta del bot. */

    public BotReply reply(
            String message,
            UtenteBean utente) throws SQLException {

        String text =
                normalize(message);

        if (text.isBlank()) {
            return new BotReply(
                    "Scrivi una domanda su ordini, spedizioni, prodotti, account, pagamenti, carrello o recensioni."
            );
        }

        if (containsAny(
                text,
                "ciao",
                "salve",
                "buongiorno",
                "buonasera",
                "hey")) {

            String nome =
                    utente != null
                            ? " " + utente.getNomeUtente()
                            : "";

            return new BotReply(
                    "Ciao" + nome
                            + "! Sono l'assistente AthliX. Posso aiutarti con ordini, spedizioni, prodotti, carrello, account, pagamenti e recensioni."
            );
        }

        if (containsAny(
                text,
                "ordine",
                "spedizione",
                "spedito",
                "consegna",
                "tracking",
                "dov'è",
                "dove e",
                "dove è")) {

            return orderReply(
                    utente
            );
        }

        if (containsAny(
                text,
                "carrello",
                "quantità",
                "quantita",
                "rimuovere prodotto",
                "aggiungere prodotto")) {

            return new BotReply(
                    "Nel carrello puoi aumentare o diminuire le quantità, rimuovere i prodotti e poi procedere al checkout. La disponibilità viene controllata anche lato server.",
                    "Apri il carrello",
                    "/cart"
            );
        }

        if (containsAny(
                text,
                "catalogo",
                "prodotto",
                "prodotti",
                "ricerca",
                "cercare",
                "categoria",
                "prezzo",
                "sconto",
                "promozione")) {

            return new BotReply(
                    "Nel catalogo puoi cercare i prodotti per nome, filtrarli per categoria, disponibilità e prezzo e ordinarli. Gli sconti attivi vengono applicati automaticamente al prezzo mostrato.",
                    "Vai al catalogo",
                    "/catalog"
            );
        }

        if (containsAny(
                text,
                "password",
                "profilo",
                "account",
                "email",
                "telefono",
                "indirizzo",
                "dati personali")) {
            if (utente == null) {
                return new BotReply(
                        "Per gestire il profilo o cambiare la password devi prima accedere al tuo account.",
                        "Accedi",
                        "/login?redirect=account"
                );
            }

            return new BotReply(
                    "Dall'area personale puoi modificare nome, cognome, email, data di nascita, indirizzo e telefono. Il cambio password è disponibile in una pagina separata.",
                    "Apri l'area personale",
                    "/account"
            );
        }

        if (containsAny(
                text,
                "pagamento",
                "pagare",
                "carta",
                "paypal",
                "checkout")) {

            return new BotReply(
                    "Il metodo di pagamento viene scelto durante il checkout. Il pagamento e l'ordine vengono registrati lato server e il carrello viene svuotato solo al completamento dell'ordine.",
                    "Vai al checkout",
                    "/checkout"
            );
        }

        if (containsAny(
                text,
                "recensione",
                "recensioni",
                "voto",
                "valutazione")) {

            return new BotReply(
                    "Le recensioni sono visibili nella pagina del prodotto. Un cliente autenticato può recensire un prodotto che ha acquistato e può successivamente aggiornare la propria recensione.",
                    "Vai al catalogo",
                    "/catalog"
            );
        }

        if (containsAny(
                text,
                "reso",
                "restituzione",
                "rimborso")) {

            return new BotReply(
                    "AthliX non ha ancora una procedura automatica di reso nel portale. Per il progetto, questa richiesta viene gestita come assistenza informativa; conserva il numero dell'ordine per identificare rapidamente l'acquisto.",
                    utente == null
                            ? "Accedi"
                            : "Vedi i miei ordini",
                    utente == null
                            ? "/login?redirect=account"
                            : "/orders"
            );
        }

        if (containsAny(
                text,
                "admin",
                "amministratore",
                "pannello")) {

            if (utente != null && utente.isAdmin()) {
                return new BotReply(
                        "Dal pannello admin puoi gestire prodotti, ordini, sconti e promozioni e recensioni.",
                        "Apri il pannello admin",
                        "/admin"
                );
            }

            return new BotReply(
                    "Il pannello amministrativo è riservato agli utenti con ruolo Admin."
            );
        }

        if (containsAny(
                text,
                "aiuto",
                "cosa puoi fare",
                "funzioni",
                "assistenza")) {

            return new BotReply(
                    "Puoi chiedermi informazioni su: stato degli ordini e spedizioni, catalogo e ricerca prodotti, carrello, checkout e pagamenti, area personale, cambio password, sconti e recensioni."
            );
        }

        return new BotReply(
                "Non ho riconosciuto con precisione la richiesta. Prova a chiedermi, ad esempio: «Dov'è il mio ordine?», «Come modifico la password?», «Come funziona il carrello?» oppure «Come lascio una recensione?»."
        );
    }

    /* Costruisce una risposta del bot relativa agli ordini dell’utente. */

    private BotReply orderReply(
            UtenteBean utente) throws SQLException {

        if (utente == null) {
            return new BotReply(
                    "Per controllare lo stato reale dei tuoi ordini devi prima accedere.",
                    "Accedi",
                    "/login?redirect=account"
            );
        }

        if (utente.isAdmin()) {
            return new BotReply(
                    "Gli ordini dei clienti sono disponibili nel pannello amministratore.",
                    "Gestisci ordini",
                    "/admin/orders"
            );
        }

        List<OrdineBean> ordini =
                orderDAO.findByUserId(
                        utente.getIdUtente()
                );

        if (ordini.isEmpty()) {
            return new BotReply(
                    "Non risultano ancora ordini associati al tuo account.",
                    "Vai al catalogo",
                    "/catalog"
            );
        }

        OrdineBean latest =
                ordini.get(0);

        OrdineBean detail =
                orderDAO.findByIdAndUserId(
                        latest.getIdOrdine(),
                        utente.getIdUtente()
                );

        if (detail == null) {
            return new BotReply(
                    "Ho trovato un ordine, ma non riesco a recuperarne il dettaglio in questo momento.",
                    "Vedi i miei ordini",
                    "/orders"
            );
        }

        String statoSpedizione =
                detail.getStatoSpedizione() == null
                        ? "non ancora disponibile"
                        : detail.getStatoSpedizione();

        StringBuilder response =
                new StringBuilder();

        response.append("Il tuo ordine più recente è il #")
                .append(detail.getIdOrdine())
                .append(". Stato ordine: ")
                .append(detail.getStato())
                .append(". Stato spedizione: ")
                .append(statoSpedizione)
                .append(".");

        SimpleDateFormat formatter =
                new SimpleDateFormat("dd/MM/yyyy");

        if (detail.getDataPartenza() != null) {
            response.append(" Partenza: ")
                    .append(
                            formatter.format(
                                    detail.getDataPartenza()
                            )
                    )
                    .append(".");
        }

        if (detail.getDataConsegna() != null) {
            response.append(" Consegna: ")
                    .append(
                            formatter.format(
                                    detail.getDataConsegna()
                            )
                    )
                    .append(".");
        }

        return new BotReply(
                response.toString(),
                "Apri il dettaglio ordine",
                "/order-detail?id="
                        + detail.getIdOrdine()
        );
    }

    /* Normalizza un testo per rendere più semplice il confronto delle intenzioni. */

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim()
                       .toLowerCase(Locale.ROOT);
    }

    /* Controlla se il testo contiene almeno una delle parole chiave indicate. */

    private boolean containsAny(
            String text,
            String... words) {

        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
