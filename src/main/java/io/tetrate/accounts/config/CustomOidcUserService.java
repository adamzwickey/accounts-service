package io.tetrate.web.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


public class CustomOidcUserService extends OidcUserService {

    Logger LOG = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Value("${tetrate.clientId}")
    private String _clientId;
    
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        LOG.debug("OIDC User Request: {}", userRequest);

        Map<String,Object> claims = new HashMap();
        claims.put("accessToken", userRequest.getAccessToken().getTokenValue());
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        Set<GrantedAuthority> authorities = mapAuthorities(userRequest.getIdToken().getClaimAsMap("resource_access"));
        if (CollectionUtils.isEmpty(authorities)) {
            authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_TETRATE_TRADER"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ACCOUNT"));
            authorities.add(new SimpleGrantedAuthority("ROLE_TRADE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_PORTFOLIO"));
        }
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OidcUser user;
        if (StringUtils.hasText(userNameAttributeName)) {
            user = new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        } else {
            user = new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
        }

        return user;
    }

    private Set<GrantedAuthority> mapAuthorities(Map<?,?> roles) {
        LOG.debug("Roles: {}", roles);
        // Assert.notNull(roles, "Client Role Mapping not configured properly");
        // return ((List<String>)((Map)roles.get(_clientId)).get("roles")).stream().filter(scope -> !scope.equals("openid"))
        //         .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase().replaceAll("\\.", "_")))
        //         .collect(Collectors.toSet());
        //temp hack for self-registration
        return null;
    }
}
