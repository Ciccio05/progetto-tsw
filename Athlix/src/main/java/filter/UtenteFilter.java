package filter;

import model.UtenteBean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/* RUOLO DELLA CLASSE: Filtro servlet: applica controlli trasversali prima dell’accesso alle risorse protette. */
@WebFilter(urlPatterns = {
        "/account",
        "/account/*",
        "/cart",
        "/checkout",
        "/orders",
        "/order-detail",
        "/order-print"
})
public class UtenteFilter implements Filter {

    /* Controlla l’accesso alla risorsa prima di proseguire nella catena dei filtri. */
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        UtenteBean utente = session == null
                ? null
                : (UtenteBean) session.getAttribute("utente");

        String path = httpRequest.getServletPath();

        if (utente == null) {
            String redirect;

            if ("/cart".equals(path)) {
                redirect = "cart";
            } else if ("/checkout".equals(path)) {
                redirect = "checkout";
            } else if ("/orders".equals(path)
                    || "/order-detail".equals(path)
                    || "/order-print".equals(path)) {
                redirect = "orders";
            } else {
                redirect = "account";
            }

            httpResponse.sendRedirect(
                    httpRequest.getContextPath()
                            + "/login?redirect="
                            + redirect
            );
            return;
        }

        /*
         * L'admin può gestire il proprio profilo (/account e /account/*),
         * ma non può acquistare né consultare le rotte ordini del cliente.
         */
        boolean accountPath =
                "/account".equals(path)
                        || path.startsWith("/account/");

        if (utente.isAdmin() && !accountPath) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
