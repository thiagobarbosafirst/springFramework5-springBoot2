package br.gov.go.sefaz.pat.procuracao.config;

import java.util.Arrays;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.gov.go.sefaz.javaee.security.x509.portalsefaz.X509PortalSefazUserDetailsService;
import br.gov.go.sefaz.javaee.security.x509.portalsefaz.config.X509PortalSefazSecurityConfiguration;
import br.gov.go.sefaz.pat.filter.PatCredenciamentoFilter;
import br.gov.go.sefaz.pat.security.x509.X509CustomAuthenticationUserDetailsService;

@EnableWebSecurity
public class WebProcuracaoX509PortalSefazSecurityConfiguration extends X509PortalSefazSecurityConfiguration {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.addFilterBefore(X509AuthenticationBigIpFilter(), X509AuthenticationFilter.class)
			.addFilterAt(x509AuthenticationFilter(), X509AuthenticationFilter.class)
			.addFilterAfter(new PatCredenciamentoFilter(Arrays.asList("/procuracaoPresencial/**","/substabelecimentoPresencial/**","/renunciaPresencial/**"
					,"/revogacaoPresencial/**","/procurador/**","/enderecos","/user/profile/**","/dashboard?card=gestor","/dashboard?card=servidor")), X509AuthenticationFilter.class)
				.logout()
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/auth/login")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
			.and()
				.authorizeRequests()  
					.antMatchers("/procuracao/listProcurador").hasRole("PROCURADOR")
					.antMatchers("/procuracao/**").hasRole("SUJEITO_PASSIVO")
					.antMatchers("/revogacao/**").hasRole("SUJEITO_PASSIVO")
					.antMatchers("/substabelecimento/**").hasRole("PROCURADOR")
					.antMatchers("/renuncia/**").hasRole("PROCURADOR")
					.antMatchers("/centralDocumentos/**").hasAnyRole("SUJEITO_PASSIVO", "PROCURADOR", "PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO","PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT")
					.antMatchers("/procurador/**").hasAnyRole("SUJEITO_PASSIVO", "PROCURADOR", "PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO","PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT")
					.antMatchers("/procuracaoPresencial/**").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO")
					.antMatchers("/renunciaPresencial/**").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO")
					.antMatchers("/substabelecimentoPresencial/**").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO")
					.antMatchers("/revogacaoPresencial/**").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO")
					.antMatchers("/").hasAnyRole("SUJEITO_PASSIVO", "PROCURADOR","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO","PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT")
					.antMatchers("/dashboard").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO","PROCURADOR","SUJEITO_PASSIVO")
					.antMatchers("/dashboard?card=sujeitoPassivo").hasAnyRole("SUJEITO_PASSIVO")
					.antMatchers("/dashboard?card=procurador").hasAnyRole("PROCURADOR")
					.antMatchers("/dashboard?card=servidor").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO")
					.antMatchers("/dashboard?card=gestor").hasAnyRole("PORTALSEFAZ_PERFIL_ADMINISTRADOR_PAT","PORTALSEFAZ_PERFIL_EMITIR_PROCURACAO")
					.antMatchers("/logout").permitAll()
					.antMatchers("/hystrix**").permitAll()
					.anyRequest()
					.authenticated()
			.and()
				.x509()
			.and()
				.sessionManagement()
					.sessionAuthenticationErrorUrl("/auth/logout/concurrency")
					.invalidSessionUrl("/auth/denied")
					.maximumSessions(1)
					.expiredUrl("/auth/logout/concurrency");
	}
	
	@Override
	public X509PortalSefazUserDetailsService x509PortalSefazUserDetailsService() {
		return new X509CustomAuthenticationUserDetailsService();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/resources/**", "/template/**", "/auth/**", "/errors/**", "/jasper/**");
	}
}