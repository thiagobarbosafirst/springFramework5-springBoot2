package br.gov.go.sefaz.pat.procuracao.support;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.pat.constants.ConstantesJasper;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.ProcessoAdministrativoTributarioEletronico;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuradorService;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;

@Component
public class RelatorioRevogacaoPdf {
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	private PatSujeitoPassivoProcessoService patSujeitoPassivoProcessoService;
	
	@Autowired
	private ProcuradorService procuradorService;
	
	@Autowired
	private RecuperadorPath recuperadorPath;
	
	/*
	 * Constantes
	 */	
	public static final String tituloMinuta					   	= 	"[Minuta] Termo de Revogação da Procuração";
	public static final String tituloTermo					    = 	"Termo de Revogação da Procuração";
	
	private Map<String, Object> parametrosRelatorio = new HashMap<String, Object>();
	private FormatSupport formatSupport = new FormatSupport();
	
	private void parametrosCabecalho() {
		parametrosRelatorio.put(ConstantesJasper.parametroLogo, recuperadorPath.recuperarPath(ConstantesJasper.caminhoLogoSefaz));
		parametrosRelatorio.put(ConstantesJasper.parametroHeader, recuperadorPath.recuperarPath(ConstantesJasper.caminhoHeaderJasper));
		parametrosRelatorio.put(ConstantesJasper.paramentroFooter, recuperadorPath.recuperarPath(ConstantesJasper.caminhoFooterJasper));
	}
	
	private void parametrosRelatorio(String sujeitopassivosEProcuradores, String processos, String dataExtenso, String titulo) {
		parametrosRelatorio.put("sujeitopassivosEProcuradores", sujeitopassivosEProcuradores);
		parametrosRelatorio.put("dataAtual", dataExtenso);
		parametrosRelatorio.put("processos", processos);
		parametrosRelatorio.put("titulo", titulo);
	}			
	
