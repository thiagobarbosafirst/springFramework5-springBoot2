package br.gov.go.sefaz.pat.procuracao.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.EnderecoProcurador;

@Repository
public interface EnderecoProcuradorJpaRepository extends JpaRepository<EnderecoProcurador, Integer>{	
	
	public EnderecoProcurador findByProcuradorIdAndTipoEndereco(Integer idProcurador, Integer tipoEndereco); 
	
}  
