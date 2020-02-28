package br.gov.go.sefaz.pat.procuracao.repository.jdbc; 

import java.sql.SQLException;
import java.util.List;

import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;


/**
 @author Marcos Jr Lopez 
*/
public interface MovimentacaoProcuracaoJdbcRepository {	

	List<MovimentacaoProcuracao> findByPessoa(Integer idPessoa);
	List<MovimentacaoProcuracao> findByPessoa(String  cnpjBase);
	List<MovimentacaoProcuracao> findByRevogacao(Integer idRevogacao);
	List<MovimentacaoProcuracao> findByProcuracao(Integer idProcuracao);
	
	List<AgrupadorMovimentacaoDto> findRenunciarByIdPessoaProcurador(Integer idPessoa);
	List<AgrupadorMovimentacaoDto> findRenunciarByIdPessoaProcuradorAndProcuracao(Integer idPessoa,Integer idProcuracao);
	List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcurador(Integer idPessoa);
	List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcuradorAndStatusAssinatura(Integer idPessoa, String pendenteAssinatura);
	List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcuradorPJ(String numeroCnpjBase);
	List<AgrupadorMovimentacaoDto> findTodosRenunciados();
	
	List<AgrupadorMovimentacaoDto> findSubstabelecerByIdPessoaProcuradorAndProcuracao(Integer idPessoa, Integer idProcuracao);
	//Consulta para tela de revogação
	List<AgrupadorMovimentacaoDto> findProcuracaoBySujeitoPassivoProcurador(Integer idProcuracao) throws SQLException;
	List<AgrupadorMovimentacaoDto> listRevogacaoesBySujeitoPassivoProcuradorPJ(String numeroCnpjBase) throws SQLException;
	List<AgrupadorMovimentacaoDto> listRevogacaoesBySujeitoPassivoProcurador(Integer idPessoa) throws SQLException;
	public List<AgrupadorMovimentacaoDto> findSubstabelecimento(Integer idPessoa);
	List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivo(String numrCnpjBase);
	List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivo(Integer idPessoa);
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorProcurador(Integer idPessoaProcurador);
}
