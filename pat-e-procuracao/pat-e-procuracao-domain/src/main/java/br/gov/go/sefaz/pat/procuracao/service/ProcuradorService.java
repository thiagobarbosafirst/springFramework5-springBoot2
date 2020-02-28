package br.gov.go.sefaz.pat.procuracao.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.corporativo.model.Endereco;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.model.EnderecoProcurador;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEndereco;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.ProcuradorJpaRepository; 

@Service
public class ProcuradorService {
	
	@Autowired
	private ProcuradorJpaRepository procuradorJpaRepository;
	
	@Autowired
	private ComplementoProcuradorService complementoProcuradorService;	
	
	@Autowired
	private EnderecoProcuradorService enderecoProcuradorService;	
		
	public Procurador consultar(Pessoa pessoa) {
		return this.procuradorJpaRepository.getOneByPessoa(pessoa);
	} 
	
	public Procurador consultar(Integer idProcurador) {
		return this.procuradorJpaRepository.findOne(idProcurador);
	}
	
	@HistoryJpa
	@Transactional
	public void save(Integer usuarioHistorico, Procurador procurador) {
		procuradorJpaRepository.save(procurador);
	}
	
	public void prepararProcurador(List<MovimentacaoProcuracao> listaMovimentacaoProcuracao) {
		for (MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacaoProcuracao) {			
			Procurador procurador = consultar(
					movimentacaoProcuracao.getProcurador().getPessoa());  
			if(procurador != null) {
				movimentacaoProcuracao.getProcurador().setId(procurador.getId());				
				if(procurador.getEscritorioProcurador() != null && procurador.getEscritorioProcurador().getIdPessoa() != null) { 
					PessoaJuridica pj = new PessoaJuridica();
					pj.setIdPessoa(procurador.getEscritorioProcurador().getIdPessoa());				
					movimentacaoProcuracao.getProcurador().setEscritorioProcurador(pj);
				} 	
				EnderecoProcurador enderecoProcuradorConsulta = enderecoProcuradorService.consultar(procurador.getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
				
				if(enderecoProcuradorConsulta != null) {					
					Endereco enderecoProcurador = movimentacaoProcuracao.getProcurador().getEnderecoProcurador().getEndereco();	
					Endereco endereco = enderecoProcuradorConsulta.getEndereco();
					endereco.setLogradouro(enderecoProcurador.getLogradouro());
					endereco.setNumero(enderecoProcurador.getNumero());
					endereco.setNumeroLote(enderecoProcurador.getNumeroLote());
					endereco.setNumeroQuadra(enderecoProcurador.getNumeroQuadra());
					endereco.setComplemento(enderecoProcurador.getComplemento());
					movimentacaoProcuracao.getProcurador().setEnderecoProcurador(enderecoProcuradorConsulta);					
				}
					
			} 	
			
			if(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa() != null && movimentacaoProcuracao.getProcurador().getListaComplementoPessoa().size() > 0) {
				
				complementoProcuradorService.prepararComplementoProcurador(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa());
				
			}					
		} 
	}
	
}
