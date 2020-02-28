package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;

@Repository
public interface PecaRevogacaoJpaRepository extends JpaRepository<PecaRevogacaoProcuracao, Integer>{
	
	PecaRevogacaoProcuracao findByRevogacaoProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(Integer id, String descricaoModeloDocumento);
	
	PecaRevogacaoProcuracao findByPecaEletronicaId(Integer id);

	List<PecaRevogacaoProcuracao> findByRevogacaoProcuracaoId(Integer id);
	
}
