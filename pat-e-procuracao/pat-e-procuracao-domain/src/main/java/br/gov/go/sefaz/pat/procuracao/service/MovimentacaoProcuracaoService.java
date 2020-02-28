package br.gov.go.sefaz.pat.procuracao.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.MovimentacaoProcuracaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.MovimentacaoProcuracaoJpaRepository;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;

@Service
public class MovimentacaoProcuracaoService {
	
	@Autowired
	private MovimentacaoProcuracaoJpaRepository movimentacaoProcuracaoJpaRepository;
	
	@Autowired
	private MovimentacaoProcuracaoJdbcRepository movimentacaoProcuracaoJdbcRepository;
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	@Autowired
	private ProcuradorService procuradorService;
	
	@Autowired
	private PatSujeitoPassivoProcessoService patSujeitoPassivoProcessoService;
	
	public MovimentacaoProcuracao find(Integer id) {
		return this.movimentacaoProcuracaoJpaRepository.findOne(id);
	}
	
	public List<MovimentacaoProcuracao> findByIdIn(Integer[] ids) {
		return this.movimentacaoProcuracaoJpaRepository.findByIdIn(ids); 
	}
	
	public List<MovimentacaoProcuracao> findByPessoa(Integer idPessoa){
		return this.movimentacaoProcuracaoJdbcRepository.findByPessoa(idPessoa);
	}
	
	public List<MovimentacaoProcuracao> findByPessoa(String cnpjbase){
		return this.movimentacaoProcuracaoJdbcRepository.findByPessoa(cnpjbase);
	}
	
	public List<MovimentacaoProcuracao> findRevogacao(Integer idRevogacao){
		return this.movimentacaoProcuracaoJdbcRepository.findByRevogacao(idRevogacao);
	}
	
	public List<MovimentacaoProcuracao> findProcuracao(Integer idProcuracao) {
		return this.movimentacaoProcuracaoJdbcRepository.findByProcuracao(idProcuracao);
	}	
	
	@HistoryJpa
	@Transactional
	public void saveAndFlush(Integer usuarioHistorico, MovimentacaoProcuracao movimentacaoProcuracao) {
		movimentacaoProcuracaoJpaRepository.saveAndFlush(movimentacaoProcuracao);
	}
	
	@HistoryJpa
	@Transactional
	public void saveAndFlush(MovimentacaoProcuracao movimentacaoProcuracao) {
		movimentacaoProcuracaoJpaRepository.saveAndFlush(movimentacaoProcuracao);
	}
	
	@HistoryJpa
	@Transactional
	public void save(Integer usuarioHistorico, MovimentacaoProcuracao movimentacaoProcuracao) {
		movimentacaoProcuracaoJpaRepository.save(movimentacaoProcuracao);
	}
	
	public List<MovimentacaoProcuracao> findByProcesso(Integer idProcesso) {
		return this.movimentacaoProcuracaoJpaRepository.findBySujeitoPassivoProcessoProcessoAdministrativoTributarioEletronicoIdProcessoAdministrativoTributarioEletronico(idProcesso);
	}	
	
	public List<MovimentacaoProcuracao> findByProcuracao(Integer idProcuracao) {
		return this.movimentacaoProcuracaoJpaRepository.findByProcuracaoId(idProcuracao);
	}
	
	public List<MovimentacaoProcuracao> findByProcuracao(Integer idProcuracao, Character indice) {
		return this.movimentacaoProcuracaoJpaRepository.findByProcuracaoIdAndIndiMovmtProcuracaoAtivaAsChar(idProcuracao, indice);
	}
	
	public List<MovimentacaoProcuracao> findBySubstabelecimento(Integer idSubstabelecimento) {
		return this.movimentacaoProcuracaoJpaRepository.findBySubEstabelecimentoProcuracaoId(idSubstabelecimento);
	}
	
	public List<MovimentacaoProcuracao> findByRevogacao(Integer idRevogacao) {
		return this.movimentacaoProcuracaoJpaRepository.findByRevogacaoProcuracaoId(idRevogacao);
	}
	/**
	 * Busca procuração
	 * @param idProcuracao
	 * @return lista de sujeito passivo e procuradores
	 * @throws SQLException
	 */
	public List<AgrupadorMovimentacaoDto> buscaProcuracaoSujeitoPassivoProcurador(Integer idProcuracao) throws SQLException{
		return movimentacaoProcuracaoJdbcRepository.findProcuracaoBySujeitoPassivoProcurador(idProcuracao);
	}
	/**
	 * Lista revagções sujeito passivo e procuradores
	 * @param numeroCnpjBase
	 * @return
	 * @throws SQLException
	 */
	public List<AgrupadorMovimentacaoDto> listaRevogacoesSujeitoPassivoProcurador(String numeroCnpjBase) throws SQLException{
		return movimentacaoProcuracaoJdbcRepository.listRevogacaoesBySujeitoPassivoProcuradorPJ(numeroCnpjBase);
	}
	
