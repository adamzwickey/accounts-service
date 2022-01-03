package io.tetrate.accounts.service;

import java.util.Arrays;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.tetrate.accounts.domain.RegistrationRequest;
import io.tetrate.accounts.domain.User;

@Service
public class KeyCloakService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    @Value("${tetrate.oidc.url}")
	private String _url;

    @Bean
    RestTemplate restTemplate(){
      return new RestTemplate();
    }

    public User registerUser(RegistrationRequest req) {

        Keycloak kc = KeycloakBuilder.builder() 
            .serverUrl(_url + "/auth")
            .realm("master")
            .username("admin") 
            .password("t3trat3!") 
            .clientId("admin-cli") 
            .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()) 
            .build();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(req.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.getEmail());
        user.setFirstName(req.getGivenNames());
        user.setLastName(req.getSurname());
        user.setEmail(req.getEmail());
        user.setCredentials(Arrays.asList(credential));
        user.setEnabled(true);
        kc.realm("tetrate").users().create(user);

        User u = new User();
        u.setEmail(req.getEmail());
        u.setGivenNames(req.getGivenNames());
        u.setId(req.getEmail());
        u.setSurname(req.getSurname());
        return u;
    }
}
