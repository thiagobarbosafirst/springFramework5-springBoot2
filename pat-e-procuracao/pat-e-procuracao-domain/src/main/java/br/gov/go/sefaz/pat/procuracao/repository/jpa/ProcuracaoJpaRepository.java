package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.Procuracao;

@Repository
public interface ProcuracaoJpaRepository extends JpaRepository<Procuracao, Integer> {

	@QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true") })
	public List<Procuracao> findAll();

	@Query(value = "SELECT STAT_PROCURACAO FROM PAT_PROCURACAO P WHERE P.ID_PROCURACAO  = :id_procuracao", nativeQuery = true)
	public Integer findIdPecaEletronicaProcuracaoByIdProcuracao(@Param("id_procuracao") Integer id);

}
