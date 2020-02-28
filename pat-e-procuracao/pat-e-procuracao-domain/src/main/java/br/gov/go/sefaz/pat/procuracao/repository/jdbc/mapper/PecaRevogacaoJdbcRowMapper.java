package br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;

@Repository
public class PecaRevogacaoJdbcRowMapper implements RowMapper<PecaRevogacaoProcuracao>{
		
	@Override
	public PecaRevogacaoProcuracao mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		PecaRevogacaoProcuracao pecaRevogacaoProcuracao = new PecaRevogacaoProcuracao();		
		pecaRevogacaoProcuracao.setId(rs.getInt("ID_PECA_REVOGACAO_PROCURACAO"));
		
		PecaEletronica pecaEletronica = new PecaEletronica();
		pecaEletronica.setId(rs.getInt("ID_PECA_ELETRONICA"));
		pecaRevogacaoProcuracao.setPecaEletronica(pecaEletronica);
		
		RevogacaoProcuracao revogacaoProcuracao = new RevogacaoProcuracao();
		revogacaoProcuracao.setId(rs.getInt("ID_REVOGACAO_PROCURACAO"));
		pecaRevogacaoProcuracao.setRevogacaoProcuracao(revogacaoProcuracao);
		
		return pecaRevogacaoProcuracao;
	}
}
