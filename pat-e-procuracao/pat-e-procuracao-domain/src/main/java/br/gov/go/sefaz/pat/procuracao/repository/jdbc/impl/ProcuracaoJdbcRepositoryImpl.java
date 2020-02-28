package br.gov.go.sefaz.pat.procuracao.repository.jdbc.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.ProcuracaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper.ProcuracaoJdbcRowMapper;

@Repository
public class ProcuracaoJdbcRepositoryImpl implements ProcuracaoJdbcRepository{

	@Autowired
	private JdbcTemplate patProcuracaojdbcTemplate;
	
	private static StringBuilder procuracaoPorProcuradorQueryBase = new StringBuilder();
	private static StringBuilder procuracaoPorSujeitoPassivoQueryBase = new StringBuilder();
	private static StringBuilder procuracaoPorSujeitoPassivoPJQueryBase = new StringBuilder();
	private static StringBuilder procuracaoPorSujeitoPassivoAndStatusQueryBase = new StringBuilder();
	
	static {
		procuracaoPorProcuradorQueryBase
			.append("select DISTINCT")
			.append(" PROCURACAO.ID_PROCURACAO, DATA_EMISSAO_PROCURACAO, DATA_VALIDADE_PROCURACAO, STAT_PROCURACAO, PROCURACAO.DATA_MAXIMA_SUBSTAB, ")
			.append(" TIPO_PODER_SUBSTAB, ") 
			.append(" DECODE(STAT_PROCURACAO, 1, 'Ativa', 2, 'Revogada', 3, 'Vencida', 4, 'Encerrada', 5, 'Inativa - Por renúncia de todos representantes', 6, 'Pendente de Assinatura Digital') descricaoStatus from ")
			.append(" PAT_PROCURACAO PROCURACAO, PAT_PROCURADOR PROCURADOR, PAT_MOVIMENTACAO_PROCURACAO MOVIMENTACAO ")
			.append(" WHERE PROCURADOR.ID_PROCURADOR = MOVIMENTACAO.ID_PROCURADOR AND MOVIMENTACAO.ID_PROCURACAO = PROCURACAO.ID_PROCURACAO ");
	}
	
	static {
		procuracaoPorSujeitoPassivoQueryBase
			.append("select DISTINCT")
			.append(" PROCURACAO.ID_PROCURACAO, DATA_EMISSAO_PROCURACAO, DATA_VALIDADE_PROCURACAO, STAT_PROCURACAO, TIPO_PODER_SUBSTAB, PROCURACAO.DATA_MAXIMA_SUBSTAB, ")
			.append(" DECODE(STAT_PROCURACAO, 1, 'Ativa', 2, 'Revogada', 3, 'Vencida', 4, 'Encerrada', 5, 'Inativa - Por renúncia de todos representantes', 6, 'Pendente de Assinatura Digital') descricaoStatus from ")
			.append(" PAT_PROCURACAO PROCURACAO, PAT_PROCURADOR PROCURADOR, PAT_MOVIMENTACAO_PROCURACAO MOVIMENTACAO, ")
			.append(" PAT_SUJEITO_PASSIVO_PROCESSO SUJEITO, PAT_PROCESSO_ADM_TRIB_ELET PROCESSO")
			.append(" WHERE PROCURADOR.ID_PROCURADOR = MOVIMENTACAO.ID_PROCURADOR AND MOVIMENTACAO.ID_PROCURACAO = PROCURACAO.ID_PROCURACAO AND MOVIMENTACAO.ID_SUJEITO_PASSIVO_PROCESSO = SUJEITO.ID_SUJEITO_PASSIVO_PROCESSO ")
			.append(" AND SUJEITO.ID_PROCESSO_ADM_TRIB_ELET = PROCESSO.ID_PROCESSO_ADM_TRIB_ELET ");
	}
	
