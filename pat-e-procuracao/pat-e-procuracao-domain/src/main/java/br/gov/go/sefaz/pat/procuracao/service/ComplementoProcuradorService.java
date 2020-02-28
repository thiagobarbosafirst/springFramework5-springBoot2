package br.gov.go.sefaz.pat.procuracao.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.corporativo.model.EmailPessoa;
import br.gov.go.sefaz.javaee.corporativo.model.TelefonePessoa;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;

@Service
public class ComplementoProcuradorService {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TelefoneService telefoneService;
	
	@Autowired
	private DocumentoService documentoService;
	
	public void prepararComplementoProcurador(List<List> listaComplementoPessoa) {
		List<EmailPessoa> listaEmailPessoa = listaComplementoPessoa.get(0);
		List<TelefonePessoa> listaTelefone = listaComplementoPessoa.get(1);
		List<DocumentoAdvogado> listaDocumento = listaComplementoPessoa.get(2);

		for (EmailPessoa emailPessoa : listaEmailPessoa) {
			
			EmailPessoa emailPessoaConsulta = emailService.consultarPorPessoaTipoEmail(emailPessoa.getIdPessoa(), emailPessoa.getTipoEmail());
			if(emailPessoaConsulta != null) {
				emailPessoa.setId(emailPessoaConsulta.getId());
			}			
			
		}

		for (TelefonePessoa telefonePessoa : listaTelefone) {
			
			TelefonePessoa telefonePessoaConsulta = telefoneService.consultarPorPessoaTipoTelefone(telefonePessoa.getIdPessoa(), telefonePessoa.getTipoTelefone());
			if(telefonePessoaConsulta != null) {
				telefonePessoa.setId(telefonePessoaConsulta.getId());
			}
		}
		
		for (DocumentoAdvogado documentoPessoa : listaDocumento) {
			
			DocumentoAdvogado documentoPessoaConsulta = documentoService.consultar(documentoPessoa.getDocumentoPessoaId());
			if(documentoPessoaConsulta != null) {
				documentoPessoa.setIdDocumentoPessoa(documentoPessoaConsulta.getIdDocumentoPessoa());
				documentoPessoa.setDocumentoPessoaId(documentoPessoaConsulta.getDocumentoPessoaId());
			}
			
		}

	}
	
	@HistoryJpa
	@Transactional
	public void saveComplementoProcurador(String matriculaUsuarioGestor, List<List> listaComplementoPessoa) throws SefazException{ 
		
		List<EmailPessoa> listaEmailPessoa = listaComplementoPessoa.get(0);
		List<TelefonePessoa> listaTelefone = listaComplementoPessoa.get(1);
		List<DocumentoAdvogado> listaDocumento = listaComplementoPessoa.get(2);
		
		for (EmailPessoa emailPessoa : listaEmailPessoa) {
			emailService.save(matriculaUsuarioGestor, emailPessoa);
		}

		for (TelefonePessoa telefonePessoa : listaTelefone) {
			telefoneService.save(matriculaUsuarioGestor, telefonePessoa);
		}
		
		for (DocumentoAdvogado documentoPessoa : listaDocumento) {
			documentoService.save(matriculaUsuarioGestor, documentoPessoa); 
		}		
		
	}
	
}
 