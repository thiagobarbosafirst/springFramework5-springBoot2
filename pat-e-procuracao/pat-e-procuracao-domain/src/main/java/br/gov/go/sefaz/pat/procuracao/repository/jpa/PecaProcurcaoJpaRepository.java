package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.PecaProcuracao;

@Repository
public interface PecaProcurcaoJpaRepository extends JpaRepository<PecaProcuracao, Integer>{
	
	PecaProcuracao findByProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(Integer id, String descricaoModeloDocumento);
	
	List<PecaProcuracao> findByProcuracaoId(Integer id);
	
	PecaProcuracao findByPecaEletronicaId(Integer id);
	
}
