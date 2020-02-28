package br.gov.go.sefaz.pat.procuracao.config.init;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import br.gov.go.sefaz.javaee.security.x509.portalsefaz.config.init.X509PortalSefazSecurityInitializer;
import br.gov.go.sefaz.javaee.service.rest.client.config.init.RestClientServiceInitializer;
import br.gov.go.sefaz.pat.documento.config.init.DocumentoDomainInitializer;
import br.gov.go.sefaz.pat.procuracao.config.PatProcuracaoWebMvcConfiguration;
import br.gov.go.sefaz.pat.procuracao.config.WebProcuracaoX509PortalSefazSecurityConfiguration;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoConverter;

/**
 * Classe responsável por iniciar as configurações da aplicação
 */
public class ApplicationProcuracaoInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		/*** Carrega classes de configuração comuns a todos os tipos de implementações ***/
		
		Class<?>[] rootConfigClasses = new Class<?>[]{};
				
		rootConfigClasses = DocumentoDomainInitializer.getRootConfigClasses();
		
		rootConfigClasses = ArrayUtils.addAll(rootConfigClasses, DomainProcuracaoInitializer.getRootConfigClasses());
		
		rootConfigClasses = ArrayUtils.add(rootConfigClasses, ProcuracaoConverter.class);
		
		// Configurações de segurança da aplicação
		 
		
		rootConfigClasses = ArrayUtils.add(rootConfigClasses, WebProcuracaoX509PortalSefazSecurityConfiguration.class);
		
		rootConfigClasses = ArrayUtils.addAll(rootConfigClasses, RestClientServiceInitializer.getRootConfigClasses());
		
		// Configurações de segurança corporativas
		rootConfigClasses = ArrayUtils.addAll(rootConfigClasses, X509PortalSefazSecurityInitializer.getRootConfigClasses());
		
		return rootConfigClasses;
	}

    /**
     * Retorna a classe que instrui o Dispatcher a
     * localizar os Controllers.
     * @return
     */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{PatProcuracaoWebMvcConfiguration.class};
	}

    /**
     * Método responsável por definir o padrão de URL
     * que será delegado para o DispatcherServlet. "/"
     * significa que tudo após a / será entregue ao Dispatcher
     * (qualquer URL dentro da aplicação).
     *
     * <servlet-mapping>
     *	 <url-pattern>/</url-pattern>
     * </servlet-mapping>
     * @return
     */
	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
	
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    	//Spring Request Context Listener
    	servletContext.addListener(new RequestContextListener());
    	super.onStartup(servletContext);
    }
}