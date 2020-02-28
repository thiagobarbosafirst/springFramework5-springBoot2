package br.gov.go.sefaz.pat.procuracao.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.pagination.PaginationConfig;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.ModeloDocumentoService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.filter.ProcessSearchFilter;
import br.gov.go.sefaz.pat.model.ModeloDocumento;
import br.gov.go.sefaz.pat.model.PecaRenunciaProcuracao;
import br.gov.go.sefaz.pat.model.RenunciaProcuracao;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusAssinaturaDigital;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorRenunciaDTO;
import br.gov.go.sefaz.pat.procuracao.service.PecaRenunciaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.RenunciaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.validation.business.RenunciaBusinessValidator;
import br.gov.go.sefaz.pat.procuracao.validation.input.ProcuracaoInputValidator;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.RENUNCIA_PRESENCIAL_PATH_ROOT)
public class RenunciaPresencialController {	
	
	@Autowired
	PessoaService pessoaService;
	
	@Autowired
	private RenunciaProcuracaoService renunciaProcuracaoService;
				
	@Autowired
	private ModeloDocumentoService modeloDocumentoService;
	
	@Autowired
	private ProcuracaoInputValidator procuracaoInputValidator;
	
	@Autowired
	private RenunciaBusinessValidator renunciaBusinessValidator;
	
	@Autowired
	private PessoaJuridicaService pessoaJuridicaService;
	
	@Autowired
	private PessoaFisicaService pessoaFisicaService;
	
	@Autowired
	private PecaRenunciaProcuracaoService pecaRenunciaProcuracaoService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
		    
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST)
public ModelAndView list(ProcessSearchFilter pesquisa, Pageable pageable,
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws SefazException {	
		
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.RENUNCIA_PRESENCIAL_VIEW_LIST);
		
		try{
			
			List<AgrupadorMovimentacaoDto> list;
			String lastQuery;			
			Pessoa pessoa = null;			
			
			if(pesquisa != null && pesquisa.getTexto() != null){
				
				pessoa = pessoaFisicaService.consultarPorCpf(pesquisa.getTexto());
				String tipoAssinatura = EnumStatusAssinaturaDigital.Assinado.getCodigo();
				
				list = (pessoa != null) ? renunciaProcuracaoService.findRenunciadosByIdPessoaProcuradorAndStatusAssinatura(pessoa.getIdPessoa(),tipoAssinatura)
						:  new ArrayList<AgrupadorMovimentacaoDto>();
				
				lastQuery = pesquisa.getTexto();
			}
			else{
				list = new ArrayList<AgrupadorMovimentacaoDto>();
				lastQuery = "";
			}
			
			mv.addObject("renuncias", list);
			mv.addObject("lastQuery", lastQuery);
						
			return mv;
			
		}catch (Exception e) {
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
		}
		
		return mv;
	}	
	
	@GetMapping(ControllerMappingConfigProcuracao.RENUNCIA_PATH_NEW_PROCURACAO_PRESENCIAL)
	public ModelAndView add(@PathVariable Optional<Integer> idProcuracao, @PathVariable Optional<Integer> idPessoa,
				@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){			
		
		List<AgrupadorMovimentacaoDto> processosRenunciar = null;				
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.RENUNCIA_PRESENCIAL_VIEW_FORM);	
		
		processosRenunciar = (idProcuracao.isPresent()) ? 
				renunciaProcuracaoService.findRenunciarByIdPessoaProcuradorAndProcuracao(idPessoa.get(), idProcuracao.get()) :
				renunciaProcuracaoService.findRenunciarByIdPessoaProcurador(idPessoa.get());
				
		EnumModeloDocumentEcm[] modelos = {EnumModeloDocumentEcm.RENUNCIA_DA_PROCURACAO_ELETRONICA, EnumModeloDocumentEcm.ANEXO_DA_RENUNCIA}; 
		List<ModeloDocumento> modelosDeDocumento = modeloDocumentoService.findByEnumModelo(Arrays.asList(modelos));
				
		mv.addObject("idUsuarioProcurador",(idPessoa.isPresent()) ? idPessoa.get() : null);
		
		mv.addObject("modelosDeDocumento",modelosDeDocumento);
		mv.addObject("renunciaCadastroDTO", new AgrupadorMovimentacaoPresencialDto());
		mv.addObject(PaginationConfig.PAGE_MODEL_NAME, processosRenunciar);		
		return mv;			
	}
	
	@PostMapping(ControllerMappingConfigProcuracao.RENUNCIA_PRESENCIAL_SAVE)
	public @ResponseBody String save(@ModelAttribute  AgrupadorMovimentacaoPresencialDto renuncia,
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, HttpServletRequest request){
		
		procuracaoInputValidator.validateItens(renuncia);
		procuracaoInputValidator.validateDocumentos(renuncia);	
		
		String redirect = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()  + "/renunciaPresencial/list"; 
		
		try {	
			renunciaBusinessValidator.validateSavePresencial(renuncia);
			
			Integer idUsuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
			RenunciaProcuracao renunciaSalva = renunciaProcuracaoService.renunciaPresencial(idUsuarioAutenticado, renuncia, request.getServletContext());
			
			PessoaFisica pessoaProcurador = pessoaFisicaService.consultarPorId(renuncia.getProcurador());
			redirect = redirect + "?opcao=cpf&texto=" + pessoaProcurador.getNumeroCpf();
			
			//Gerar de Juntada de Documentos.
			//Usuario Autenticado != Usuário Autor.
			pecaRenunciaProcuracaoService.saveReciboJuntaDocumentos(request, idUsuarioAutenticado, pessoaProcurador.getIdPessoa(), renunciaSalva.getId());
									
			//TODO: INPLEMENTAR O ENVIO DE COMUNICAÇÃO AO DTE DO SUJEITO PASSIVO SOBRE A RENUNCIA DE PROCURADOR. (Ver Documentação)
			
			return redirect;
						
		} catch (Exception e) {
			throw new AjaxRequestException(e.getMessage());
		}				
	}
	
	@GetMapping("/recibo_juntada/{id}")   
	public @ResponseBody byte[] downloadReciboJuntaDocumentos(HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, 
					KeyStoreException, NoSuchAlgorithmException, IllegalAccessException,InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
						
		PecaEletronicaDto pecaEletronicaDTO = pecaRenunciaProcuracaoService.getReciboJuntaDocumentos(id);		
		
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-disposition", "attachment; filename="+pecaEletronicaDTO.getNomePecaEletronica());		
		return pecaEletronicaDTO.getContent();
	}	
	
	@GetMapping("/download/{id}")   
	public @ResponseBody byte[] downloadRenunciaProcuracao(HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, 
					KeyStoreException, NoSuchAlgorithmException, IllegalAccessException,InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		try {
			
			String modeloRenuncia = EnumModeloDocumentEcm.RENUNCIA_DA_PROCURACAO_ELETRONICA.getDescricaoModeloDocumento();			
			PecaRenunciaProcuracao pecaRenuncia = pecaRenunciaProcuracaoService.findPecaRenuncia(id, modeloRenuncia);
								
			PecaEletronicaDto pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaRenuncia.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			response.setHeader("Content-disposition", "attachment; filename="+pecaEletronicaDTO.getNomePecaEletronica());		
			return pecaEletronicaDTO.getContent();	
			
		} catch (Exception e) {			
			throw new AjaxRequestException((e instanceof SefazException) ? e.getLocalizedMessage() 
					: "Falha no processo de salvamento/carregamento do documento solicitado.");
		}
	}

}
