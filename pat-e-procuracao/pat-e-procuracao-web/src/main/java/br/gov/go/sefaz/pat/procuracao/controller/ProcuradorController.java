package br.gov.go.sefaz.pat.procuracao.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.javaee.corporativo.constants.OrigemEmpresa;
import br.gov.go.sefaz.javaee.corporativo.constants.RegimeFiscal;
import br.gov.go.sefaz.javaee.corporativo.model.DocumentoPessoaId;
import br.gov.go.sefaz.javaee.corporativo.model.EmailPessoa;
import br.gov.go.sefaz.javaee.corporativo.model.Empresa;
import br.gov.go.sefaz.javaee.corporativo.model.Endereco;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEnderecoPK;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.model.TelefonePessoa;
import br.gov.go.sefaz.javaee.corporativo.model.TipoDocumento;
import br.gov.go.sefaz.javaee.corporativo.service.EmailPessoaService;
import br.gov.go.sefaz.javaee.corporativo.service.EmpresaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaEnderecoService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.corporativo.service.TelefonePessoaService;
import br.gov.go.sefaz.javaee.corporativo.service.UfService;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;
import br.gov.go.sefaz.pat.model.EnderecoProcurador;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEmail;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEndereco;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoTelefone;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuradorDto;
import br.gov.go.sefaz.pat.procuracao.rest.client.CorporativoAdminRestClient;
import br.gov.go.sefaz.pat.procuracao.service.DocumentoService;
import br.gov.go.sefaz.pat.procuracao.service.EnderecoProcuradorService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuradorService;
import br.gov.go.sefaz.pat.procuracao.validation.input.ProcuracaoInputValidator;
import br.gov.go.sefaz.pat.support.UtilSupport;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.PROCURADOR_PATH_ROOT)
public class ProcuradorController {	
	
	@Autowired
	private ProcuradorService procuradorService;
	
	@Autowired
	private PessoaFisicaService pessoaFisicaService;
	
	@Autowired
	private PessoaJuridicaService pessoaJuridicaService;
	
	@Autowired
	private UfService ufService;
	
	@Autowired
	private EmailPessoaService emailPessoaService;
	
	@Autowired
	private PessoaEnderecoService pessoaEnderecoService;
	
	@Autowired
	private TelefonePessoaService telefonePessoaService;
	
	@Autowired
	private DocumentoService documentoService;
	
	@Autowired
	private EmpresaService empresaService;
	
	@Autowired
	private EnderecoProcuradorService enderecoProcuradorService;
	
	@Autowired
	private CorporativoAdminRestClient procuracaoRestClient;
	
	@Autowired
	private ProcuracaoInputValidator procuracaoInputValidator;
	
