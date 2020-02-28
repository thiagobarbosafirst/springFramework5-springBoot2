package br.gov.go.sefaz.pat.procuracao.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.DocumentException;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticado;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.enumerator.EnumStatusAssinaturaPeca;
import br.gov.go.sefaz.pat.enumerator.EnumTipoAssinantePeca;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaRevogacaoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.RevogacaoService;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoSupport;
import br.gov.go.sefaz.pat.procuracao.validation.input.RevogacaoInputValidator;
import br.gov.go.sefaz.pat.service.AssinantePecaService;
import br.gov.go.sefaz.pat.service.PatSignatureService;
import br.gov.go.sefaz.pat.service.PatUploadService;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping("/revogacao")
public class RevogacaoController {
	
	private static Logger logger = LogManager.getLogger(RevogacaoController.class);
	
	@Autowired
	private RevogacaoService revogacaoService;
	
	@Autowired
	private ProcuracaoSupport revogacaoSupport;
	
	@Autowired
	private PecaRevogacaoService pecaRevogacaoService;
	
	@Autowired
	private ProcuracaoService procuracaoService;

	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PatUploadService patUploadService;
	
	@Autowired
	private PatSignatureService patSignatureService;
	
	@Autowired
	private AssinantePecaService assinantePecaService;
	
	@Autowired
	private RevogacaoInputValidator revogacaoInputValidator;

	@GetMapping("/{idProcuracao}")
	public ModelAndView listarProcessosByProcuracao(@PathVariable Integer idProcuracao,
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws SefazException {

		ModelAndView mv = null;
		try {			
			mv = new ModelAndView(ControllerMappingConfigProcuracao.REVOGACAO_PROCURACAO_VIEW_FORM);
			Procuracao procuracao = procuracaoService.find(idProcuracao);
			List<AgrupadorMovimentacaoDto> listaProcessosByProcuracao = movimentacaoProcuracaoService.buscaProcuracaoSujeitoPassivoProcurador(idProcuracao);
			Boolean revogacaoNula = revogacaoService.revogacaoNula(idProcuracao);
			
			mv.addObject("procuracao", procuracao);			
			mv.addObject("processoByRevogacao", listaProcessosByProcuracao);
			mv.addObject("revogacaoNula", revogacaoNula);		
			return mv;					
		}catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu alguma problema ao selecionar a procuração. " + e.getMessage());
		}	
	}
	
	@GetMapping("/list")
	public ModelAndView listarRevogacoes(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws SefazException {
		ModelAndView mv = null;
		List<AgrupadorMovimentacaoDto> listaRevogacoes = null;
		Boolean existeRevogacoes = null;
		UsuarioAutenticado usuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado();
		Character tipoPessoa = revogacaoSupport.consultarTipoPessoa(usuarioAutenticado.getIdPessoa());
		try {
			mv = new ModelAndView("revogacao/revogacao-list");
						
			if(tipoPessoa == 'J'){
				String cnpjBase = revogacaoSupport.consultarPessoaJuridica(usuarioAutenticado.getIdPessoa()).getNumeroCnpj().substring(0,8);
				listaRevogacoes = movimentacaoProcuracaoService.listaRevogacoesSujeitoPassivoProcurador(cnpjBase);
				existeRevogacoes = revogacaoService.existeRevogacoes(cnpjBase);
			}
			else{
				Integer idPessoaFisica = revogacaoSupport.consultarPessoFisica(usuarioAutenticado.getIdPessoa()).getIdPessoa();
				listaRevogacoes = movimentacaoProcuracaoService.listaRevogacoesSujeitoPassivoProcurador(idPessoaFisica);
				existeRevogacoes = revogacaoService.existeRevogacoes(idPessoaFisica);
			}
						
			mv.addObject("existeRevogacoes", existeRevogacoes);			
			mv.addObject("usuarioAutenticadoDetails", usuarioAutenticado.getFromType());
			mv.addObject("revogacoes", listaRevogacoes);
			return mv;
		} catch (Exception e) {
			logger.error(e);
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
			return mv;
		}
		
	}
	
	@PostMapping("/save")
	public ModelAndView save(@RequestParam(name="detalhesRevogacao[]",required=false) Integer[] detalhesRevogacao, @RequestParam("idProcuracao") Integer idProcuracao, RevogacaoProcuracao revogacaoProcuracao, 
			BindingResult result, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws IllegalAccessException, 
	InvocationTargetException, NoSuchMethodException, SefazException, SefazValidationException{
				
		ModelAndView mv = null;
		try{
			this.revogacaoInputValidator.validateSave(detalhesRevogacao, result);
			try {	
				revogacaoService.salvarRevogacaoEletronica(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), detalhesRevogacao, revogacaoProcuracao);
				mv = new ModelAndView("redirect:/revogacao/list");
				return mv;									
			}catch (SefazException se) {
				mv = new ModelAndView("revogacao/revogacao-form");
				mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, se.getLocalizedMessage());
				return mv;					
			}catch (Exception e) {
				mv = new ModelAndView("revogacao/revogacao-form");
				mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
				return mv;
			}			
		}catch (SefazValidationException e){ 
			logger.error(e);
			mv = listarProcessosByProcuracao(idProcuracao, usuarioAutenticadoDetails);
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
		}		
		return mv;
	}
	
