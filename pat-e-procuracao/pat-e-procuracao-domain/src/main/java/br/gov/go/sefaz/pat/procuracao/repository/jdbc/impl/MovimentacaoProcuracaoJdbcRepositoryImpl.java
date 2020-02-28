package br.gov.go.sefaz.pat.procuracao.repository.jdbc.impl;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.constants.TipoAgrupadorMovimentacaoMapper;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.MovimentacaoProcuracaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper.AgrupadorMovimentacaoJdbcRowMapper;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper.MovimentacaoProcuracaoJdbcRowMapper;

@Repository
public class MovimentacaoProcuracaoJdbcRepositoryImpl implements MovimentacaoProcuracaoJdbcRepository{

	@Autowired
	private JdbcTemplate patProcuracaojdbcTemplate;
		
	private static StringBuilder queryBase = new StringBuilder();
	private static StringBuilder nvlBase = new StringBuilder();
	private static StringBuilder decodeBase = new StringBuilder();
	private static StringBuilder tablesBase = new StringBuilder();
	private static StringBuilder filterProcesso = new StringBuilder();
	private static StringBuilder substabelecimentoBase = new StringBuilder();
	
	private static StringBuilder orderByBase = new StringBuilder();
	private static StringBuilder orderByRenunciados = new StringBuilder();
	
	private static StringBuilder filterRenunciar = new StringBuilder();
	private static StringBuilder filterRenunciarByProcuracao = new StringBuilder();	
	private static StringBuilder filterSubstabelecer = new StringBuilder();
	private static StringBuilder filterSubstabelecimento = new StringBuilder();
	
	private static StringBuilder filterRenunciados = new StringBuilder();
	private static StringBuilder filterRenunciadosByStatusAssinatura = new StringBuilder();
	private static StringBuilder filterRenunciadosByCnpjBase = new StringBuilder();
	private static StringBuilder filterTodosRenunciados = new StringBuilder();	
		
	//Constantes referente revogação
	private static StringBuilder filterRevogacaoIdProcuracao = new StringBuilder();
	private static StringBuilder filterRevogacaoCnpjBase = new StringBuilder();
	private static StringBuilder filterRevogacaoIdPessoa = new StringBuilder();	
	private static StringBuilder orderByDateRevogacoes = new StringBuilder();
	
