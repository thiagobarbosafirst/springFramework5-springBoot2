package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;

@Repository
public interface MovimentacaoProcuracaoJpaRepository extends JpaRepository<MovimentacaoProcuracao, Integer>{	
	
	public List<MovimentacaoProcuracao> findBySujeitoPassivoProcessoProcessoAdministrativoTributarioEletronicoIdProcessoAdministrativoTributarioEletronico(Integer idProcesso); 
	
	public List<MovimentacaoProcuracao> findByProcuracaoId(Integer idProcuracao);
	
	public List<MovimentacaoProcuracao> findByProcuracaoIdAndIndiMovmtProcuracaoAtivaAsChar(Integer idProcuracao, Character indice); 
	
	public List<MovimentacaoProcuracao> findBySubEstabelecimentoProcuracaoId(Integer idSubstabelecimento);  
	
	public List<MovimentacaoProcuracao> findByProcuracaoStatusAndSujeitoPassivoProcessoId(String statusOutorga, Integer idSujeitoPassivoProcesso); 
	
	public List<MovimentacaoProcuracao> findByIdIn(Integer[] idsMovimentacao);
	
	public List<MovimentacaoProcuracao>findByRenunciaProcuracaoId(Integer idRenuncia);
	
	public List<MovimentacaoProcuracao> findByRevogacaoProcuracaoId(Integer idRevogacao);
}  
