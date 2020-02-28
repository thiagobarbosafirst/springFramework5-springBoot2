package br.gov.go.sefaz.pat.procuracao.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.security.dto.PessoaAutenticadaDto;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.filter.ProcessSearchFilter;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;

@Service
public class AgrupadorMovimentacaoService {
	
	private static Logger logger = LogManager.getLogger(AgrupadorMovimentacaoService.class);

	@Autowired
	private PessoaJuridicaService pessoaJuridicaService;
	
	@Autowired
	private PessoaFisicaService pessoaFisicaService;
		
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	public ModelAndView listarPorSujeitoPassivo(ProcessSearchFilter pesquisa, ModelAndView mv) {
		try{
			List<AgrupadorMovimentacaoDto> lista = null;
			PessoaJuridica pessoaJuridica = null;
			PessoaFisica pessoaFisica = null;
			
			if(pesquisa != null && pesquisa.getTexto() != null){
				if(pesquisa.getTexto().length() == 14) {
					pessoaJuridica = pessoaJuridicaService.consultarPorCnpj(pesquisa.getTexto());
					mv.addObject("idSujeitoPassivo", pessoaJuridica.getIdPessoa());
					lista = movimentacaoProcuracaoService.listarProcuracaoSujeitoPassivoPJ(pesquisa.getTexto().substring(0,8));
					mv.addObject("pessoaJuridica", PessoaAutenticadaDto.PESSOA_JURIDICA);
				} else {
					pessoaFisica = pessoaFisicaService.consultarPorCpf(pesquisa.getTexto());
					mv.addObject("idSujeitoPassivo", pessoaFisica.getIdPessoa());
					lista = movimentacaoProcuracaoService.listarProcuracaoSujeitoPassivo(pessoaFisica.getIdPessoa());
					mv.addObject("pessoaFisica", PessoaAutenticadaDto.PESSOA_FISICA);
				}
				
				if(lista.size() == 0) throw new SefazValidationException("Nenhuma procuração encontrada.");
 				if(pessoaFisica == null && pessoaJuridica == null){
 					throw new SefazValidationException("Não encontramos registros no nosso banco de dados com esse CPF/CNPJ");
 				}
				
				mv.addObject("listaProcuracao", lista);
			}
			
		}catch (Exception e) {
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
		}
		return mv;
	}
	
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivo(String cpfCnpj) throws SefazValidationException{
		
		if(cpfCnpj.length() == 14) {
			return listarProcuracaoPorSujeitoPassivoPJ(cpfCnpj.substring(0,8));
		} else {
			return listarProcuracaoPorSujeitoPassivoPF(cpfCnpj);
		}
			
	}
	
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivoPJ(String cnpjBase) throws SefazValidationException{
		
		try{
			List<AgrupadorMovimentacaoDto> lista = null;
			
			lista = movimentacaoProcuracaoService.listarProcuracaoSujeitoPassivoPJ(cnpjBase);
			
			if(lista == null || lista.size() == 0) { 
				throw new SefazValidationException("Nenhuma procuração encontrada.");
			}			
			
			return lista;
			
		}catch (Exception e) {
			throw new SefazValidationException(e.getMessage());  
		}
		
	}
	
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorSujeitoPassivoPF(String cpf) throws SefazValidationException{
		
		try{
			List<AgrupadorMovimentacaoDto> lista = null;
			
			PessoaFisica pessoaFisica = pessoaFisicaService.consultarPorCpf(cpf);
			lista = movimentacaoProcuracaoService.listarProcuracaoSujeitoPassivo(pessoaFisica.getIdPessoa());
			
			if(lista == null || lista.size() == 0) { 
				throw new SefazValidationException("Nenhuma procuração encontrada.");
			}			
			
			return lista;
		}catch (SefazValidationException e) {
			throw new SefazValidationException(e.getMessage()); 	
		}catch (Exception e) {
			throw new SefazValidationException("Ocorreu um erro ao consultar as procurações."); 
		}
		
	}
	
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorProcurador(String cpf) throws SefazValidationException{
		
			List<AgrupadorMovimentacaoDto> lista = null;
			PessoaFisica pessoaFisica = pessoaFisicaService.consultarPorCpf(cpf);
			return lista = listarProcuracaoPorProcurador(pessoaFisica.getIdPessoa());
		
	}
	
	public List<AgrupadorMovimentacaoDto> listarProcuracaoPorProcurador(Integer idPessoaFisicaProcurador) throws SefazValidationException{
		
		try{
			logger.debug("AgrupadorMovimentacaoService.listarProcuracaoPorProcurador antes de chamar listarProcuracaoPorProcurador" + idPessoaFisicaProcurador);
			List<AgrupadorMovimentacaoDto> lista = null;			
			lista = movimentacaoProcuracaoService.listarProcuracaoPorProcurador(idPessoaFisicaProcurador);
			logger.debug("AgrupadorMovimentacaoService.listarProcuracaoPorProcurador depois de chamar listarProcuracaoPorProcurador" + idPessoaFisicaProcurador);
			if(lista == null || lista.size() == 0) { 
				throw new SefazValidationException("Nenhuma procuração encontrada.");
			}			
			
			return lista;
		}catch (SefazValidationException e) {
			throw new SefazValidationException(e.getMessage()); 
		}catch (Exception e) {
			throw new SefazValidationException("Ocorreu um erro ao consultar as procurações."); 
		}
		
	}
	
	
}
