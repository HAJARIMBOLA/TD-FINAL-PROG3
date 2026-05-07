package edu.hei.school.agricultural.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    // Clé API valide définie en dur dans le code
    private static final String VALID_API_KEY = "agri-secure-key";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Récupérer la clé API de l'en-tête x-api-key
        String apiKey = httpRequest.getHeader("x-api-key");

        // Vérifier si la clé API est présente
        if (apiKey == null || apiKey.isEmpty()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Bad credentials\"}");
            return;
        }

        // Vérifier si la clé API est correcte
        if (!apiKey.equals(VALID_API_KEY)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Bad credentials\"}");
            return;
        }

        // La clé est valide, continuer le traitement de la requête
        chain.doFilter(request, response);
    }
}
