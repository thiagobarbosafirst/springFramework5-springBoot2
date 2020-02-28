package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.PecaSubstabelecimento;

@Repository
public interface PecaSubstabelecimentoJpaRepository extends JpaRepository<PecaSubstabelecimento, Integer>{
	
	PecaSubstabelecimento findBySubEstabelecimentoProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(Integer id, String descricaoModeloDocumento);
	
	PecaSubstabelecimento findByPecaEletronicaId(Integer id);
	
	List<PecaSubstabelecimento> findBySubEstabelecimentoProcuracaoId(Integer idSubstabelecimento);
}
