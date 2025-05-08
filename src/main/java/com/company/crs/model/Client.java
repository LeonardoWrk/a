package com.company.crs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class for Client entity
 */
public class Client implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String email;
    private String logoPath;
    private List<Address> addresses = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public Client() {
    }
    
    /**
     * Constructor with fields
     * 
     * @param id The client ID
     * @param name The client name
     * @param email The client email
     */
    public Client(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Gets the client ID
     * 
     * @return The client ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the client ID
     * 
     * @param id The client ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the client name
     * 
     * @return The client name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the client name
     * 
     * @param name The client name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the client email
     * 
     * @return The client email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the client email
     * 
     * @param email The client email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the logo path
     * 
     * @return The logo path
     */
    public String getLogoPath() {
        return logoPath;
    }

    /**
     * Sets the logo path
     * 
     * @param logoPath The logo path to set
     */
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    /**
     * Gets the client addresses
     * 
     * @return The list of addresses
     */
    public List<Address> getAddresses() {
        return addresses;
    }

    /**
     * Sets the client addresses
     * 
     * @param addresses The list of addresses to set
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    
    /**
     * Adds an address to the client
     * 
     * @param address The address to add
     */
    public void addAddress(Address address) {
        this.addresses.add(address);
    }
    
    /**
     * Removes an address from the client
     * 
     * @param address The address to remove
     * @return True if the address was removed, false otherwise
     */
    public boolean removeAddress(Address address) {
        return this.addresses.remove(address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Client other = (Client) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Client [id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
