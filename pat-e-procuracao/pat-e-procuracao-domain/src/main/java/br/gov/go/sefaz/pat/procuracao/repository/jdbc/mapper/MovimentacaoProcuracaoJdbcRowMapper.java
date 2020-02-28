package br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.model.RenunciaProcuracao;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;

public class MovimentacaoProcuracaoJdbcRowMapper implements RowMapper<MovimentacaoProcuracao>{
	
	@Override
	public MovimentacaoProcuracao mapRow(ResultSet rs, int rowNum) throws SQLException {
		MovimentacaoProcuracao movimentacaoProcuracao = new MovimentacaoProcuracao();
		
		movimentacaoProcuracao.setId(rs.getInt("ID_MOVMT_PROCURACAO"));
		movimentacaoProcuracao.setDataInicioMovimentacao(rs.getDate("DATA_INICIO_MOVMT_PROCURACAO"));
		String indiMovmtProcuracaoAtiva = rs.getString("INDI_MOVMT_PROCURACAO_ATIVA");
		movimentacaoProcuracao.setIndiMovmtProcuracaoAtivaAsChar((!StringUtils.isEmpty(indiMovmtProcuracaoAtiva) ? indiMovmtProcuracaoAtiva.charAt(0) : null));
		
		Procuracao procuracao = new Procuracao();
		procuracao.setId(rs.getInt("ID_PROCURACAO"));
		movimentacaoProcuracao.setProcuracao(procuracao);
		
		Procurador procurador = new Procurador();
		procurador.setId(rs.getInt("ID_PROCURADOR"));
		movimentacaoProcuracao.setProcurador(procurador);
		
		SubEstabelecimentoProcuracao subEstabelecimento = new SubEstabelecimentoProcuracao();
		subEstabelecimento.setId(rs.getInt("ID_SUBSTAB_PROCR"));
		movimentacaoProcuracao.setSubEstabelecimentoProcuracao(subEstabelecimento);
		
		RenunciaProcuracao renuncia = new RenunciaProcuracao();
		renuncia.setId(rs.getInt("ID_RENUNCIA_PROCURACAO"));
		movimentacaoProcuracao.setRenunciaProcuracao(renuncia);
		
		RevogacaoProcuracao revogacao = new RevogacaoProcuracao();
		revogacao.setId(rs.getInt("ID_REVOGACAO_PROCURACAO"));
		movimentacaoProcuracao.setRevogacaoProcuracao(revogacao);
		
		SujeitoPassivoProcesso sujeitoPassivo = new SujeitoPassivoProcesso();
		sujeitoPassivo.setId(rs.getInt("ID_SUJEITO_PASSIVO_PROCESSO"));
		movimentacaoProcuracao.setSujeitoPassivoProcesso(sujeitoPassivo);				
		
		return movimentacaoProcuracao;
	}

}
