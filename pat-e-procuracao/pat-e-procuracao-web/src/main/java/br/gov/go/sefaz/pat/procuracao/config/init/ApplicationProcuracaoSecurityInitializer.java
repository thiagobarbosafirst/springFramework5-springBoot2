package br.gov.go.sefaz.pat.procuracao.config.init;

import java.util.EnumSet;

import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * <b>Secretaria de Estado da Fazenda de Goiás</b></br>
 * Supervisão de Arquitetura e Prospecção Tecnológica</br>
 * <i>{Project Name}</i></br></br>
 * 
 * {Artifact description}.
 * 
 * @version 
 * <ul>
 * <li>$LastChangedRevision$</li>
 * <li>$LastChangedBy$</li>
 * <li>$LastChangedDate$</li>
 * </ul>
 * 
 * @author Thiago-SC and Renato-RS
 * 
 */
public class ApplicationProcuracaoSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		
		//avoid url JSESSION id URL rewritten, and save cookies
		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
		
		Dynamic encodingFilter = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
		encodingFilter.setInitParameter("encoding","UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(null, false, "/*");
	}
}