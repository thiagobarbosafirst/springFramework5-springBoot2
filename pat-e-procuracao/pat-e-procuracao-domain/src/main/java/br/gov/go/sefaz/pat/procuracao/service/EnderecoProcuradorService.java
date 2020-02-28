package br.gov.go.sefaz.pat.procuracao.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.model.EnderecoProcurador;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.EnderecoProcuradorJpaRepository;
import br.gov.go.sefaz.pat.service.PatEnderecoService;

@Service
public class EnderecoProcuradorService {
	
	@Autowired
	private EnderecoProcuradorJpaRepository enderecoProcuradorJpaRepository;
	
	@Autowired
	private PatEnderecoService patEnderecoService;
	
	@HistoryJpa
	@Transactional
	public void save(Integer idUsuarioHistorico, String matriculaUsuarioGestor, EnderecoProcurador enderecoProcurador) {
		patEnderecoService.save(matriculaUsuarioGestor, enderecoProcurador.getEndereco()); 
		enderecoProcuradorJpaRepository.save(enderecoProcurador);  
	}	
	
	public EnderecoProcurador consultar(Integer idProcurador, Integer tipoEndereco) {
		return this.enderecoProcuradorJpaRepository.findByProcuradorIdAndTipoEndereco(idProcurador, tipoEndereco);
	}
}