package br.gov.go.sefaz.pat.procuracao.controller.config;

public class ControllerMappingConfigProcuracao {
	
	public static final String PATH_ACTION_NEW							= 	"/form";	
	public static final String PATH_ACTION_EDIT							= 	"/form/{id}";
	public static final String PATH_ACTION_SAVE							= 	"/save";	
	public static final String PATH_ACTION_DELETE						= 	"/delete/{id}";
	public static final String PATH_ACTION_LIST							= 	"/list";
	public static final String PATH_ACTION_LIST_SUBSTABELECIMENTO		= 	"/listSubstabelecimento";
	public static final String PATH_ACTION_LIST_PROCURADOR				= 	"/listProcurador";
	public static final String PATH_ACTION_VERIFY_STEP_WIZARD			=	"/verifyStep/{id}";
	
	public static final String MODEL_ERROR_DETAILS_NAME 				= 	"errorDetails";
	
	
	public static final String DASHBOARD_PATH_ROOT						= 	"/dashboard";
	public static final String DASHBOARD_VIEW							= 	"index";
	public static final String DASHBOARD_MODEL_DTO						= 	"dashboardDto";
	
	public static final String UF_MODEL_ENTITIES_NAME 			= 	"ufs";
	public static final String PATSUJEITOPASSIVO_MODEL_ENTITIES_NAME 	= 	"sujeitoPassivos";
	
	public static final String AUTH_PATH_ROOT							= 	"/auth";
	public static final String AUTH_PATH_LOGIN 							= 	"/login";
	public static final String AUTH_PATH_LOGIN_FORM						= 	"/login-form";
	public static final String AUTH_PATH_LOGIN_ERROR					= 	"/login/error";
	public static final String AUTH_PATH_LOGOUT_SUCCESS					= 	"/logout/success";
	public static final String AUTH_PATH_LOGOUT_CONCURRENCY				= 	"/logout/concurrency";
	public static final String AUTH_PATH_DENIED 						= 	"/denied";
	public static final String AUTH_VIEW_LOGIN 							= 	"auth/login";
	public static final String AUTH_VIEW_LOGIN_FORM						= 	"auth/login-form";
	public static final String AUTH_VIEW_LOGOUT_SUCCESS					= 	"auth/logout-success";
	public static final String AUTH_VIEW_LOGOUT_CONCURRENCY				= 	"auth/logout-concurrency";
	public static final String AUTH_VIEW_DENIED							= 	"auth/access-denied";
	public static final String AUTH_MODEL_LOGIN_ERROR_DETAILS_NAME 		= 	"loginErrorDetails";
	
	public static final String PROCURACAO_PATH_ROOT					        = 	"/procuracao";
	public static final String SUBSTABELECIMENTO_PATH_ROOT					= 	"/substabelecimento";
	public static final String SUBSTABELECIMENTO_PRESENCIAL_PATH_ROOT		= 	"/substabelecimentoPresencial";
	public static final String PROCURADOR_PATH_ROOT					        = 	"/procurador";
	public static final String PROCURACAO_VIEW_FORM 						= 	"procuracao/procuracao-form";
	public static final String PROCURACAO_VIEW_FORM_REDIRECT 				=   "redirect:/procuracao/form";
	public static final String SUBSTABELECIMENTO_VIEW_FORM 					= 	"substabelecimento/substabelecimento-form";  
	public static final String SUBSTAB_PRESENCIAL_VIEW_FORM 				= 	"substabelecimentoPresencial/substabelecimentoPresencial-form";
	public static final String SUBSTAB_PRESENCIAL_VIEW_LIST 				= 	"substabelecimentoPresencial/substabelecimentoPresencial-list";
	public static final String PROCURACAO_VIEW_LIST 						= 	"procuracao/procuracao-list";
	public static final String SUBSTABELECIMENTO_VIEW_LIST 					= 	"substabelecimento/substabelecimento-list";
	public static final String PROCURACAO_VIEW_LIST_REDIRECT 				= 	"redirect:/procuracao/list";
	public static final String SUBSTABELECIMENTO_VIEW_LIST_REDIRECT			= 	"redirect:/substabelecimento/listSubstabelecimento";
	public static final String PROCURACAO_POR_PROCURADOR_VIEW_LIST 			= 	"procuracao/procuracao-list-procurador";
	public static final String PROCURACAO_PRESENCIAL_PROCURADOR_VIEW_LIST 	= 	"procuracaoPresencial/procuracaoPresencial-list-procurador";
	public static final String PROCURADOR_LAYOUT_FORM 						= 	"layout/fragments/procurador-form";
	public static final String PROCURADOR_VIEW_LIST 						= 	"layout/fragments/procurador-list";
	public static final String ESCRITORIO_LAYOUT_FORM 						= 	"layout/fragments/escritorio-form";
	public static final String PROCURACAO_REPORT_PATH_MINUTA				=	"/report/minuta/{sujeitoPassivoId:\\d+}";
	public static final String MINUTA_PROCR_REPORT_PATH_JASPER_RESOLVER_PJ	=   "minuta-report";	
	public static final String MINUTA_SUBSTAB_REPORT_PATH_JASPER_RESOLVER_PJ=   "minuta-substab-report";	
	public static final String PROCURACAO_REPORT_PATH_JASPER_RESOLVER_PJ	=   "procuracao-report";
	public static final String PROCURACAO_BUSINESS_NAME 		            =	"PatProcuração";
	public static final String PROCURACAO_DETAILS_NAME						=	"procuracao"; 
	