	//Constantes referente a query da movimentacao
	private static StringBuilder queryBaseMovimentacao = new StringBuilder();
	private static StringBuilder tablesBaseMovimentacao = new StringBuilder();
	private static StringBuilder filterMovimentacaoByPessoaIdPessoa = new StringBuilder();
	private static StringBuilder filterMovimentacaoByRevogacaoIdRevogacao = new StringBuilder();
	private static StringBuilder filterMovimentacaoByProcuracaoIdProcuracao = new StringBuilder();
	private static StringBuilder filterMovimentacaoByCnpjBase = new StringBuilder();
	//Constantes Procuracao
	private static StringBuilder filterProcuracaoByCnpjBase = new StringBuilder();
	private static StringBuilder filterProcuracaoByIdPessoa = new StringBuilder();
	private static StringBuilder filterProcuracaoByProcurador = new StringBuilder();
	private static StringBuilder orderByDateProcuracao = new StringBuilder();
	
	
	static {		
		queryBase
			.append(" SELECT DISTINCT ")
			.append(" PROCESSO.ID_PROCESSO_ADM_TRIB_ELET  AS ID_PROCESSO, ")
			.append(" PROCESSO.TIPO_DOCUMENTO_ORIGEM_PAT || TO_CHAR(PROCESSO.NUMR_SEQUENCIAL_PAT, 'FM0000000000') || TO_CHAR(PROCESSO.NUMR_DIGITO_VERIF_PAT,'FM00') NUMR_PAT, ")
			.append(" PROCESSO.STAT_PAT, ")
			.append(" PROCESSO.DATA_FORMLZCAO_PAT, ")
			.append(" PROCURADOR.ID_PESSOA AS ID_PESSOA_PROCURADOR, ")			
			.append(" PES_FIS.NOME_PESSOA NOME_PROCURADOR, ")
			.append(" PES_FIS.NUMR_CPF CPF_PROCURADOR, ")
			.append(" SUJEITO.TIPO_SUJEITO_PASSIVO, ")
			.append(" SUJEITO.ID_SUJEITO_PASSIVO_PROCESSO, ")
			.append(" MOVIMENTACAO.ID_MOVMT_PROCURACAO, ")
			.append(" MOVIMENTACAO.ID_SUBSTAB_PROCR ID_SUBSTABELECIMENTO, ")
			.append(" MOVIMENTACAO.ID_RENUNCIA_PROCURACAO ID_RENUNCIA_OUTORGA, ")
			.append(" RITO.DESC_RITO_PROCESSUAL, ")				
			.append(" REVOGACAO.ID_REVOGACAO_PROCURACAO, ")
			.append(" REVOGACAO.INDI_PENDENTE_ASSINATURA_DIG, ")
			.append(" REVOGACAO.INDI_REVOGACAO_PRESENCIAL, ")
			.append(" REVOGACAO.DATA_REVOGACAO_PROCURACAO, ")
			.append(" PROCURACAO.ID_PROCURACAO, ")
			.append(" PROCURACAO.DATA_EMISSAO_PROCURACAO, ") 
			.append(" PROCURACAO.DATA_VALIDADE_PROCURACAO, ")
			.append(" PROCURACAO.STAT_PROCURACAO, ")
			.append(" PROCURACAO.INDI_PROCURACAO_PRESENCIAL, ")
			.append(" PROCURACAO.DATA_VALIDADE_PROCURACAO, ")
			.append(" PROCURACAO.TIPO_PODER_SUBSTAB, ")
			.append(" DECODE(STAT_PROCURACAO, 1, 'Ativa', 2, 'Revogada', 3, 'Vencida', 4, 'Encerrada', 5, 'Inativa - Por renúncia de todos representantes', 6, 'Pendente de Assinatura Digital') DESC_STATUS_PROC, ")
			.append(" DECODE(TIPO_PODER_SUBSTAB, 1, 'Sem poderes para substabelecer', 2, 'somente com reserva de poderes', 3, 'somente sem reserva de poderes', 4, 'com ou sem reserva de poderes') DESC_TIPO_PODER_PROC, ");
			
		nvlBase
			.append("( NVL((SELECT PES_FIS.NUMR_CPF FROM GEN_PESSOA_FISICA PES_FIS  ")
			.append("WHERE PES_FIS.ID_PESSOA = SUJEITO.ID_PESSOA), ")
			.append("(SELECT PES_JUR.NUMR_CNPJ FROM GEN_PESSOA_JURIDICA PES_JUR ")
			.append("WHERE PES_JUR.ID_PESSOA = SUJEITO.ID_PESSOA)))  CNPJ_CPF, ")
			
			.append("( NVL((SELECT PES_FIS.NOME_PESSOA FROM GEN_PESSOA_FISICA PES_FIS ")
			.append("WHERE PES_FIS.ID_PESSOA = SUJEITO.ID_PESSOA ), ")
			.append("(SELECT PES_JUR.NOME_FANTASIA FROM GEN_PESSOA_JURIDICA PES_JUR ")
			.append("WHERE PES_JUR.ID_PESSOA = SUJEITO.ID_PESSOA )) )  NOME_SUJEITO ");
		
		decodeBase	
			.append(" , ")
			.append("MOVIMENTACAO.INDI_MOVMT_PROCURACAO_ATIVA AS STATUS_MOVIMENTACAO_CODIGO, ")
			.append("DECODE(MOVIMENTACAO.INDI_MOVMT_PROCURACAO_ATIVA, ")
			.append("'S', 'Ativo', ")
			.append("'N', 'Inativo') AS STATUS_MOVIMENTACAO_DESC,") 
			.append("PROCURACAO.ID_PROCURACAO, ")
			.append("PROCURACAO.DATA_EMISSAO_PROCURACAO,  ")
			.append("PROCURACAO.DATA_VALIDADE_PROCURACAO, ")
			.append("PROCURACAO.STAT_PROCURACAO, ")
			.append("DECODE(STAT_PROCURACAO, 1, 'Ativa', 2, 'Revogada', 3, 'Vencida', 4, 'Encerrada', ")
			.append("5, 'Inativa - Por renúncia de todos representantes', 6, ")
			.append("'Pendente de Assinatura Digital') AS STATUS_PROCURACAO_DESC, ")
			.append("RENUNCIA.ID_RENUNCIA_PROCURACAO ID_RENUNCIA_OUTORGA, ")
			.append("RENUNCIA.DATA_RENUNCIA_PROCURACAO  DATA_RENUNCIA_OUTORGA, ")
			.append("RENUNCIA.INDI_PENDENTE_ASSINATURA_DIG  PENDENTE_ASSINATURA_DIG, ")
			.append("RENUNCIA.INDI_RENUNCIA_PRESENCIAL ");
		
		substabelecimentoBase
			.append(", ( NVL((SELECT SUBST.INDI_RESERVA_SUBSTAB_PROCR FROM PAT_SUBESTABELECIMENTO_PROCR SUBST ")
			.append("WHERE SUBST.ID_SUBSTAB_PROCR = MOVIMENTACAO.ID_SUBSTAB_PROCR), ")
			.append("PROCURACAO.TIPO_PODER_SUBSTAB)) INDI_RESERVA_SUBSTAB_PROCR, ")
			.append("SUBSTABELECIMENTO.ID_SUBSTAB_PROCR, ")
			.append("SUBSTABELECIMENTO.DATA_SUBSTAB_PROCR, ")
			.append("SUBSTABELECIMENTO.INDI_RESERVA_SUBSTAB_PROCR, ")			
			.append("SUBSTABELECIMENTO.DATA_LIMITE_SUBSTAB, ")
			.append("SUBSTABELECIMENTO.DATA_VALIDADE_SUBSTAB_PROCR, ")
			.append("SUBSTABELECIMENTO.STAT_SUBSTAB_PROCR, ")
			.append("SUBSTABELECIMENTO.INDI_SUBSTAB_PRESENCIAL ");   
			
		tablesBase
			.append("FROM ")
			.append("PAT_MOVIMENTACAO_PROCURACAO MOVIMENTACAO ")
			.append("LEFT OUTER JOIN PAT_PROCURACAO PROCURACAO ")
			.append("ON MOVIMENTACAO.ID_PROCURACAO = PROCURACAO.ID_PROCURACAO ")
			.append("LEFT OUTER JOIN PAT_PROCURADOR PROCURADOR ")
			.append("ON MOVIMENTACAO.ID_PROCURADOR = PROCURADOR.ID_PROCURADOR ")
			.append("LEFT OUTER JOIN PAT_SUJEITO_PASSIVO_PROCESSO SUJEITO ")
			.append("ON MOVIMENTACAO.ID_SUJEITO_PASSIVO_PROCESSO = SUJEITO.ID_SUJEITO_PASSIVO_PROCESSO ")
			.append("LEFT OUTER JOIN PAT_PROCESSO_ADM_TRIB_ELET PROCESSO ")
			.append("ON SUJEITO.ID_PROCESSO_ADM_TRIB_ELET = PROCESSO.ID_PROCESSO_ADM_TRIB_ELET ")
			.append("LEFT OUTER JOIN PAT_RITO_PROCESSUAL RITO ")
			.append("ON PROCESSO.ID_RITO_PROCESSUAL = RITO.ID_RITO_PROCESSUAL ")
			.append("LEFT OUTER JOIN PAT_RENUNCIA_PROCURACAO RENUNCIA ")
			.append("ON MOVIMENTACAO.ID_RENUNCIA_PROCURACAO = RENUNCIA.ID_RENUNCIA_PROCURACAO ")
			.append("LEFT OUTER JOIN PAT_REVOGACAO_PROCURACAO REVOGACAO ")
			.append("ON MOVIMENTACAO.ID_REVOGACAO_PROCURACAO = REVOGACAO.ID_REVOGACAO_PROCURACAO ")
			.append("LEFT OUTER JOIN GEN_PESSOA_FISICA PES_FIS ")
			.append("ON PROCURADOR.ID_PESSOA = PES_FIS.ID_PESSOA  ")
			.append("LEFT OUTER JOIN GEN_PESSOA_JURIDICA PES_JUR ")
			.append("ON SUJEITO.ID_PESSOA = PES_JUR.ID_PESSOA ")
			.append("LEFT OUTER JOIN GEN_PESSOA PESSOA ") 
			.append("ON SUJEITO.ID_PESSOA = PESSOA.ID_PESSOA ")  
			.append("LEFT OUTER JOIN PAT_SUBESTABELECIMENTO_PROCR SUBSTABELECIMENTO ")
			.append("ON MOVIMENTACAO.ID_SUBSTAB_PROCR = SUBSTABELECIMENTO.ID_SUBSTAB_PROCR ")
			.append("WHERE ");
//			.append("PROCESSO.STAT_PAT = 2 ");  
		
		filterProcesso.append("PROCESSO.STAT_PAT = 2 ");
			 			 
		filterRenunciar
			.append("AND PROCURADOR.ID_PESSOA = ? ")
			.append("AND MOVIMENTACAO.INDI_MOVMT_PROCURACAO_ATIVA = 'S' ")
			.append("AND PROCURACAO.STAT_PROCURACAO = 1 ")
			.append("AND RENUNCIA.ID_RENUNCIA_PROCURACAO IS NULL ")
		    .append("AND (to_date(DATA_VALIDADE_PROCURACAO, 'dd/MM/yy') >= to_date(SYSDATE, 'dd/MM/yy') OR DATA_VALIDADE_PROCURACAO IS NULL) ");
		
		filterRenunciarByProcuracao
			.append("AND PROCURACAO.STAT_PROCURACAO = 1 ")
			.append("AND MOVIMENTACAO.ID_PROCURACAO = ? ");
		
		filterRenunciados			
			.append("AND PROCURADOR.ID_PESSOA = ? ")			
			.append("AND MOVIMENTACAO.ID_RENUNCIA_PROCURACAO IS NOT NULL ");
		
		filterRenunciadosByStatusAssinatura
			.append("AND RENUNCIA.INDI_PENDENTE_ASSINATURA_DIG = ? ");
		
		filterRenunciadosByCnpjBase			
			.append("AND PES_JUR.NUMR_CNPJ_BASE = ? ")			
			.append("AND MOVIMENTACAO.ID_RENUNCIA_PROCURACAO IS NOT NULL ");
		
		filterTodosRenunciados
			.append("AND MOVIMENTACAO.ID_RENUNCIA_PROCURACAO IS NOT NULL ");
				
		filterSubstabelecer
			.append("AND PROCURADOR.ID_PESSOA = ? ")
			.append("AND MOVIMENTACAO.INDI_MOVMT_PROCURACAO_ATIVA = 'S' ")
			.append("AND PROCURACAO.STAT_PROCURACAO = 1 ")
			.append("AND RENUNCIA.ID_RENUNCIA_PROCURACAO IS NULL ")
			.append("AND PROCURACAO.ID_PROCURACAO = ? ")
		    .append("AND (to_date(DATA_VALIDADE_PROCURACAO, 'dd/MM/yy') >= to_date(SYSDATE, 'dd/MM/yy') OR DATA_VALIDADE_PROCURACAO IS NULL) "); 
		
		filterSubstabelecimento
		.append("AND MOVIMENTACAO.ID_MOVMT_PROCURACAO_ORIGEM IN (SELECT M.ID_MOVMT_PROCURACAO FROM PAT_MOVIMENTACAO_PROCURACAO M, PAT_PROCURADOR P WHERE M.ID_PROCURADOR = P.ID_PROCURADOR AND P.ID_PESSOA = ?) ")
		//.append("AND MOVIMENTACAO.INDI_MOVMT_PROCURACAO_ATIVA = 'S' ") 
		.append("AND PROCURACAO.STAT_PROCURACAO = 1 ")
		.append("AND MOVIMENTACAO.ID_SUBSTAB_PROCR IS NOT NULL "); 
		//.append("AND RENUNCIA.ID_RENUNCIA_PROCURACAO IS NULL ");
		//.append("AND PROCURACAO.ID_PROCURACAO = ? ")
	    //.append("AND (to_date(DATA_VALIDADE_PROCURACAO, 'dd/MM/yy') >= to_date(SYSDATE, 'dd/MM/yy') OR DATA_VALIDADE_PROCURACAO IS NULL) "); 
		
		filterRevogacaoIdProcuracao
			.append("AND PROCURACAO.STAT_PROCURACAO = 1 ")
			.append("AND MOVIMENTACAO.ID_PROCURACAO = ? ");
	
		filterRevogacaoCnpjBase
			.append("AND MOVIMENTACAO.ID_REVOGACAO_PROCURACAO is not null ")
			.append("AND PES_JUR.NUMR_CNPJ_BASE = ? ");
		
		filterRevogacaoIdPessoa
			.append("AND MOVIMENTACAO.ID_REVOGACAO_PROCURACAO is not null ")
			.append("AND PESSOA.ID_PESSOA = ? ");
		
		filterProcuracaoByCnpjBase
			.append("PES_JUR.NUMR_CNPJ_BASE = ? ");
		
		filterProcuracaoByIdPessoa
			.append("PESSOA.ID_PESSOA = ? ");
		
		filterProcuracaoByProcurador
		.append("PROCURADOR.ID_PESSOA = ? ");
		
		orderByBase
			.append("ORDER BY PROCESSO.ID_PROCESSO_ADM_TRIB_ELET ");
		
		orderByRenunciados
			.append("ORDER BY RENUNCIA.ID_RENUNCIA_PROCURACAO desc");
		
		orderByDateRevogacoes
			.append("ORDER BY REVOGACAO.DATA_REVOGACAO_PROCURACAO desc ");
		
		orderByDateProcuracao
			.append("ORDER BY PROCURACAO.DATA_EMISSAO_PROCURACAO desc ");
		
	}
		
