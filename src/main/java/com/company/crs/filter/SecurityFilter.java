package com.company.crs.filter;

import com.company.crs.bean.AuthenticationBean;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Security filter to protect secured pages
 * Redirects unauthenticated users to the login page
 */
@WebFilter(urlPatterns = {"*.xhtml"})
public class SecurityFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(SecurityFilter.class.getName());
    
    @Inject
    private AuthenticationBean authBean;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Check if the requested page is in the secured area or requires authentication
        boolean isSecuredPage = requestURI.contains("/secured/");
        boolean isLoginPage = requestURI.contains("/login.xhtml");
        boolean isResourceRequest = requestURI.contains("/javax.faces.resource/") || 
                                   requestURI.contains("/resources/");
        
        // Allow access to login page and resources without authentication
        if (isLoginPage || isResourceRequest) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is authenticated
        boolean isLoggedIn = false;
        
        // If CDI injection fails (can happen in some containers), try to get from session
        if (authBean == null) {
            Object sessionAuth = httpRequest.getSession().getAttribute("authenticationBean");
            isLoggedIn = sessionAuth != null && ((AuthenticationBean) sessionAuth).isLoggedIn();
        } else {
            isLoggedIn = authBean.isLoggedIn();
        }
        
        // If accessing secured page without authentication, redirect to login
        if (isSecuredPage && !isLoggedIn) {
            LOGGER.info("Unauthorized access attempt to " + requestURI);
            httpResponse.sendRedirect(contextPath + "/login.xhtml");
        } 
        // If authenticated user tries to access login page, redirect to index
        else if (isLoginPage && isLoggedIn) {
            httpResponse.sendRedirect(contextPath + "/index.xhtml");
        }
        // Otherwise, continue with the request
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