	public static final String SUBSTAB_PRESENCIAL_PATH_ACTION_NEW		    = 	"/form/idProcuracao/{idProcuracao}/idPessoaProcurador/{idPessoaProcurador}";
	
	public static final String RENUNCIA_PATH_ROOT            				= 	"/renuncia";	
	public static final String RENUNCIA_VIEW_FORM 				            = 	"renuncia/renuncia-form";
	public static final String RENUNCIA_VIEW_FORM_REDIRECT 				    = 	"redirect:/renuncia/form";	
	public static final String RENUNCIA_VIEW_LIST 				            = 	"renuncia/renuncia-list";	
	public static final String RENUNCIA_VIEW_LIST_REDIRECT 				    = 	"redirect:/renuncia/list";
	public static final String RENUNCIA_PATH_NEW_BY_PROCURACAO            	= 	"/{idProcuracao}";
	public static final String RENUNCIA_PATH_NEW_PROCURACAO_PRESENCIAL      = 	"/idProcuracao/{idProcuracao}/idPessoa/{idPessoa}";
	public static final String RENUNCIA_PRESENCIAL_VIEW_LIST                = 	"renunciaPresencial/renunciaPresencial-list";
		
	public static final String RENUNCIA_REPORT_PATH_JASPER                  =   "jasper/renuncia-report.jasper";
	public static final String RENUNCIA_RECIBO_JUNTADA_PATH_JASPER          =   "jasper/recibo-juntada-renuncia.jasper";
	
	public static final String RENUNCIA_PRESENCIAL_PATH_ROOT         		= 	"/renunciaPresencial";
	public static final String RENUNCIA_PRESENCIAL_SAVE              		= 	"save_presencial";	
	public static final String RENUNCIA_PRESENCIAL_VIEW_FORM 				= 	"renunciaPresencial/renunciaPresencial-form";	 
	public static final String RENUNCIA_PRESENCIAL_VIEW_LIST_PRESENCIAL 	= 	"renunciaPresencial/renunciaPresencial-list";	
	public static final String RENUNCIA__PRESENCIAL_VIEW_LIST_REDIRECT 		= 	"redirect:/renunciaPresencial/list";
	
	
	public static final String PROCESSO_VIEW_LIST_BY_REVOGACAO  				= 	"processoByRevogacao";
	public static final String REVOGACAO_DETAILS_NAME				            = 	"revogacoes";
	public static final String REVOGACAO_MINUTA						            = 	"/minuta";
	public static final String REVOGACAO_EXISTE						            = 	"existeRevogacao";
	public static final String REVOGACOES_EXISTE					            = 	"existeRevogacoes";
	public static final String REVOGACAO_ROCURACAO_PATH_ROOT		            = 	"/revogacao";
	public static final String REVOGACAO_ROCURACAO_PATH_LIST		            = 	"/list";
	public static final String REVOGACAO_PROCURACAO_VIEW_FORM					=	"revogacao/revogacao-form";
	public static final String REVOGACAO_PROCURACAO_VIEW_LIST 				    = 	"revogacao/revogacao-list";
	public static final String REVOGACAO_PROCURACAO_VIEW_LIST_REDIRECT		    = 	"redirect:/revogacao/list";
	
	public static final String ENDERECO_PATH_ROOT					        = 	"/enderecos";
	
	public static final String ERRORS_PATH_ROOT							= 	"/errors";
	public static final String ERRORS_PATH_404							= 	"/404";
	public static final String ERRORS_PATH_405							= 	"/405";
	public static final String ERRORS_PATH_500							= 	"/500";
	public static final String ERRORS_VIEW_PAGE_NOT_FOUND				= 	"errors/page-not-found";
	public static final String ERRORS_VIEW_METHOD_NOT_SUPPORTED			= 	"errors/method-not-supported";
	public static final String ERRORS_VIEW_INTERNAL_ERROR				= 	"errors/internal-error";
	
	public static final String USER_PATH_ROOT							= 	"/user";
	public static final String USER_PATH_PROFILE						= 	"/profile";
	public static final String USER_VIEW_PROFILE						= 	"user/profile-info";
	public static final String USER_MODEL_USUARIO_AUTENTICADO_NAME 		= 	"usuarioAutenticado";
	public static final String USER_MODEL_USUARIO_AUTENTICADO_DETAILS	= 	"usuarioAutenticadoDetails";
	
	public static final String CENTRAL_DOCUMENTOS_PATH_ROOT            	= 	"/centralDocumentos";	
	
	
}