	public Map<String, Object> parametrosTermoDeRevogacao(RevogacaoProcuracao revogacao) throws SQLException, SefazException {
		
		String processos 							= null;
		String dataExtenso 							= "";
		String cnpjMF 								= ", CNPJ/MF sob nº ";
		String cpfMF 								= ", CPF/MF sob nº ";
		int cnt 								    = 0;
		StringBuilder textoRelatorio = new StringBuilder("");
		
		List<MovimentacaoProcuracao> listaMovimentacoes = movimentacaoProcuracaoService.findByRevogacao(revogacao.getId());
		
		try {
			agrupadorMinutaTermo(cnpjMF, cpfMF, cnt, textoRelatorio, listaMovimentacoes);			
	
			processos = agrupadorProcessos(listaMovimentacoes);
			dataExtenso = "Goiânia, " + formatSupport.retornarDataFormatada(); 		
			
			parametrosCabecalho();
			parametrosRelatorio(textoRelatorio.toString(), processos, dataExtenso, tituloTermo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return parametrosRelatorio;
	}
	
	public Map<String, Object> parametrosMinuta(Integer[] detalhesRevogacao){
		
		String processos 							= null;
		String dataExtenso 							= "";
		String cnpjMF 								= ", CNPJ/MF sob nº ";
		String cpfMF 								= ", CPF/MF sob nº ";
		int cnt 								    = 0;
		StringBuilder textoRelatorio = new StringBuilder("");
		
		List<MovimentacaoProcuracao> listaMovimentacoes = movimentacaoProcuracaoService.findByIdIn(detalhesRevogacao);
		try {
			agrupadorMinutaTermo(cnpjMF, cpfMF, cnt, textoRelatorio, listaMovimentacoes);			
	
			processos = agrupadorProcessos(listaMovimentacoes);
			dataExtenso = "Goiânia, " + formatSupport.retornarDataFormatada(); 		
			
			parametrosCabecalho();
			parametrosRelatorio(textoRelatorio.toString(), processos, dataExtenso, tituloMinuta);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return parametrosRelatorio;
	}
	/**
	 * Agrupa sujeito passivos e procuradores por lista de movimentações para gerar arquivo pdf minuta e termo de revogação
	 * @param cnpjMF
	 * @param cpfMF
	 * @param cnt
	 * @param textoRelatorio
	 * @param listaMovimentacoes
	 */
	private void agrupadorMinutaTermo(String cnpjMF, String cpfMF, int cnt, StringBuilder textoRelatorio,
			List<MovimentacaoProcuracao> listaMovimentacoes) {
		
		List<Integer> listaDistinctId = listaMovimentacoes.stream().map(e->e.getSujeitoPassivoProcesso().getId())
				.distinct()
				.collect(Collectors.toList());				
		
		for (Integer idSujeitoPassivo : listaDistinctId) {
			 SujeitoPassivoProcesso sujeitoPassivo = patSujeitoPassivoProcessoService.find(idSujeitoPassivo);
			 List<MovimentacaoProcuracao> movimentacoesSujeitoPassivo = listaMovimentacoes.stream().filter(m->m.getSujeitoPassivoProcesso().getId().equals(idSujeitoPassivo)).collect(Collectors.toList());
			 String agrupadorProcurador = agrupadorProcurador(movimentacoesSujeitoPassivo);
			 if(cnt == 0)
				 textoRelatorio.append("Pelo presente instrumento particular de revogação, o outorgante ");
			 if(cnt >0 )
				 textoRelatorio.append(" e o outorgante ");
			 if (sujeitoPassivo.getPessoa().getTipoPessoa() == 'F')
				 textoRelatorio.append(sujeitoPassivo.getPessoa().getPessoaFisica().getNome() + cpfMF + formatSupport.formatarCPF(sujeitoPassivo.getPessoa().getPessoaFisica().getNumeroCpf()));
			 else
				 textoRelatorio.append(sujeitoPassivo.getPessoa().getPessoaJuridica().getNomeFantasia() + cnpjMF +  formatSupport.formatarCNPJ(sujeitoPassivo.getPessoa().getPessoaJuridica().getNumeroCnpj()));
			 textoRelatorio.append(" revogo os poderes que foram concedidos ao(s) " + agrupadorProcurador);
			 
			 cnt++;
		}
	}
	
	private String agrupadorProcessos(List<MovimentacaoProcuracao> listaMovimentacoes) {
		Integer contador = 0;
		List<Integer> processosList = new ArrayList<Integer>();
		
		StringBuilder processos = new StringBuilder();
				
		for(MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacoes) {
			
			SujeitoPassivoProcesso sujeitoPassivoProcesso = patSujeitoPassivoProcessoService.find(movimentacaoProcuracao.getSujeitoPassivoProcesso().getId());
			ProcessoAdministrativoTributarioEletronico pate = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico();
								
			if(processosList.indexOf(pate.getIdProcessoAdministrativoTributarioEletronico()) == -1){
				processos.append(formatSupport.formatarPAT(pate.getTipoDocumentoOrigem(), 
						pate.getNumeroSequencial(), pate.getNumeroDigitoVerificador()));
				processosList.add(pate.getIdProcessoAdministrativoTributarioEletronico());
			}
			contador++;
		}
		return processos.toString();
	}
	
	private String agrupadorProcurador(List<MovimentacaoProcuracao> listaMovimentacoes) {	
		String cpfMF 								= ", CPF/MF sob nº ";
		Integer contador = 0;		
		StringBuilder procurador = new StringBuilder();
				
		for(MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacoes) {
			
			Procurador getProcurador = procuradorService.consultar(movimentacaoProcuracao.getProcurador().getId());
			
			if(contador > 0 && (contador + 1) == listaMovimentacoes.size()) {
				procurador.append(" e ");
			}
			else if(contador > 0) {
				procurador.append(", ");
			}
			
			procurador.append(getProcurador.getPessoa().getPessoaFisica().getNome() + cpfMF + formatSupport.formatarCPF(getProcurador.getPessoa().getPessoaFisica().getNumeroCpf()));
			contador++;		
		}									
		return procurador.toString();
	}

}