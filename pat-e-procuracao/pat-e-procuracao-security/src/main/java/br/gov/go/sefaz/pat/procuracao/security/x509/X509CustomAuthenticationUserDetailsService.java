package br.gov.go.sefaz.pat.procuracao.security.x509;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import br.gov.go.sefaz.javaee.security.x509.X509AuthenticationUserDetailsService;

public class X509CustomAuthenticationUserDetailsService extends X509AuthenticationUserDetailsService {
	
	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
		return super.loadUserDetails(token);
		
	}
}