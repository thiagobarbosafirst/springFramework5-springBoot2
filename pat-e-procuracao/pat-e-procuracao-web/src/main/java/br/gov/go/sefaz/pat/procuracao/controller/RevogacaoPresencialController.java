package br.gov.go.sefaz.pat.procuracao.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.security.dto.PessoaAutenticadaDto;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.ModeloDocumentoService;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.filter.ProcessSearchFilter;
import br.gov.go.sefaz.pat.model.ModeloDocumento;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.service.AgrupadorMovimentacaoService;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.RevogacaoService;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoSupport;
import br.gov.go.sefaz.pat.procuracao.validation.input.ProcuracaoInputValidator;
import br.gov.go.sefaz.pat.procuracao.validation.input.RevogacaoInputValidator;

@Controller
@RequestMapping("/revogacaoPresencial")
public class RevogacaoPresencialController {

	private static Logger logger = LogManager.getLogger(RevogacaoPresencialController.class);
	
	@Autowired
	private RevogacaoService revogacaoService;
	
	@Autowired
	private ProcuracaoSupport revogacaoSupport; 
	
	@Autowired
	private RevogacaoInputValidator revogacaoInputValidator;
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	@Autowired
	private ProcuracaoInputValidator procuracaoInputValidator;

	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	private ModeloDocumentoService modeloDocumentoService;
	
	@Autowired
	private PessoaFisicaService pessoaFisicaService;
	
	@Autowired
	private AgrupadorMovimentacaoService agrupadorMovimentacaoService;
	
	@GetMapping("/search")
	public ModelAndView pesquisarProcuracao(ProcessSearchFilter pesquisa, RedirectAttributes redir) throws SefazException, SefazValidationException {	
		ModelAndView mv = new ModelAndView("revogacaoPresencial/revogacaoPresencial-search");
		
		if(pesquisa != null && pesquisa.getTexto() != null) {
			Integer idSujeitoPassivo = revogacaoSupport.consultaIdPessoaSujeitoPassivo(pesquisa);
			
			if(idSujeitoPassivo == null) {
				boolean idSP = false;
				mv.addObject("idSujeitoPassivo", idSP); 
			}
			else {
				mv.addObject("idSujeitoPassivo", idSujeitoPassivo);
				redir.addFlashAttribute("idSujeitoPassivo", idSujeitoPassivo);					
			}
		}
		
		return agrupadorMovimentacaoService.listarPorSujeitoPassivo(pesquisa, mv);
	}	

	@GetMapping("/{idProcuracao}/{idSujeitoPassivo}")
	public ModelAndView listarProcessosPorProcuracao(@PathVariable Integer idProcuracao, @ModelAttribute("idSujeitoPassivo") Integer idSujeitoPassivo) throws SefazException {

		ModelAndView mv = new ModelAndView("revogacaoPresencial/revogacaoPresencial-form");
		EnumModeloDocumentEcm[] modelos = {EnumModeloDocumentEcm.REVOGACAO_DA_PROCURACAO, EnumModeloDocumentEcm.ANEXO_DA_REVOGACAO_DA_PROCURACAO}; 
		try {			
			Procuracao procuracao = procuracaoService.find(idProcuracao);
			
			List<AgrupadorMovimentacaoDto> listaProcessosByProcuracao = movimentacaoProcuracaoService.buscaProcuracaoSujeitoPassivoProcurador(idProcuracao);
			List<ModeloDocumento> modelosDeDocumento = modeloDocumentoService.findByEnumModelo(Arrays.asList(modelos));
			
			Boolean revogacaoNula = revogacaoService.revogacaoNula(idProcuracao);
			revogacaoInputValidator.validateRevogacaoNula(revogacaoNula);
						
			mv.addObject("procuracao", procuracao);	
			mv.addObject("idSujeitoPassivo", idSujeitoPassivo);
			mv.addObject("processoByRevogacao", listaProcessosByProcuracao);
			mv.addObject("modelosDeDocumento", modelosDeDocumento);
			return mv;
		}catch (Exception e) {
			logger.error(e);
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
			return mv;
		}	
	}
	