	private StringBuilder construirQueryRenunciar(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);		
		stringQuery.append(filterRenunciar);
		stringQuery.append(orderByBase);
		
		return stringQuery;
	}
	
	private StringBuilder construirQueryRenunciarByProcuracao(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterRenunciar);
		stringQuery.append(filterRenunciarByProcuracao);
		stringQuery.append(orderByBase);
		
		return stringQuery;
	}
	
	private StringBuilder construirQueryRenunciados(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterRenunciados);
		stringQuery.append(orderByRenunciados);
		return stringQuery;
	}
	
	private String construirQueryRenunciadosByStatusAssinatura(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterRenunciados);
		stringQuery.append(filterRenunciadosByStatusAssinatura);
		stringQuery.append(orderByRenunciados);
		return stringQuery.toString();
	}
	
	private StringBuilder construirQueryRenunciadosByCnpjBase(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterRenunciadosByCnpjBase);
		stringQuery.append(orderByRenunciados);
		return stringQuery;
	}
	
	private StringBuilder construirQueryTodosRenunciados(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterTodosRenunciados);		
		stringQuery.append(orderByRenunciados);
		return stringQuery;
	}
	
	private StringBuilder construirQuerySubstabelecimento(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(substabelecimentoBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterSubstabelecer);
		stringQuery.append(orderByBase);
		
		return stringQuery;
	}
	
	private StringBuilder construirQuerySubstabelecidos(){
		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(queryBase);
		stringQuery.append(nvlBase);
		stringQuery.append(decodeBase);
		stringQuery.append(substabelecimentoBase);
		stringQuery.append(tablesBase);
		stringQuery.append(filterProcesso);
		stringQuery.append(filterSubstabelecimento);
		stringQuery.append(orderByBase);
		
		return stringQuery;
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> findSubstabelecerByIdPessoaProcuradorAndProcuracao(Integer idPessoa, Integer idProcuracao) {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQuerySubstabelecimento().toString(), new Object[]{idPessoa, idProcuracao},new int[]{java.sql.Types.INTEGER, java.sql.Types.INTEGER});	
 	    AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.PROCESSO);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> findSubstabelecimento(Integer idPessoa) {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQuerySubstabelecidos().toString(), new Object[]{idPessoa},new int[]{java.sql.Types.INTEGER});
 	    AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.SUBSTABELECIMENTO);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> findRenunciarByIdPessoaProcurador(Integer idPessoa) {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQueryRenunciar().toString(), new Object[]{idPessoa},new int[]{Types.INTEGER});
		return  new AgrupadorMovimentacaoJdbcRowMapper().converterQuery(list,TipoAgrupadorMovimentacaoMapper.RENUNCIA);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> findRenunciarByIdPessoaProcuradorAndProcuracao(Integer idPessoa,Integer idProcuracao) {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQueryRenunciarByProcuracao().toString(),
				new Object[]{idPessoa, idProcuracao},
				new int[]{Types.INTEGER, Types.INTEGER});
		return new AgrupadorMovimentacaoJdbcRowMapper().converterQuery(list,TipoAgrupadorMovimentacaoMapper.RENUNCIA);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcurador(Integer idPessoa) {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQueryRenunciados().toString(), new Object[]{idPessoa},new int[]{Types.INTEGER});		
 	    AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list,TipoAgrupadorMovimentacaoMapper.RENUNCIA);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcuradorAndStatusAssinatura(Integer idPessoa, String pendenteAssinatura) {
				
		Object[] parametros  = new Object[]{idPessoa, pendenteAssinatura};		
		int[] tiposParametros = new int[]{Types.INTEGER, Types.CHAR};
		
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQueryRenunciadosByStatusAssinatura(),parametros,tiposParametros);
		return new AgrupadorMovimentacaoJdbcRowMapper().converterQuery(list,TipoAgrupadorMovimentacaoMapper.RENUNCIA);
	}
	
	
	@Override
	public List<AgrupadorMovimentacaoDto>  findRenunciadosByIdPessoaProcuradorPJ(String numeroCnpjBase) {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQueryRenunciadosByCnpjBase().toString(), new Object[]{numeroCnpjBase},new int[]{Types.INTEGER});
		return new AgrupadorMovimentacaoJdbcRowMapper().converterQuery(list,TipoAgrupadorMovimentacaoMapper.RENUNCIA);
	}	
	
	@Override
	public List<AgrupadorMovimentacaoDto>  findTodosRenunciados() {
		List<Map<String,Object>> list = patProcuracaojdbcTemplate.queryForList(construirQueryTodosRenunciados().toString());	 	    
		return new AgrupadorMovimentacaoJdbcRowMapper().converterQuery(list,TipoAgrupadorMovimentacaoMapper.RENUNCIA);
	}
	
	/**
	 * Querys Revogação
	 */
	private String queryRevogacaoByIdProcuracao(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(nvlBase)
				.append(tablesBase)
				.append(filterProcesso)
				.append(filterRevogacaoIdProcuracao);
		
		return stringQuery.toString();
	}
	
	private String queryRevogacaoByCnpj(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(nvlBase)
				.append(tablesBase)
				.append(filterProcesso)
				.append(filterRevogacaoCnpjBase)
				.append(orderByDateRevogacoes);
		
		return stringQuery.toString();
	}
	
	private String queryRevogacaoByIdPessoa(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(nvlBase)
				.append(tablesBase)
				.append(filterProcesso)
				.append(filterRevogacaoIdPessoa)
				.append(orderByDateRevogacoes);
		
		return stringQuery.toString();
	}
		
	@Override
	public List<AgrupadorMovimentacaoDto> findProcuracaoBySujeitoPassivoProcurador(Integer idProcuracao) throws SQLException {		
		List<Map<String, Object>> list = patProcuracaojdbcTemplate.queryForList(queryRevogacaoByIdProcuracao().toString(), new Object[] {idProcuracao}, new int[]{java.sql.Types.INTEGER});
		AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.PROCESSO);
	}

	@Override
	public List<AgrupadorMovimentacaoDto> listRevogacaoesBySujeitoPassivoProcuradorPJ(String numeroCnpjBase) throws SQLException {
		List<Map<String, Object>> list = patProcuracaojdbcTemplate.queryForList(queryRevogacaoByCnpj().toString(), new Object[] {numeroCnpjBase}, new int[]{java.sql.Types.VARCHAR});
		AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.REVOGACAO);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> listRevogacaoesBySujeitoPassivoProcurador(Integer idPessoa) throws SQLException {
		List<Map<String, Object>> list = patProcuracaojdbcTemplate.queryForList(queryRevogacaoByIdPessoa().toString(), new Object[] {idPessoa}, new int[]{java.sql.Types.INTEGER});
		AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.REVOGACAO);
	}	
	/**
	 * Querys Procuração
	 */
	private String queryProcuracaoByCnpjBase(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(nvlBase)
				.append(tablesBase)
				.append(filterProcuracaoByCnpjBase)
				.append(orderByDateProcuracao);
		
		return stringQuery.toString();
	}
	
	private String queryProcuracaoByIdPessoa(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(nvlBase)
				.append(tablesBase)
				.append(filterProcuracaoByIdPessoa)
				.append(orderByDateProcuracao);
		
		return stringQuery.toString();
	}
	
	private String queryProcuracaoByProcurador(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBase)
				.append(nvlBase)
				.append(substabelecimentoBase)
				.append(tablesBase)
				.append(filterProcuracaoByProcurador)
				.append(orderByDateProcuracao);
		
		return stringQuery.toString();
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivo(String numrCnpjBase) {
		List<Map<String, Object>> list = patProcuracaojdbcTemplate.queryForList(queryProcuracaoByCnpjBase().toString(), new Object[] {numrCnpjBase}, new int[]{java.sql.Types.VARCHAR});
		AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.PROCURACAO); 
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivo(Integer idPessoa) {
		List<Map<String, Object>> list = patProcuracaojdbcTemplate.queryForList(queryProcuracaoByIdPessoa().toString(), new Object[] {idPessoa}, new int[]{java.sql.Types.INTEGER});
		AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.PROCURACAO);
	}
	
	@Override
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorProcurador(Integer idPessoaProcurador) {
		List<Map<String, Object>> list = patProcuracaojdbcTemplate.queryForList(queryProcuracaoByProcurador().toString(), new Object[] {idPessoaProcurador}, new int[]{java.sql.Types.INTEGER});
		AgrupadorMovimentacaoJdbcRowMapper rowMapper = new AgrupadorMovimentacaoJdbcRowMapper();
		return rowMapper.converterQuery(list, TipoAgrupadorMovimentacaoMapper.PROCURACAO_PROCURADOR);
	}
	
	/**
	 * Busca de movimentações por idPessoa, idRevogacao e idProcuracao
	 */
	static {
		queryBaseMovimentacao 
			.append(" select ")
			.append(" MP.ID_MOVMT_PROCURACAO, ")
			.append(" MP.DATA_INICIO_MOVMT_PROCURACAO, ")
			.append(" MP.INDI_MOVMT_PROCURACAO_ATIVA, ")
			.append(" MP.ID_SUBSTAB_PROCR, ")
			.append(" MP.ID_RENUNCIA_PROCURACAO, ")
			.append(" MP.ID_PROCURACAO, ")
			.append(" MP.ID_SUJEITO_PASSIVO_PROCESSO, ")
			.append(" MP.ID_PROCURADOR, ")
			.append(" MP.ID_REVOGACAO_PROCURACAO ");
		
		tablesBaseMovimentacao
			.append(" from ")
			.append(" PAT_MOVIMENTACAO_PROCURACAO MP ")
			.append(" left outer join PAT_PROCURACAO PROCURACAO ")
			.append(" on MP.ID_PROCURACAO = PROCURACAO.ID_PROCURACAO ")
			.append(" left outer join PAT_PROCURADOR PROCURADOR ")
			.append(" on MP.ID_PROCURADOR = PROCURADOR.ID_PROCURADOR ")
			.append(" left outer join PAT_SUBESTABELECIMENTO_PROCR SUB_PROCR ")
			.append(" on MP.ID_SUBSTAB_PROCR = SUB_PROCR.ID_SUBSTAB_PROCR ")
			.append(" left outer join PAT_RENUNCIA_PROCURACAO RENUNCIA ")
			.append(" on MP.ID_RENUNCIA_PROCURACAO = RENUNCIA.ID_RENUNCIA_PROCURACAO ")
			.append(" left outer join PAT_SUJEITO_PASSIVO_PROCESSO SPP ")
			.append(" on MP.ID_SUJEITO_PASSIVO_PROCESSO = SPP.ID_SUJEITO_PASSIVO_PROCESSO ")
			.append(" left outer join PAT_REVOGACAO_PROCURACAO REVOGACAO ")
			.append(" on MP.ID_REVOGACAO_PROCURACAO = REVOGACAO.ID_REVOGACAO_PROCURACAO ")
			.append(" left outer join GEN_PESSOA GEN_PES ")
			.append(" on SPP.ID_PESSOA = GEN_PES.ID_PESSOA  ")
			.append(" left outer join GEN_PESSOA_JURIDICA PES_JUR ")
			.append(" on SPP.ID_PESSOA = PES_JUR.ID_PESSOA ")
			.append(" left outer join GEN_PESSOA_FISICA PES_FIS ")
			.append(" on PROCURADOR.ID_PESSOA = PES_FIS.ID_PESSOA ")
			.append(" where ");
		
		filterMovimentacaoByPessoaIdPessoa
			.append(" GEN_PES.ID_PESSOA = ?");
		
		filterMovimentacaoByRevogacaoIdRevogacao
			.append(" MP.ID_REVOGACAO_PROCURACAO = ?");
		
		filterMovimentacaoByProcuracaoIdProcuracao
			.append(" MP.ID_PROCURACAO = ?");
		
		filterMovimentacaoByCnpjBase
			.append(" PES_JUR.NUMR_CNPJ_BASE = ? ");
	}
	
	private String queryMovimentacaoByPessoaIdPessoa(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBaseMovimentacao)
				.append(tablesBaseMovimentacao)
				.append(filterMovimentacaoByPessoaIdPessoa);
		
		return stringQuery.toString();
	}
	
	private String queryMovimentacaoByPessoaCnpjBase(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBaseMovimentacao)
				.append(tablesBaseMovimentacao)
				.append(filterMovimentacaoByCnpjBase);
		
		return stringQuery.toString();
	}
	
	private String queryMovimentacaoByRevogacaoIdRevogacao(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBaseMovimentacao)
				.append(tablesBaseMovimentacao)
				.append(filterMovimentacaoByRevogacaoIdRevogacao);
		
		return stringQuery.toString();
	}
	
	private String queryMovimentacaoByProcuracaoIdProcuracao(){
		StringBuilder stringQuery = new StringBuilder()	
				.append(queryBaseMovimentacao)
				.append(tablesBaseMovimentacao)
				.append(filterMovimentacaoByProcuracaoIdProcuracao);
		
		return stringQuery.toString();
	}
	
	@Override
	public List<MovimentacaoProcuracao> findByPessoa(Integer idPessoa) {
		return patProcuracaojdbcTemplate.query(queryMovimentacaoByPessoaIdPessoa().toString(), new Object[] {idPessoa}, new MovimentacaoProcuracaoJdbcRowMapper());
	}

	@Override
	public List<MovimentacaoProcuracao> findByPessoa(String cnpjBase) {
		return patProcuracaojdbcTemplate.query(queryMovimentacaoByPessoaCnpjBase().toString(), new Object[] {cnpjBase}, new MovimentacaoProcuracaoJdbcRowMapper());
	}			

	@Override
	public List<MovimentacaoProcuracao> findByRevogacao(Integer idRevogacao) {
		return patProcuracaojdbcTemplate.query(queryMovimentacaoByRevogacaoIdRevogacao().toString(), new Object[] {idRevogacao}, new MovimentacaoProcuracaoJdbcRowMapper());
	}

	@Override
	public List<MovimentacaoProcuracao> findByProcuracao(Integer idProcuracao) {
		return patProcuracaojdbcTemplate.query(queryMovimentacaoByProcuracaoIdProcuracao().toString(), new Object[] {idProcuracao}, new MovimentacaoProcuracaoJdbcRowMapper());
	}

}
