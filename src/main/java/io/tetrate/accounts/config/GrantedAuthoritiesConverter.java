package io.tetrate.accounts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GrantedAuthoritiesConverter extends JwtAuthenticationConverter {

    private static final Logger LOG = LoggerFactory.getLogger(GrantedAuthoritiesConverter.class);

    @Value("${tetrate.oidc.clientId}")
    private String _clientId;

    @Override
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        LOG.debug("JWT: {}", jwt);        
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_TETRATE_TRADER"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ACCOUNT"));
            authorities.add(new SimpleGrantedAuthority("ROLE_TRADE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_PORTFOLIO"));
        return authorities;            
        // Map<String,Object> m = jwt.getClaimAsMap("resource_access");
        // LOG.debug("resource_access: {}", m);
        // Assert.notNull(m.get(_clientId), "Client Role Mapping not configured properly");
        // return ((Collection<String>)((Map)m.get(_clientId)).get("roles")).stream().filter(scope -> !scope.equals("openid"))
        //         .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase().replaceAll("\\.", "_")))
        //         .collect(Collectors.toSet());
        // Collection<String> scopes = (Collection<String>) jwt.getClaimAsStringList("scope");
        // return scopes.stream().filter(scope -> !scope.equals("openid"))
        //         .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase().replaceAll("\\.", "_")))
        //         .collect(Collectors.toSet());
    }
}