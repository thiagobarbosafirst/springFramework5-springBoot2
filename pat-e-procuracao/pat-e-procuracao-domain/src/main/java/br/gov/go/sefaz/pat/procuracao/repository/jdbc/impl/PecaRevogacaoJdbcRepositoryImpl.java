package br.gov.go.sefaz.pat.procuracao.repository.jdbc.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.PecaRevogacaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper.PecaRevogacaoJdbcRowMapper;

@Repository
public class PecaRevogacaoJdbcRepositoryImpl implements PecaRevogacaoJdbcRepository{
	
	@Autowired
	private JdbcTemplate patProcuracaojdbcTemplate;
	
	private static StringBuilder queryBase = new StringBuilder();
	private static StringBuilder tablesBase = new StringBuilder();
	private static StringBuilder filtroIdRevogacao = new StringBuilder();
	
	static {
		queryBase 
			.append(" select ")
			.append(" PECA_REV.ID_PECA_REVOGACAO_PROCURACAO, ")
			.append(" PECA_REV.ID_PECA_ELETRONICA, ")
			.append(" PECA_REV.ID_REVOGACAO_PROCURACAO ");
		
		tablesBase
			.append(" from ")
			.append(" PAT_PECA_REVOGACAO_PROCURACAO PECA_REV ")
			.append(" left outer join PAT_PECA_ELETRONICA PECA_ELE ")
			.append(" on PECA_REV.ID_PECA_ELETRONICA = PECA_ELE.ID_PECA_ELETRONICA ")
			.append(" left outer join PAT_REVOGACAO_PROCURACAO REVOGACAO ")
			.append(" on PECA_REV.ID_REVOGACAO_PROCURACAO = REVOGACAO.ID_REVOGACAO_PROCURACAO ")		
			.append(" where ");
		
		filtroIdRevogacao
			.append(" PECA_REV.ID_REVOGACAO_PROCURACAO = ?");
	}
	
	private String queryPecaRevogacaoPorIdRevogacao(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(tablesBase)
				.append(filtroIdRevogacao);
		
		return stringQuery.toString();
	}
	
	@Override
	public List<PecaRevogacaoProcuracao> findByRevogacaoProcuracaoId(Integer idRevogacao) {
		return patProcuracaojdbcTemplate.query(queryPecaRevogacaoPorIdRevogacao().toString(), new Object[] {idRevogacao}, new PecaRevogacaoJdbcRowMapper());
	}
}