	static {
		procuracaoPorSujeitoPassivoPJQueryBase
			.append("select DISTINCT")
			.append(" PROCURACAO.ID_PROCURACAO, DATA_EMISSAO_PROCURACAO, DATA_VALIDADE_PROCURACAO, STAT_PROCURACAO, TIPO_PODER_SUBSTAB, PROCURACAO.DATA_MAXIMA_SUBSTAB, ")
			.append(" DECODE(STAT_PROCURACAO, 1, 'Ativa', 2, 'Revogada', 3, 'Vencida', 4, 'Encerrada', 5, 'Inativa - Por renúncia de todos representantes', 6, 'Pendente de Assinatura Digital') descricaoStatus from ")
			.append(" PAT_PROCURACAO PROCURACAO, PAT_PROCURADOR PROCURADOR, PAT_MOVIMENTACAO_PROCURACAO MOVIMENTACAO, ")
			.append(" PAT_SUJEITO_PASSIVO_PROCESSO SUJEITO, PAT_PROCESSO_ADM_TRIB_ELET PROCESSO, GEN_PESSOA_JURIDICA PJ")
			.append(" WHERE PROCURADOR.ID_PROCURADOR = MOVIMENTACAO.ID_PROCURADOR AND MOVIMENTACAO.ID_PROCURACAO = PROCURACAO.ID_PROCURACAO AND MOVIMENTACAO.ID_SUJEITO_PASSIVO_PROCESSO = SUJEITO.ID_SUJEITO_PASSIVO_PROCESSO ")
			.append(" AND SUJEITO.ID_PROCESSO_ADM_TRIB_ELET = PROCESSO.ID_PROCESSO_ADM_TRIB_ELET ");
	}
	
	static {
		procuracaoPorSujeitoPassivoAndStatusQueryBase
			.append("select DISTINCT")
			.append(" PROCURACAO.ID_PROCURACAO ")
			.append(" from PAT_PROCURACAO PROCURACAO, PAT_MOVIMENTACAO_PROCURACAO MOVIMENTACAO ")
			.append(" WHERE MOVIMENTACAO.ID_PROCURACAO = PROCURACAO.ID_PROCURACAO AND (PROCURACAO.DATA_VALIDADE_PROCURACAO IS NULL OR PROCURACAO.DATA_VALIDADE_PROCURACAO > SYSDATE)");
	}
	
	@Override
	public List<ProcuracaoDto> listarProcuracaoPorProcurador(Integer idPessoa) {
		StringBuilder sqlSB = new StringBuilder(procuracaoPorProcuradorQueryBase)
				.append(" AND PROCURADOR.ID_PESSOA = ? ");
		
		return patProcuracaojdbcTemplate.query(sqlSB.toString(), new Object[] {idPessoa}, new ProcuracaoJdbcRowMapper());
	}
	
	@Override
	public List<ProcuracaoDto> listarProcuracaoPorSujeitoPassivo(Integer idPessoa) {
		StringBuilder sqlSB = new StringBuilder(procuracaoPorSujeitoPassivoQueryBase)
				.append(" AND SUJEITO.ID_PESSOA = ? ");
		
		return patProcuracaojdbcTemplate.query(sqlSB.toString(), new Object[] {idPessoa}, new ProcuracaoJdbcRowMapper());
	}
	
	@Override
	public List<ProcuracaoDto> listarProcuracaoPorSujeitoPassivo(String numrCnpjBase) {
		StringBuilder sqlSB = new StringBuilder(procuracaoPorSujeitoPassivoPJQueryBase)
				.append(" AND SUJEITO.ID_PESSOA = PJ.id_PESSOA and PJ.NUMR_CNPJ_BASE = ? ");
		
		return patProcuracaojdbcTemplate.query(sqlSB.toString(), new Object[] {numrCnpjBase}, new ProcuracaoJdbcRowMapper());
	}
	
	@Override
	public boolean verificarProcuracao(Integer idSujeitoPassivo, String status) {
		StringBuilder sqlSB = new StringBuilder(procuracaoPorSujeitoPassivoAndStatusQueryBase)
				.append(" AND MOVIMENTACAO.ID_SUJEITO_PASSIVO_PROCESSO = ? and PROCURACAO.STAT_PROCURACAO = ? ");
		
		try {
		    patProcuracaojdbcTemplate.queryForObject(sqlSB.toString(), new Object[]{idSujeitoPassivo, status}, Long.class);
		    return true;
		} catch (EmptyResultDataAccessException e) {
		    return false;
		}
	}

}