	@GetMapping("/list")
	public ModelAndView listaRevogacoes(ProcessSearchFilter pesquisa) throws SefazException {	
		ModelAndView mv = new ModelAndView("revogacaoPresencial/revogacaoPresencial-list");
		try{
			List<AgrupadorMovimentacaoDto> lista = null;			
			Boolean existeRevogacoes = null;

			if(pesquisa != null && pesquisa.getTexto() != null){
				if(!pesquisa.getTexto().isEmpty() && pesquisa.getTexto().length() == 14) {
					String cnpjBase = pesquisa.getTexto().substring(0,8);
					lista = movimentacaoProcuracaoService.listaRevogacoesSujeitoPassivoProcurador(cnpjBase);
					existeRevogacoes = revogacaoService.existeRevogacoes(cnpjBase);
					mv.addObject("pessoaJuridica", PessoaAutenticadaDto.PESSOA_JURIDICA);
				}
				else {
					Integer idPessoaFisica = null;
					try {
						 idPessoaFisica = pessoaFisicaService.consultarPorCpf(pesquisa.getTexto()).getIdPessoa();						
					} catch (Exception e) {
						throw new Exception("Não encontramos registros no nosso banco de dados com esse CPF.");
					}
					lista = movimentacaoProcuracaoService.listaRevogacoesSujeitoPassivoProcurador(idPessoaFisica);
					existeRevogacoes = revogacaoService.existeRevogacoes(idPessoaFisica);
					mv.addObject("pessoaFisica", PessoaAutenticadaDto.PESSOA_FISICA);
				}

				revogacaoInputValidator.validateListaRevogacoes(existeRevogacoes);			
				mv.addObject("revogacoes", lista);
				mv.addObject("existeRevogacoes", existeRevogacoes);
				
				return mv;
			}							
		}catch (Exception e) {
			logger.error(e);
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
		}
		return mv;
	}	
	
	@PostMapping("save_presencial")
	public @ResponseBody String save(@ModelAttribute AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto,
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, HttpServletRequest request, BindingResult result){
		
		String redirect = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()  + "/revogacaoPresencial/list";
		Integer usuarioHistorico = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
		Integer usuarioAutor = agrupadorMovimentacaoPresencialDto.getIdSujeitoPassivo();
		Integer[] detalhesMovimentacaoConverter = new Integer[agrupadorMovimentacaoPresencialDto.getMovimentacoes().size()]; 
		detalhesMovimentacaoConverter = agrupadorMovimentacaoPresencialDto.getMovimentacoes().toArray(detalhesMovimentacaoConverter);
				
		try {			
			redirect = revogacaoSupport.filtroRedirectPessoaFisicaJuridica(agrupadorMovimentacaoPresencialDto, redirect);
			
			procuracaoInputValidator.validateItens(agrupadorMovimentacaoPresencialDto);
			procuracaoInputValidator.validateDocumentos(agrupadorMovimentacaoPresencialDto);
			revogacaoInputValidator.validateSave(detalhesMovimentacaoConverter, result);
			
			RevogacaoProcuracao revogacao = revogacaoService.prepararRevogacaoPresencial();
			List<PecaEletronicaDto> listaPecaEletronicaDto = revogacaoService.criarPecasRevogacao(usuarioHistorico, usuarioAutor, revogacao, agrupadorMovimentacaoPresencialDto);
			
			revogacaoService.salvarRevogacaoPresencial(usuarioHistorico, usuarioAutor, revogacao, listaPecaEletronicaDto, agrupadorMovimentacaoPresencialDto);
			logger.debug(String.format("Save Presencial ->  Revogação Presencial salva com sucesso!"));
			return redirect;		
		} catch (Exception e) {
			logger.error(e);
			throw new AjaxRequestException(e.getLocalizedMessage());
		}				
	}
	
}
