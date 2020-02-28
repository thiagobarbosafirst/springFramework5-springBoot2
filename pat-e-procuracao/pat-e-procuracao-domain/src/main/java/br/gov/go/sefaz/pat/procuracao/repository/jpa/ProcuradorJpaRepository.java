package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.pat.model.Procurador;

@Repository
public interface ProcuradorJpaRepository extends JpaRepository<Procurador, Integer>{

	@QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
	Procurador getOneByPessoa(Pessoa pessoa);	
 
}
