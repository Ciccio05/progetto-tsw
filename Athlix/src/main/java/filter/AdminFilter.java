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
@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AdminFilter implements Filter {

    /* Controlla l’accesso alla risorsa prima di proseguire nella catena dei filtri. */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        UtenteBean utente = session == null
                ? null
                : (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login?redirect=admin"
            );
            return;
        }

        if (!utente.isAdmin()) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