	@PostMapping("/minuta") 
	public ModelAndView gerarMinuta(@RequestParam(name="detalhesRevogacao[]", required=false) Integer[] detalhesRevogacao, @RequestParam("idProcuracao") Integer idProcuracao,
			HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws IOException, SefazException, SefazValidationException {		
		
		ModelAndView mv = null;
		try {
			byte[] pdfData = revogacaoService.gerarMinuta(detalhesRevogacao);								
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			response.setHeader("Content-disposition", "attachment; filename=" + "Minuta_Revogação.pdf");
			
			OutputStream outPut = response.getOutputStream();
			outPut.write(pdfData);
			outPut.close();
			
		} catch (Exception e) {
			logger.error(e);
			mv = listarProcessosByProcuracao(idProcuracao, usuarioAutenticadoDetails);		
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
		}
		return mv;
	}	
	/**
	 * Verifica posições do componente wizard
	 * @param usuarioAutenticadoDetails
	 * @param id
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@PostMapping(value="/verifyStep/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer verifyStep(@PathVariable Integer id) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		Integer stepWizard = revogacaoService.checkStepOfWizardRevogacao(id);
		
		if (stepWizard != null)
			return stepWizard;
		else
			throw new AjaxRequestException("Erro ao verificar a etapa da Revogação Procuração.");
	}
	
	@GetMapping("/download/{id}")   
	public void gerarPecaRevogacao(HttpServletRequest request, HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, KeyStoreException, 
					NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		
		PecaRevogacaoProcuracao pecaRevogacao = null;
		PecaEletronicaDto pecaEletronicaDtoTermo = null;
		pecaRevogacao = this.pecaRevogacaoService.consultarPecaRevogacaoPecaEletronica(id, EnumModeloDocumentEcm.REVOGACAO_DA_PROCURACAO.getDescricaoModeloDocumento());
		Integer idUsuarioLogado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
		
		try {
			if(pecaRevogacao == null){								
				pecaEletronicaDtoTermo = revogacaoService.gerarTermoRevogacao(idUsuarioLogado, id);
				pecaRevogacaoService.savePecaRevogacaoAndUcmDocument(idUsuarioLogado, id, pecaEletronicaDtoTermo);				
			} else {
				pecaEletronicaDtoTermo = pecaEletronicaEcmService.consultar(pecaRevogacao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica()); 				
			}
			//envia a resposta com o MIME Type PDF
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			//força a abertura de download
			response.setHeader("Content-disposition", "attachment; filename=" + pecaEletronicaDtoTermo.getNomePecaEletronica());
		}catch (Exception e) {
			logger.error(e);
			throw new AjaxRequestException("Ocorreu um problema ao fazer o download do arquivo. ");
		}		
		OutputStream outPut = response.getOutputStream();
		outPut.write(pecaEletronicaDtoTermo.getContent());
		outPut.close();	
	}
	
	@GetMapping("/reciboJuntada/{id}")   
	public void downloadReciboJuntada(HttpServletResponse response, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, KeyStoreException, 
					NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		
		PecaRevogacaoProcuracao pecaReciboJuntada = null;
		PecaEletronicaDto pecaEletronicaDto = null;		
		pecaReciboJuntada = this.pecaRevogacaoService.consultarPecaRevogacaoPecaEletronica(id, EnumModeloDocumentEcm.TERMO_DE_JUNTADA.getDescricaoModeloDocumento());
		
		try {
			pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaReciboJuntada.getPecaEletronica().getNumeroChaveAcessoPecaEletronica()); 							
			//envia a resposta com o MIME Type PDF
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			//força a abertura de download
			response.setHeader("Content-disposition", "attachment; filename=" + pecaEletronicaDto.getNomePecaEletronica());
		}catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu um problema ao fazer o download recibo de juntada. " + e);
		}		
		OutputStream outPut = response.getOutputStream();
		outPut.write(pecaEletronicaDto.getContent());
		outPut.close(); 		
	}
	
	@PostMapping("/upload/{id}")
	@ResponseStatus(HttpStatus.OK)
    public void upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, 
    		@PathVariable Integer id, HttpServletRequest request) throws AjaxRequestException, IOException, SefazException, DocumentException, 
    IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException {
		
		Integer idUsuarioLogado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
		patUploadService.validarArquivo(file);
		
		PecaRevogacaoProcuracao pecaRevogacao = pecaRevogacaoService.consultarPecaRevogacaoPecaEletronica(id, EnumModeloDocumentEcm.REVOGACAO_DA_PROCURACAO.getDescricaoModeloDocumento());
		
		if(pecaRevogacao != null){			
			PecaEletronicaDto pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaRevogacao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			RevogacaoProcuracao revogacao = pecaRevogacao.getRevogacaoProcuracao();
			List<MovimentacaoProcuracao> listaMovimentacoes = movimentacaoProcuracaoService.findByRevogacao(revogacao.getId());
			
			//validação arquivo PKCS7
			patSignatureService.validarArquivoCades(pecaEletronicaDto.getContent(), file);
			assinantePecaService.saveAssinantePeca(idUsuarioLogado, EnumStatusAssinaturaPeca.ASSINADO_DIGITALMENTE, 
					EnumTipoAssinantePeca.DEFESA, pecaEletronicaDto.getIdPecaEletronica(), file.getBytes());
			
			/*// Validação de certificado
			patSignatureService.validarArquivo(file, pecaEletronicaDto.getHashCode(), context.getRealPath(ConstantesKeyStore.keyStorePathTest));
			//Gerando novo hashcode
			pecaEletronicaDto.setHashCode(Hash.generate(Hash.ALGORITHM_SHA_256, file.getBytes()));
			pecaEletronicaDto.setContent(file.getBytes());*/
			try {
				//Gerando Recibo de Juntada
				PecaEletronicaDto pecaReciboJuntada = procuracaoService.gerarReciboDeJuntada(idUsuarioLogado, idUsuarioLogado, listaMovimentacoes, pecaEletronicaDto.getNomePecaEletronica(), "Recibo de Juntada da Revogação");
				revogacaoService.ativarRevogacao(idUsuarioLogado, id, revogacao, pecaEletronicaDto, pecaReciboJuntada);
			} catch (Exception e) {
				logger.error(e);
				throw new AjaxRequestException("Erro ao salvar a procuração.");
			}			
		} else {
			throw new AjaxRequestException("Não conseguimos identificar o documento gerado, por favor faça download do documento novamente.");
		}					
	}
	
}
