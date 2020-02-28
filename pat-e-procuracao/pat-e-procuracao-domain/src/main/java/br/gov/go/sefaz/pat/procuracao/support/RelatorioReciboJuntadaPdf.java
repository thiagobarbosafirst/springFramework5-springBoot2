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
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;
import br.gov.go.sefaz.pat.procuracao.service.ProcuradorService;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;

@Component
public class RelatorioReciboJuntadaPdf {
	
	@Autowired
	private PatSujeitoPassivoProcessoService patSujeitoPassivoProcessoService;
	
	@Autowired
	private ProcuradorService procuradorService;
	
	@Autowired
	private RecuperadorPath recuperadorPath;
	
	private Map<String, Object> parametrosRelatorio = new HashMap<String, Object>();
	private FormatSupport formatSupport = new FormatSupport();
	
	private void parametrosCabecalho() {
		parametrosRelatorio.put(ConstantesJasper.parametroLogo, recuperadorPath.recuperarPath(ConstantesJasper.caminhoLogoSefaz));
		parametrosRelatorio.put(ConstantesJasper.parametroHeader, recuperadorPath.recuperarPath(ConstantesJasper.caminhoHeaderJasper));
		parametrosRelatorio.put(ConstantesJasper.paramentroFooter, recuperadorPath.recuperarPath(ConstantesJasper.caminhoFooterJasper));
	}
	
	public Map<String, Object> parametrosReciboDeJuntada(String dadosUsuarioLogado, List<MovimentacaoProcuracao> listaMovimentacoes, String peca, String tituloRecibo) throws SQLException, SefazException{
		
		List<String> sujeitoPassivos = new ArrayList<String>();
		List<String> procuradores = new ArrayList<String>();
		List<String> processos = new ArrayList<String>();
		List<String> pecas = new ArrayList<String>();
		try {
			sujeitoPassivos.add(agrupadorSujeitoPassivo(listaMovimentacoes));
			procuradores.add(agrupadorProcurador(listaMovimentacoes));
			processos.add(agrupadorProcessos(listaMovimentacoes));
			pecas.add(peca);
			
			parametrosCabecalho();
			parametrosRelatorio.put("dataHora", formatSupport.retornarDataFormatada()); 
			parametrosRelatorio.put("sujeitos", sujeitoPassivos);
			parametrosRelatorio.put("procurador", procuradores);
			parametrosRelatorio.put("processos", processos);
			parametrosRelatorio.put("pecas", pecas);
			parametrosRelatorio.put("usuario", dadosUsuarioLogado);
			parametrosRelatorio.put("titulo", tituloRecibo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return parametrosRelatorio;
	}
	
	private String agrupadorSujeitoPassivo(List<MovimentacaoProcuracao> listaMovimentacoes) {
		String cnpjMF 								= ", CNPJ/MF sob nº ";
		String cpfMF 								= ", CPF/MF sob nº ";
		int contador = 0;
		StringBuilder sujeitoPassivo = new StringBuilder();																	
		
		List<Integer> listaDistinctId = listaMovimentacoes.stream().map(e->e.getSujeitoPassivoProcesso().getId())
				.distinct()
				.collect(Collectors.toList());
															
		for(Integer idSujeitoPassivo : listaDistinctId){ 
			SujeitoPassivoProcesso sujeitoPassivoProcesso = patSujeitoPassivoProcessoService.find(idSujeitoPassivo);
			if(contador > 0 && (contador + 1) == listaDistinctId.size()) {
					sujeitoPassivo.append(" e ");
				}
				else if(contador > 0) {
					sujeitoPassivo.append(", ");
				}
						
				if (sujeitoPassivoProcesso.getPessoa().getTipoPessoa() == 'F') 
					sujeitoPassivo.append(sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNome() + cpfMF + formatSupport.formatarCPF(sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNumeroCpf()));
				else
					sujeitoPassivo.append(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNomeFantasia() + cnpjMF +  formatSupport.formatarCNPJ(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNumeroCnpj()));
			contador++;	
			}
		return sujeitoPassivo.toString();
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