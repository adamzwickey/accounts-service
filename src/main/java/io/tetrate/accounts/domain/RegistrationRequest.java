package io.tetrate.accounts.domain;

/**
 * Represents a request for User Registration
 *
 * @author Adam Zwickey
 */
public class RegistrationRequest {

    private String givenNames;
    private String surname;
    private String email;
    private String password;
    private String confirmPassword;
    
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    
}