package br.gov.go.sefaz.pat.procuracao.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEnderecoPK;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.EnderecoPessoaJpaRepository;

@Service
public class EnderecoPessoaService {
	
	@Autowired
	private EnderecoPessoaJpaRepository enderecoPessoaJpaRepository;
	
	@HistoryJpa
	@Transactional
	public void save(String matriculaUsuarioHistorico, PessoaEndereco endereco) {
		enderecoPessoaJpaRepository.save(endereco);
	}
	
	public PessoaEndereco consultar(PessoaEnderecoPK pessoaEnderecoPK) {
		return this.enderecoPessoaJpaRepository.findOne(pessoaEnderecoPK);
	}	
	
}
 