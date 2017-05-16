package com.capitalone.dashboard.auth.ldap;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import javax.naming.NamingException;
import java.util.Collection;

@Configuration
public class CustomUserDetailsContextMapper extends LdapUserDetailsMapper {

    private static final Logger LOGGER = Logger.getLogger(CustomUserDetailsContextMapper.class);

    @Override
    public CustomUserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection authorities) {

        LdapUserDetailsImpl ldapUserDetailsImpl = (LdapUserDetailsImpl) super.mapUserFromContext(ctx, username, authorities);
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setAccountNonExpired(ldapUserDetailsImpl.isAccountNonExpired());
        customUserDetails.setAccountNonLocked(ldapUserDetailsImpl.isAccountNonLocked());
        customUserDetails.setCredentialsNonExpired(ldapUserDetailsImpl.isCredentialsNonExpired());
        customUserDetails.setEnabled(ldapUserDetailsImpl.isEnabled());
        customUserDetails.setUsername(ldapUserDetailsImpl.getUsername());
        customUserDetails.setAuthorities(ldapUserDetailsImpl.getAuthorities());

        LOGGER.info("DN from ctx: " + ctx.getDn());
        try {
            if (ctx.getAttributes().get("givenName") != null) {
                LOGGER.info("givenName from attr: " + ctx.getAttributes().get("givenName").get());
                customUserDetails.setFirstName("" + ctx.getAttributes().get("givenName").get());
            }

            if (ctx.getAttributes().get("initials") != null) {
                LOGGER.info("initials from attr: " + ctx.getAttributes().get("initials").get());
                customUserDetails.setMiddleName("" + ctx.getAttributes().get("initials").get());
            }

            if (ctx.getAttributes().get("sn") != null) {
                LOGGER.info("sn from attr: " + ctx.getAttributes().get("sn").get());
                customUserDetails.setLastName("" + ctx.getAttributes().get("sn").get());
            }

            if (ctx.getAttributes().get("displayName") != null) {
                LOGGER.info("displayName from attr: " + ctx.getAttributes().get("displayName").get());
                customUserDetails.setDisplayName("" + ctx.getAttributes().get("displayName").get());
            }

            if (ctx.getAttributes().get("mail") != null) {
                LOGGER.info("mail from attr: " + ctx.getAttributes().get("mail").get());
                customUserDetails.setEmailAddress("" + ctx.getAttributes().get("mail").get());
            }
        } catch (NamingException e) {
            LOGGER.warn("NamingException: " + e);
        }
        LOGGER.info("Attributes size: " + ctx.getAttributes().size());

        return customUserDetails;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // default
    }
}
