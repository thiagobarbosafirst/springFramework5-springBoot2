package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;

@Repository
public interface RevogacaoJpaRepository extends JpaRepository<RevogacaoProcuracao, Integer>{

}
