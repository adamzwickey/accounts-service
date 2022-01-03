package io.tetrate.accounts.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Represents the account.
 *
 * Entity object that represents a user account.
 *
 * @author Adam Zwickey
 *
 */
public class User implements Serializable {

    private String id;
    private String email;
    private String givenNames;
    private String surname;
    private DateTime createdDate;
    private String jwt;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getGivenNames() {
        return givenNames;
    }
    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public DateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }
    public String getJwt() {
        return jwt;
    }
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    
}