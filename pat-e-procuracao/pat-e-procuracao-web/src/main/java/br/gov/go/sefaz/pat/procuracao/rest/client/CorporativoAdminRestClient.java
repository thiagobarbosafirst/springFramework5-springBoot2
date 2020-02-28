package br.gov.go.sefaz.pat.procuracao.rest.client;

import java.io.IOException;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.go.sefaz.javaee.commons.resource.v1.MensagemRetorno;
import br.gov.go.sefaz.javaee.corporativo.model.Empresa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.service.rest.client.jwt.interceptor.JwtAuthorizationTokenClientInterceptor;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.procuracao.support.PatProcuracaoPropertiesSupport;

@Service
public class CorporativoAdminRestClient {
	
	private Logger logger = LogManager.getLogger(CorporativoAdminRestClient.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PatProcuracaoPropertiesSupport patProcuracaoPropertiesSupport;
	
	public PessoaFisica savePessoaFisica(PessoaFisica pessoaFisica, String usuarioHistorico) {

		RequestEntity<PessoaFisica> pesRequestEntity = buildPessoaFisicaRequest(pessoaFisica, usuarioHistorico);
		
		try {
			
			ResponseEntity<PessoaFisica> response = restTemplate.exchange(pesRequestEntity, PessoaFisica.class);
			
			return response.getBody();
			
		} catch (HttpClientErrorException e) {
			
			logger.error("Erro ao consumir serviço de inclusão de pessoa física.");
			
			MensagemRetorno mensagemRetorno = extractMensagemRetorno(e.getResponseBodyAsString());
			
			throw new AjaxRequestException(mensagemRetorno.getDetalhes().toArray(new String[0]));
			
		}catch (Exception e) {
			logger.error("Erro Server..",e);
			
			throw new AjaxRequestException("Erro ao realizar requisição de inclusão de pessoa física.");
		}
		
	}
	
	public PessoaJuridica savePessoaJuridica(PessoaJuridica pessoaJuridica, String usuarioHistorico) {

		RequestEntity<PessoaJuridica> pesRequestEntity = buildPessoaJuridicaRequest(pessoaJuridica, usuarioHistorico);
		
		try {
			
			ResponseEntity<PessoaJuridica> response = restTemplate.exchange(pesRequestEntity, PessoaJuridica.class);
			
			return response.getBody();
			
		} catch (HttpClientErrorException e) {
			
			logger.error("Erro ao consumir serviço de inclusão de pessoa jurídica.");
			
			MensagemRetorno mensagemRetorno = extractMensagemRetorno(e.getResponseBodyAsString());
			
			throw new AjaxRequestException(mensagemRetorno.getDetalhes().toArray(new String[0]));
			
		}catch (Exception e) {
			logger.error("Erro Server..",e);
			
			throw new AjaxRequestException("Erro ao realizar requisição de inclusão de pessoa jurídica."); 
		}
		
	}
	
	public Empresa saveEmpresa(Empresa empresa, String usuarioHistorico) {

		RequestEntity<Empresa> pesRequestEntity = buildEmpresaRequest(empresa, usuarioHistorico);
		
		try {
			
			ResponseEntity<Empresa> response = restTemplate.exchange(pesRequestEntity, Empresa.class);
			
			return response.getBody();
			
		} catch (HttpClientErrorException e) {
			
			logger.error("Erro ao consumir serviço de inclusão de empresa.");
			
			MensagemRetorno mensagemRetorno = extractMensagemRetorno(e.getResponseBodyAsString());
			
			throw new AjaxRequestException(mensagemRetorno.getDetalhes().toArray(new String[0]));
			
		}catch (Exception e) {
			logger.error("Erro Server..",e);
			
			throw new AjaxRequestException("Erro ao realizar requisição de inclusão de empresa."); 
		}
		
	}
	
	private RequestEntity<PessoaFisica> buildPessoaFisicaRequest(PessoaFisica pessoaFisica, String usuarioHistorico){
		
		String resourceApplication =  patProcuracaoPropertiesSupport.getProperty("procuracao.client.resource.application.name");
		
//		String url = "http://wldesfazapp01.intra.goias.gov.br:10110/cad-service/v1/pessoafisica/";
		
		String url = patProcuracaoPropertiesSupport.getProperty("procuracao.client.pessoa.service.endpointUrl"); 
		
		return RequestEntity
				.post(URI.create(url))
					.header(JwtAuthorizationTokenClientInterceptor.RESOURCE_APPLICATION_HEADER,resourceApplication)
					.header(JwtAuthorizationTokenClientInterceptor.USUARIO_HISTORICO_HEADER,usuarioHistorico)
					.body(pessoaFisica);
	}
	
	private RequestEntity<PessoaJuridica> buildPessoaJuridicaRequest(PessoaJuridica pessoaJuridica, String usuarioHistorico){
		
		String resourceApplication =  patProcuracaoPropertiesSupport.getProperty("procuracao.client.resource.application.name");
		
//		String url = "http://wldesfazapp01.intra.goias.gov.br:10110/cad-service/v1/pessoajuridica/";
		String url = patProcuracaoPropertiesSupport.getProperty("procuracao.client.pessoaJuridica.service.endpointUrl"); 
		
		return RequestEntity
				.post(URI.create(url))
					.header(JwtAuthorizationTokenClientInterceptor.RESOURCE_APPLICATION_HEADER,resourceApplication)
					.header(JwtAuthorizationTokenClientInterceptor.USUARIO_HISTORICO_HEADER,usuarioHistorico)
					.body(pessoaJuridica);
	}
	
	private RequestEntity<Empresa> buildEmpresaRequest(Empresa empresa, String usuarioHistorico){
		
		String resourceApplication =  patProcuracaoPropertiesSupport.getProperty("procuracao.client.resource.application.name");
		
//		String url = "http://wldesfazapp01.intra.goias.gov.br:10110/cad-service/v1/empresa/";
		String url = patProcuracaoPropertiesSupport.getProperty("procuracao.client.empresa.service.endpointUrl"); 
		
		return RequestEntity
				.post(URI.create(url))
					.header(JwtAuthorizationTokenClientInterceptor.RESOURCE_APPLICATION_HEADER,resourceApplication)
					.header(JwtAuthorizationTokenClientInterceptor.USUARIO_HISTORICO_HEADER,usuarioHistorico)
					.body(empresa);
	}
	
	private MensagemRetorno extractMensagemRetorno(String responseBodyAsString){
		
		try {
			return objectMapper.readValue(responseBodyAsString, MensagemRetorno.class);
		} catch (IOException e) {
			logger.error("Não foi possível extrair a mensagem de retorno.",e);
		}
		
		return null;
	}
}
