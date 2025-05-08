package com.company.crs.bean;

import com.company.crs.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Session scoped bean to handle authentication and maintain user session
 */
@Named
@SessionScoped
public class AuthenticationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthenticationBean.class.getName());
    
    // API endpoint for authentication
    private static final String AUTH_API_URL = "http://localhost:8080/api/auth/login"; // Replace with actual API URL
    
    private String username;
    private String password;
    private boolean rememberMe;
    private User currentUser;

    /**
     * Attempts to authenticate the user with the provided credentials
     * @return navigation outcome
     */
    public String login() {
        try {
            String jwtToken = callAuthApi();
            
            if (jwtToken != null && !jwtToken.isEmpty()) {
                // Create user and store in session
                currentUser = new User(username, jwtToken);
                
                // Add default role - in a real app, roles would come from the API
                currentUser.addRole("USER");
                
                // Clear password from memory
                password = null;
                
                addMessage(FacesMessage.SEVERITY_INFO, "Login Successful", "Welcome " + username);
                return "index?faces-redirect=true";
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid credentials");
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            addMessage(FacesMessage.SEVERITY_ERROR, "Login Error", "An error occurred during login: " + e.getMessage());
            return null;
        }
    }

    /**
     * Logs out the current user and invalidates the session
     * @return navigation outcome
     */
    public String logout() {
        try {
            // Clear user data
            currentUser = null;
            username = null;
            password = null;
            
            // Invalidate session
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.invalidateSession();
            
            return "login?faces-redirect=true";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
            return "login?faces-redirect=true";
        }
    }

    /**
     * Calls the authentication API to validate credentials and get JWT token
     * @return JWT token if authentication successful, null otherwise
     */
    private String callAuthApi() {
        HttpURLConnection connection = null;
        try {
            // Create connection
            URL url = new URL(AUTH_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            // Create JSON payload
            String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", 
                    username, password);
            
            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Parse response to get token
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(connection.getInputStream());
                
                // Extract token from response - adjust path based on actual API response structure
                if (rootNode.has("token")) {
                    return rootNode.get("token").asText();
                } else if (rootNode.has("access_token")) {
                    return rootNode.get("access_token").asText();
                } else {
                    LOGGER.warning("Token not found in API response");
                    return null;
                }
            } else {
                LOGGER.warning("Authentication failed with response code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error calling authentication API", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Helper method to add faces messages
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(severity, summary, detail));
    }
    
    /**
     * Checks if user is logged in
     * @return true if user is authenticated
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Gets JWT token for API calls
     * @return JWT token or null if not authenticated
     */
    public String getJwtToken() {
        return currentUser != null ? currentUser.getJwtToken() : null;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
