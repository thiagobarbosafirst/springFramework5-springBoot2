package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.javaee.corporativo.model.TelefonePessoa;

@Repository
public interface TelefoneJpaRepository extends JpaRepository<TelefonePessoa, Integer>{
	
	public TelefonePessoa findByIdPessoaAndTipoTelefone(Integer idPessoa, Integer tipoTelefone);
} 
