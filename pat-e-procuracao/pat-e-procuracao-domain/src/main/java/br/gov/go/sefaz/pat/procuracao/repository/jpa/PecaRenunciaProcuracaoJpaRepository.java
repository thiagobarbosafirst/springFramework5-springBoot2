package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.PecaRenunciaProcuracao;

@Repository
public interface PecaRenunciaProcuracaoJpaRepository extends JpaRepository<PecaRenunciaProcuracao, Integer> {
	
	PecaRenunciaProcuracao findByRenunciaProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(Integer id, String descricaoModeloDocumento);	
	PecaRenunciaProcuracao findByPecaEletronicaId(Integer id);
	List<PecaRenunciaProcuracao> findByRenunciaProcuracaoId(Integer idRenuncia);
}
