package br.gov.go.sefaz.pat.procuracao.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.corporativo.model.EmailPessoa;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.EmailJpaRepository;

@Service
public class EmailService {
	
	@Autowired
	private EmailJpaRepository emailJpaRepository;
	
	@HistoryJpa
	@Transactional
	public void save(String matriculaUsuarioHistorico, EmailPessoa emailPessoa) {
		emailJpaRepository.save(emailPessoa);
	}
	
	public EmailPessoa consultarPorPessoaTipoEmail(Integer idPessoa, Integer tipoEmail) {
		return this.emailJpaRepository.findByIdPessoaAndTipoEmail(idPessoa, tipoEmail);
		
	}	
	
}
