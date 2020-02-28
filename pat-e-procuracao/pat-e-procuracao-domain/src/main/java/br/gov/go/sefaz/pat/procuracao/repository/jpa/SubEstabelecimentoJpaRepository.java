package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;

@Repository
public interface SubEstabelecimentoJpaRepository extends JpaRepository<SubEstabelecimentoProcuracao, Integer> {
	
	@Query(value = "SELECT * FROM PAT_SUBESTABELECIMENTO_PROCR S WHERE S.ID_SUBSTAB_PROCR  = :id_substab", nativeQuery = true)
	public SubEstabelecimentoProcuracao buscaSubEstabelecimento(@Param("id_substab") Integer id);

	@Query(value = "SELECT STAT_SUBSTAB_PROCR FROM PAT_SUBESTABELECIMENTO_PROCR P WHERE P.ID_SUBSTAB_PROCR  = :id_substabelecimento", nativeQuery = true)
	public Character findSubstabelecimentoById(@Param("id_substabelecimento") Integer id);

}
