package br.gov.go.sefaz.pat.procuracao.rest.client.config;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.resource.jwt.v1.JwtCredentials;
import br.gov.go.sefaz.javaee.commons.resource.jwt.v1.JwtDetails;
import br.gov.go.sefaz.javaee.commons.resource.jwt.v1.JwtPrincipal;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.security.jwt.support.RSALoaderSupport;
import br.gov.go.sefaz.javaee.service.rest.client.config.RestClientServiceRestTemplateConfiguration;
import br.gov.go.sefaz.javaee.service.rest.client.jwt.factory.JwtAuthorizationTokenClientFactory;
import br.gov.go.sefaz.javaee.service.rest.client.jwt.factory.JwtAuthorizationTokenClientFactoryConfiguration;
import br.gov.go.sefaz.javaee.service.rest.factory.RestObjectMapperFactory;
import br.gov.go.sefaz.pat.procuracao.rest.client.mixin.PessoaMixIn;
import br.gov.go.sefaz.pat.procuracao.support.PatProcuracaoPropertiesSupport;

@Configuration
public class PatProcuracaoRestTemplateConfiguration extends RestClientServiceRestTemplateConfiguration {

	@Autowired
	private PatProcuracaoPropertiesSupport patProcuracaoPropertiesSupport;
	
	public PatProcuracaoRestTemplateConfiguration() {
		setUseTrustedSSLClientHttpRequestFactory(true);
	}
	
	@Bean
	public JwtAuthorizationTokenClientFactory restClientServiceJwtAuthorizationTokenClientFactory() throws SefazException {
		return new JwtAuthorizationTokenClientFactory(patClientFactoryConfiguration());
	}
	
	private JwtAuthorizationTokenClientFactoryConfiguration patClientFactoryConfiguration() throws SefazException {
		String username = this.patProcuracaoPropertiesSupport.getProperty("procuracao.client.username");
		String password = this.patProcuracaoPropertiesSupport.getProperty("procuracao.client.password");
		String resourceApp = this.patProcuracaoPropertiesSupport.getProperty("procuracao.client.resource.application.name");
		String servicePublicKeyPath = this.patProcuracaoPropertiesSupport.getProperty("procuracao.client.resource.application.public.key");
		String jwtAuthorizationTokenUrl = this.patProcuracaoPropertiesSupport.getProperty("procuracao.client.resource.application.token.url");
		
		JwtPrincipal jwtPrincipal = JwtPrincipal.build()
				.username(username)
				.credentials(JwtCredentials.build()
					.password(password))
				.details(JwtDetails.build()
					.resourceApp(resourceApp));
		
		RSAPublicKey publicKey = RSALoaderSupport.loadPublicKey(servicePublicKeyPath);
		return new JwtAuthorizationTokenClientFactoryConfiguration(jwtPrincipal,restClientServiceHttpMessageConverters(),publicKey,jwtAuthorizationTokenUrl);
	}
	
	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper mapper = RestObjectMapperFactory.createObjectMapper();
		mapper.addMixIn(Pessoa.class, PessoaMixIn.class);
		return mapper;
	}
	
	@Override
	public List<HttpMessageConverter<?>> restClientServiceHttpMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setObjectMapper(objectMapper());
		messageConverters.add(messageConverter);
		return messageConverters;
	}
}