	/**
	 * Lista revagções por sujeito passivo e procuradores
	 * @param idPessoa
	 * @return
	 * @throws SQLException
	 */
	public List<AgrupadorMovimentacaoDto> listaRevogacoesSujeitoPassivoProcurador(Integer idPessoa) throws SQLException{
		return movimentacaoProcuracaoJdbcRepository.listRevogacaoesBySujeitoPassivoProcurador(idPessoa);
	}
		
	public List<SujeitoPassivoProcesso> listarSujeitoPassivoSemProcuracaoAtiva(String numrCnpjBase) {
		List<SujeitoPassivoProcesso> lista = patSujeitoPassivoProcessoService.listarSujeitoPassivoPorProcessos(numrCnpjBase);
		List<SujeitoPassivoProcesso> listaSujeitoPassivoRetorno = new ArrayList<SujeitoPassivoProcesso>();
		for (SujeitoPassivoProcesso sujeitoPassivoProcesso : lista) {
			if(!procuracaoService.verificarProcuracao(sujeitoPassivoProcesso.getId(), EnumStatusProcuracao.Ativa.getCodigo())) {
				if(!procuracaoService.verificarProcuracao(sujeitoPassivoProcesso.getId(), EnumStatusProcuracao.PendenteAssinatura.getCodigo())) {
					listaSujeitoPassivoRetorno.add(sujeitoPassivoProcesso);  
				}
			}
		}
		
		return listaSujeitoPassivoRetorno;
	}
	
	public List<SujeitoPassivoProcesso> listarSujeitoPassivoPFSemProcuracaoAtiva(Integer idPessoa) {
		List<SujeitoPassivoProcesso> lista = patSujeitoPassivoProcessoService.listarSujeitoPassivoPorProcessosPF(idPessoa);
		List<SujeitoPassivoProcesso> listaSujeitoPassivoRetorno = new ArrayList<SujeitoPassivoProcesso>();
		for (SujeitoPassivoProcesso sujeitoPassivoProcesso : lista) {
			if(!procuracaoService.verificarProcuracao(sujeitoPassivoProcesso.getId(), EnumStatusProcuracao.Ativa.getCodigo())) {
				if(!procuracaoService.verificarProcuracao(sujeitoPassivoProcesso.getId(), EnumStatusProcuracao.PendenteAssinatura.getCodigo())) {
					listaSujeitoPassivoRetorno.add(sujeitoPassivoProcesso);
				}
			}
		} 
		
		return listaSujeitoPassivoRetorno;
	}
	
	public List<MovimentacaoProcuracao> findByMovimentacaoProcuracaoByRenunciaId(Integer idRenuncia) {
		return movimentacaoProcuracaoJpaRepository.findByRenunciaProcuracaoId(idRenuncia);
	}	 
	
	/**
	 * Consultar itens para substabelecer. Processos, sujeitos e procurações. 
	*/
	public List<AgrupadorMovimentacaoDto> findSubstabelecerByIdPessoaProcuradorAndProcuracao(Integer idPessoa, Integer idProcuracao) {
		return this.movimentacaoProcuracaoJdbcRepository.findSubstabelecerByIdPessoaProcuradorAndProcuracao(idPessoa, idProcuracao); 
	}
	
	/**
	 * Consultar lista substabelecimento
	*/
	public List<AgrupadorMovimentacaoDto> findSubstabelecimento(Integer idPessoa) {
		return this.movimentacaoProcuracaoJdbcRepository.findSubstabelecimento(idPessoa); 
	}
	/**
	 * Consulta lista de procurações
	 * @param idPessoa
	 * @return
	 */
	public List<AgrupadorMovimentacaoDto> listarProcuracaoSujeitoPassivo(Integer idPessoa) {
		return this.movimentacaoProcuracaoJdbcRepository.listarProcuracaoPorSujeitoPassivo(idPessoa); 
	}
	/**
	 * Consulta lista de procurações
	 * @param numrCnpjBase
	 * @return
	 */
	public List<AgrupadorMovimentacaoDto> listarProcuracaoSujeitoPassivoPJ(String numrCnpjBase) {
		return this.movimentacaoProcuracaoJdbcRepository.listarProcuracaoPorSujeitoPassivo(numrCnpjBase);
	} 
	
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorProcurador(Integer idPessoaProcurador) {
		return this.movimentacaoProcuracaoJdbcRepository.listarProcuracaoPorProcurador(idPessoaProcurador);
	}
	
}