	@PostMapping(value="/{numeroCpf:[0-9]{11}}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ModelAndView consultarPessoaFisica(@PathVariable String numeroCpf) {
				
		ProcuradorDto procuradorDto = new ProcuradorDto();
		ProcuracaoDto procuracaoDto = new ProcuracaoDto();
		
		if(!new UtilSupport().isCPF(numeroCpf)) { 
			throw new AjaxRequestException("Cpf inválido.");
		}
		
		//Consulta Pessoa Física
		PessoaFisica pessoaFisica = pessoaFisicaService.consultarPorCpf(numeroCpf);		
		Procurador procuradorConsulta = procuradorService.consultar(pessoaFisica);
		procuradorDto.setCadastrarCnpj(false);
		
		if(procuradorConsulta != null) {			
			
			if(procuradorConsulta.getEscritorioProcurador() != null) {				
				
				procuradorDto.setIdEscritorio(procuradorConsulta.getEscritorioProcurador().getIdPessoa());
				procuradorDto.setNumeroCnpjEscritorio(procuradorConsulta.getEscritorioProcurador().getNumeroCnpj());
				procuradorDto.setNomeFantasiaEscritorio(procuradorConsulta.getEscritorioProcurador().getNomeFantasia());
				procuradorDto.setNomeEmpresarialEscritorio(procuradorConsulta.getEscritorioProcurador().getEmpresa().getNomeEmpresa());  
				procuradorDto.setRegimeFiscal(String.valueOf(procuradorConsulta.getEscritorioProcurador().getEmpresa().getIndiRegimeFiscalAsInt()));
				
				PessoaEnderecoPK pessoaEnderecoPKPJ = new PessoaEnderecoPK();
				pessoaEnderecoPKPJ.setIdPessoa(procuradorConsulta.getEscritorioProcurador().getIdPessoa());
				pessoaEnderecoPKPJ.setTipoEndereco(EnumTipoEndereco.COMERCIAL.getCodigo());
				
				PessoaEndereco pessoaEnderecoPJ = pessoaEnderecoService.consultar(pessoaEnderecoPKPJ);
				
				if(pessoaEnderecoPJ != null && pessoaEnderecoPJ.getEndereco() != null && pessoaEnderecoPJ.getEndereco().getLogradouro() != null) {
					procuradorDto.setCodgLogradouroEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getCodigo()); 
					procuradorDto.setNumeroEscritorio(pessoaEnderecoPJ.getEndereco().getNumero());
					procuradorDto.setQuadraEscritorio(pessoaEnderecoPJ.getEndereco().getNumeroQuadra());
					procuradorDto.setLoteEscritorio(pessoaEnderecoPJ.getEndereco().getNumeroLote());
					procuradorDto.setComplementoEscritorio(pessoaEnderecoPJ.getEndereco().getComplemento());
					procuradorDto.setCepEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getCep().toString());
					procuradorDto.setEnderecoEscritorioFormatado(new FormatSupport().formatEndereco(pessoaEnderecoPJ.getEndereco()));	
					procuradorDto.setEnderecoEscritorio(pessoaEnderecoPJ.getEndereco()); 
					procuradorDto.setTipoLogradouroEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getTipoLogradouro().getDescricao());
					procuradorDto.setNomeLogradouroEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getNomeLogradouro().getNome());
					procuradorDto.setBairroLogradouroEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getNomeBairro().getNome());
					procuradorDto.setCidadeLogradouroEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getMunicipio().getNomeMunicipio());
					procuradorDto.setUfLogradouroEscritorio(pessoaEnderecoPJ.getEndereco().getLogradouro().getMunicipio().getUf().getCodigo());	
				}				
			}
			
		}
				
		if(pessoaFisica != null) {
			
			procuradorDto.setPessoaFisica(pessoaFisica);
			if(procuradorConsulta != null) {
				procuradorDto.setCadastrarCpf(false);
				EnderecoProcurador enderecoProcurador = enderecoProcuradorService.consultar(procuradorConsulta.getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
				decoratorEndereco(procuradorDto, enderecoProcurador.getEndereco());				
			}	
			
			else {
				
				/**
				 * Procurado o endereço da pessoa nos endereços corporativos. O mesmo, se houver, será replicado na tabela de endereço procurador
				 */
				PessoaEnderecoPK pessoaEnderecoPK = new PessoaEnderecoPK();
				pessoaEnderecoPK.setIdPessoa(pessoaFisica.getIdPessoa());
				
				//Tipo Endereço comercial
				pessoaEnderecoPK.setTipoEndereco(EnumTipoEndereco.COMERCIAL.getCodigo());		
				
				PessoaEndereco pessoaEndereco = pessoaEnderecoService.consultar(pessoaEnderecoPK);
				
				if(pessoaEndereco != null) {
					//Não utilizo o idEndereço nesse caso pois será inserido um novo endereco para o procurador na tabela de endereço procurador
					pessoaEndereco.getEndereco().setIdEndereco(null);
					decoratorEndereco(procuradorDto, pessoaEndereco.getEndereco());
				}
			}		
			
						
			//Consulta e-mail: Comercial 
			EmailPessoa emailPessoa = emailPessoaService
					.consultarPorPessoaTipoEmail(pessoaFisica.getIdPessoa(),EnumTipoEmail.COMERCIAL.getCodigo());
			
			if(emailPessoa != null) {
				procuradorDto.setEmailPessoa(emailPessoa.getEmail());
				procuradorDto.setIdEmail(emailPessoa.getId());
				procuradorDto.setTipoEmail(emailPessoa.getTipoEmail());
			}
			
			//Consulta telefone: Comercial 
			TelefonePessoa telefonePessoa = telefonePessoaService
					.consultarPorPessoaTipoTelefone(pessoaFisica.getIdPessoa(),EnumTipoTelefone.COMERCIAL.getCodigo());
			
			if(telefonePessoa != null) {
				procuradorDto.setIdTelefone(telefonePessoa.getId());
				procuradorDto.setTelefone(telefonePessoa.getNumeroDDD() + "" + telefonePessoa.getNumeroTelefone());
				procuradorDto.setTipoTelefone(telefonePessoa.getTipoTelefone());
			}
			
			//Consulta documento tipo documento 11 = Identidade Funcional.
			DocumentoPessoaId documentoPessoaId = new DocumentoPessoaId();
			Pessoa pessoaDocumento = new Pessoa();
			pessoaDocumento.setIdPessoa(pessoaFisica.getIdPessoa());
			documentoPessoaId.setPessoa(pessoaDocumento);
			
			TipoDocumento tipoDocumentoPessoa = new TipoDocumento();
			tipoDocumentoPessoa.setIdTipoDocumento(11);
			documentoPessoaId.setTipoDocumento(tipoDocumentoPessoa);
			DocumentoAdvogado documentoAdvogado = documentoService.consultar(documentoPessoaId);
			
			if(documentoAdvogado != null) {
				procuradorDto.setUfAdvogado(documentoAdvogado.getUf().getCodigo());
				procuradorDto.setNumeroOAB(documentoAdvogado.getNumeroDocumento());
				procuradorDto.setTipoDocumento(documentoAdvogado.getDocumentoPessoaId().getTipoDocumento().getIdTipoDocumento());
				procuradorDto.setIdDocumento(documentoAdvogado.getIdDocumentoPessoa());
			}			

		} else {
			procuradorDto.setCadastrarCpf(true);
		}

		procuracaoDto.setProcurador(procuradorDto);
		
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURADOR_LAYOUT_FORM);
		mv.addObject(ControllerMappingConfigProcuracao.UF_MODEL_ENTITIES_NAME, ufService.listar());
		mv.addObject("regimesFiscais", montarListaRegimeFiscal());		
		mv.addObject("procuracaoDto", procuracaoDto); 
		mv.addObject("cpfConsultado", numeroCpf);
		
		return mv; 
	}	
	
	@PostMapping(value="/{numeroCnpj:[0-9]{14}}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ModelAndView consultarPessoaJuridica(@PathVariable String numeroCnpj){
		
		procuracaoInputValidator.validateCnpj(numeroCnpj);
		
		ProcuradorDto procuradorDto = new ProcuradorDto();
		
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica = pessoaJuridicaService.consultarPorCnpj(numeroCnpj);
		
		if(pessoaJuridica != null) {
			
			procuradorDto.setCadastrarCnpj(false);
			
			procuradorDto.setIdEscritorio(pessoaJuridica.getIdPessoa());
			procuradorDto.setNumeroCnpjEscritorio(numeroCnpj);
			procuradorDto.setNomeFantasiaEscritorio(pessoaJuridica.getNomeFantasia());		
			procuradorDto.setNomeEmpresarialEscritorio(pessoaJuridica.getEmpresa().getNomeEmpresa());  
			procuradorDto.setRegimeFiscal(String.valueOf(pessoaJuridica.getEmpresa().getIndiRegimeFiscalAsInt()));
			
			//Consulta Endereço
			PessoaEnderecoPK pessoaEnderecoPK = new PessoaEnderecoPK();
			pessoaEnderecoPK.setIdPessoa(pessoaJuridica.getIdPessoa());
			
		} else {
			procuradorDto.setCadastrarCnpj(true);
			procuradorDto.setIdEscritorio(null);
			procuradorDto.setNumeroCnpjEscritorio("");
			procuradorDto.setNomeFantasiaEscritorio("");	
			procuradorDto.setNomeEmpresarialEscritorio("");	
		}
		
		ProcuracaoDto procuracaoDto = new ProcuracaoDto();
		procuracaoDto.setProcurador(procuradorDto);
		
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.ESCRITORIO_LAYOUT_FORM);  
		mv.addObject("regimesFiscais", montarListaRegimeFiscal());	
		mv.addObject("procuracaoDto", procuracaoDto);  
		mv.addObject("cnpjConsultado", numeroCnpj);
		return mv; 
	}	
	
	@RequestMapping(value = "/addProcurador", method = RequestMethod.POST)
	public @ResponseBody ModelAndView addProcurador(@RequestBody ProcuracaoDto procuracaoDto, HttpServletRequest request) {
		
		List<ProcuradorDto> procuradores = procuracaoDto.getProcuradores();
				
		if(procuradores == null) {
			procuradores = new ArrayList<ProcuradorDto>();
		}		
		
		if(procuracaoDto.getProcurador().getIdPessoa() == null) {
			
			PessoaFisica pessoaFisica = new PessoaFisica();
			pessoaFisica.setNome(procuracaoDto.getProcurador().getNome());
			pessoaFisica.setNumeroCpf(procuracaoDto.getProcurador().getCpf().replaceAll("[.-]", ""));
			pessoaFisica = procuracaoRestClient.savePessoaFisica(pessoaFisica, "7374");
			procuracaoDto.getProcurador().setIdPessoa(pessoaFisica.getIdPessoa()); 
			procuracaoDto.getProcurador().setCadastrarCpf(false);
			
		} 
		
		if(procuracaoDto.getProcurador().getIdEscritorio() == null && procuracaoDto.getProcurador().getNumeroCnpjEscritorio() != null && !procuracaoDto.getProcurador().getNumeroCnpjEscritorio().equals("")) {
			
			String numrCnpj = procuracaoDto.getProcurador().getNumeroCnpjEscritorio().replaceAll("\\D", "");
			
			Empresa empresa = empresaService.consultarPorCnpjBase(numrCnpj.substring(0, 8));
			if(empresa == null) {
				empresa = new Empresa();
				empresa.setNumeroCnpjBase(numrCnpj.substring(0, 8));
				empresa.setNomeEmpresa(procuracaoDto.getProcurador().getNomeEmpresarialEscritorio());
				empresa.setIndiOrigemEmpresa(OrigemEmpresa.SEFAZ);
				empresa.setIndiRegimeFiscal(RegimeFiscal.parse(Integer.valueOf(procuracaoDto.getProcurador().getRegimeFiscal()))); 
				empresa = procuracaoRestClient.saveEmpresa(empresa, "7374"); 
			}
			
			PessoaJuridica pessoaJuridica = new PessoaJuridica();
			pessoaJuridica.setNomeFantasia(procuracaoDto.getProcurador().getNomeFantasiaEscritorio());
			pessoaJuridica.setNumeroCnpj(numrCnpj);
			pessoaJuridica.setEmpresa(empresa);
			
			pessoaJuridica = procuracaoRestClient.savePessoaJuridica(pessoaJuridica, "7374");
			procuracaoDto.getProcurador().setIdEscritorio(pessoaJuridica.getIdPessoa());
			procuracaoDto.getProcurador().setNumeroCnpjEscritorio(pessoaJuridica.getNumeroCnpj());
			procuracaoDto.getProcurador().setNomeFantasiaEscritorio(pessoaJuridica.getNomeFantasia());
			
			procuracaoDto.getProcurador().setCadastrarCnpj(false);
			
		} 
		
		procuradores.add(procuracaoDto.getProcurador());
		
		procuracaoDto.setProcuradores(procuradores);
		
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURADOR_VIEW_LIST);   
		mv.addObject("procuracaoDto", procuracaoDto);  
		return mv; 	
	}
	
	private List<RegimeFiscal> montarListaRegimeFiscal() {
		List<RegimeFiscal> listaRegimeFiscais = new ArrayList<RegimeFiscal>();
		listaRegimeFiscais.add(RegimeFiscal.MICRO_EMPRESA);
		listaRegimeFiscais.add(RegimeFiscal.NORMAL);
		return listaRegimeFiscais;
	}
	
	private void decoratorEndereco(ProcuradorDto procuradorDto, Endereco endereco) {
		procuradorDto.setCodgLogradouro(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getCodigo()); 
		procuradorDto.setNumero(endereco.getNumero());
		procuradorDto.setQuadra(endereco.getNumeroQuadra());
		procuradorDto.setLote(endereco.getNumeroLote());
		procuradorDto.setComplemento(endereco.getComplemento());
		procuradorDto.setCep(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getCep().toString());
		procuradorDto.setEnderecoFormatado(new FormatSupport().formatEndereco(endereco));	
		procuradorDto.setIdEndereco(endereco.getIdEndereco());	 			
		procuradorDto.setTipoLogradouro(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getTipoLogradouro().getDescricao());
		procuradorDto.setNomeLogradouro(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getNomeLogradouro().getNome());
		procuradorDto.setBairroLogradouro(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getNomeBairro().getNome());
		procuradorDto.setCidadeLogradouro(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getMunicipio().getNomeMunicipio());
		procuradorDto.setUfLogradouro(endereco.getLogradouro() == null  ? null : endereco.getLogradouro().getMunicipio().getUf().getCodigo());	
	}
	
}
