package edu.eci.cvds.prometeo.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("🔍 Request URI: " + request.getRequestURI());
        System.out.println("🔍 Method: " + request.getMethod());
        System.out.println("🔍 All Attributes: ");
        request.getAttributeNames().asIterator().forEachRemaining(attr ->
            System.out.println(attr + " = " + request.getAttribute(attr))
        );
        filterChain.doFilter(request, response);
    }
}