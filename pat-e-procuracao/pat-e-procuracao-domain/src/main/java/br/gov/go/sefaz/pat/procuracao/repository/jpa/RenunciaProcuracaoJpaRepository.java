package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.RenunciaProcuracao;

@Repository
public interface RenunciaProcuracaoJpaRepository extends JpaRepository<RenunciaProcuracao, Integer>{	
}  
