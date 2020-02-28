package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.javaee.corporativo.model.EmailPessoa;

@Repository
public interface EmailJpaRepository extends JpaRepository<EmailPessoa, Integer>{
	
	EmailPessoa findByIdPessoaAndTipoEmail(Integer arg0, Integer arg1); 

} 
