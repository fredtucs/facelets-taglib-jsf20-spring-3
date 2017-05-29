package org.springframework.security.taglibs.facelets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @author Dominik Dorn
 */
public class SpringSecurityELLibraryTest {

    private static final String KEY = "123456789";

    private SecurityContext context;

    private final SimpleGrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
    private final SimpleGrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");

    @org.junit.Before
    public void setUp() {

        context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(ROLE_ADMIN);
        authorities.add(ROLE_USER);

        User principal = new User("username",
                "password",
                /* enabled */ true,
                /* accountNonExpired */ true,
                /* credentialsNonExpired */ true,
                /* accountNonLocked */ true,
                authorities);
        Authentication auth = new PreAuthenticatedAuthenticationToken(principal, new Object(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_USER"));

        when(context.getAuthentication()).thenReturn(auth);
    }

    @org.junit.After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testIfAnyGranted_noMatches() {
        assertFalse(SpringSecurityELLibrary.ifAnyGranted("ROLE_VISITOR,ROLE_MODERATOR"));
    }

    @Test
    public void testIfAnyGranted_oneMatches() {
        assertTrue(SpringSecurityELLibrary.ifAnyGranted("ROLE_VISITOR,ROLE_MODERATOR,ROLE_ADMIN"));
    }


    /*
		test if ROLE_ADMIN is not granted, as it is by our test data, this should return false
     */
    @Test
    public void testIfNotGranted_roleAdmin() {
        assertFalse(SpringSecurityELLibrary.ifNotGranted("ROLE_ADMIN"));
    }

    /*
		test if ROLE_VISITOR is not granted, as this is not within our default test data, this should return true
     */
    @Test
    public void testIfNotGranted_roleVisitor() {
        assertTrue(SpringSecurityELLibrary.ifNotGranted("ROLE_VISITOR"));
    }

    @Test
    public void testIfAllGranted_notAllRolesGranted() {
        assertFalse(SpringSecurityELLibrary.ifAllGranted("ROLE_ADMIN,ROLE_USER,ROLE_MODERATOR"));
    }

    @Test
    public void testIfAllGranted_allRolesGranted() {
        assertTrue(SpringSecurityELLibrary.ifAllGranted("ROLE_ADMIN,ROLE_USER"));
    }

    @Test
    public void testIfAllGranted_noRolesSpecified() {
        assertFalse(SpringSecurityELLibrary.ifAllGranted(""));
    }

    @Test
    public void testIfAllGranted_nullGiven() {
        assertFalse(SpringSecurityELLibrary.ifAllGranted(null));
    }

    @Test
    public void testIsAuthenticated_authenticated() {
        assertTrue(SpringSecurityELLibrary.isAuthenticated());
    }

    @Test
    public void testIsAuthenticated_annonymous() {
        Authentication anonymousAuth = new AnonymousAuthenticationToken(KEY, "anonymousUser", getAnonymousAuthorities());
        when(context.getAuthentication()).thenReturn(anonymousAuth);
        assertFalse(SpringSecurityELLibrary.isAuthenticated());
    }

    @Test
    public void testIsAnonymous_anonymous() {
        Authentication anonymousAuth = new AnonymousAuthenticationToken(KEY, "anonymousUser", getAnonymousAuthorities());
        when(context.getAuthentication()).thenReturn(anonymousAuth);
        assertTrue(SpringSecurityELLibrary.isAnonymous());
    }

    @Test
    public void testIsAnonymous_authenticated() {
        assertFalse(SpringSecurityELLibrary.isAnonymous());
    }

    private List<GrantedAuthority> getAnonymousAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ANONYMOUS"));
        return authorities;
    }
}
