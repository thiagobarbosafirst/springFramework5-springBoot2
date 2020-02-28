package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEnderecoPK;

@Repository
public interface EnderecoPessoaJpaRepository extends JpaRepository<PessoaEndereco, PessoaEnderecoPK> {
	

} 
