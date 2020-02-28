package br.gov.go.sefaz.pat.procuracao.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.corporativo.model.TelefonePessoa;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.TelefoneJpaRepository;

@Service
public class TelefoneService {
	
	@Autowired
	private TelefoneJpaRepository telefoneJpaRepository;
	
	@HistoryJpa
	@Transactional
	public void save(String matriculaUsuarioHistorico, TelefonePessoa telefonePessoa) {
		telefoneJpaRepository.save(telefonePessoa); 
	}
	
	public TelefonePessoa consultarPorPessoaTipoTelefone(Integer idPessoa, Integer tipoTelefone) {
		return this.telefoneJpaRepository.findByIdPessoaAndTipoTelefone(idPessoa, tipoTelefone);
	}
}
