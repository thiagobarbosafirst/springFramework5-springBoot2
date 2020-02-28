package br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import br.gov.go.sefaz.javaee.commons.support.Data;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;

public class ProcuracaoJdbcRowMapper implements RowMapper<ProcuracaoDto>{

	@Override
	public ProcuracaoDto mapRow(ResultSet resultSet, int line) throws SQLException {
		
		ProcuracaoDto procuracao = new ProcuracaoDto();
		
		procuracao.setId(resultSet.getInt("ID_PROCURACAO"));
		procuracao.setDataProcuracao(resultSet.getDate("DATA_EMISSAO_PROCURACAO") == null ? "" : Data.formatar(resultSet.getDate("DATA_EMISSAO_PROCURACAO"), "dd/MM/yyyy hh:mm:ss"));
		procuracao.setValidade(resultSet.getDate("DATA_VALIDADE_PROCURACAO") == null ? "" : Data.formatar(resultSet.getDate("DATA_VALIDADE_PROCURACAO"), "dd/MM/yyyy"));
		
		Character reservaPoder =  resultSet.getString("TIPO_PODER_SUBSTAB").charAt(0);	
		EnumReservaPoderPermitido enumReservaPoderPermitido = EnumReservaPoderPermitido.parse(reservaPoder);
		procuracao.setPoderProcuracao(enumReservaPoderPermitido.getSignificadoReservaPermitido());
		
		procuracao.setDataMaximaSubstab(resultSet.getDate("DATA_MAXIMA_SUBSTAB") == null ? "" : Data.formatar(resultSet.getDate("DATA_MAXIMA_SUBSTAB"), "dd/MM/yyyy"));
		
		if(enumReservaPoderPermitido.getIndiReservaPermitido().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
			procuracao.setPermiteSubstabelecimento(false);
			procuracao.setSubstabelecimentoValidado(false);
		}
		else {
			procuracao.setPermiteSubstabelecimento(true);
			procuracao.setSubstabelecimentoValidado(true);
			
			Date dataAtual = null;
			try {
				dataAtual = Data.toDate(Data.formatar(new java.util.Date(), Data.DATA_PADRAO), Data.DATA_PADRAO);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(resultSet.getDate("DATA_VALIDADE_PROCURACAO") != null && resultSet.getDate("DATA_VALIDADE_PROCURACAO").before(dataAtual)) {
				procuracao.setSubstabelecimentoValidado(false);
			}
			if(resultSet.getDate("DATA_MAXIMA_SUBSTAB") != null && resultSet.getDate("DATA_MAXIMA_SUBSTAB").before(dataAtual)) {
				procuracao.setSubstabelecimentoValidado(false);
			}
		}	
		
		procuracao.setStatus(resultSet.getString("STAT_PROCURACAO"));
		procuracao.setDescricaoStatus(resultSet.getString("descricaoStatus"));		
		
		return procuracao; 
	}	
	
}
