package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.javaee.corporativo.model.DocumentoPessoaId;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;

@Repository
public interface DocumentoJpaRepository extends JpaRepository<DocumentoAdvogado, DocumentoPessoaId>{
	

} 
