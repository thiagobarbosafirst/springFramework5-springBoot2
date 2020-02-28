package br.gov.go.sefaz.pat.procuracao.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.pat.procuracao.constants.Messages;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;

/**
 * <b>Secretaria de Estado da Fazenda de Goiás</b></br>
 * Supervisão de Arquitetura e Prospecção Tecnológica</br>
 * <i>SEFAZ-GO JavaEE Sample Project Archetype</i><br/><br/>
 * 
 * Security Controller.
 * 
 * @author Thiago-SC and Renato-RS
 * 
 */
@Controller
@RequestMapping(ControllerMappingConfigProcuracao.AUTH_PATH_ROOT)
public class AuthController {
	
	@GetMapping(ControllerMappingConfigProcuracao.AUTH_PATH_LOGIN)
	public ModelAndView login() {
		return new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_LOGIN);
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.AUTH_PATH_LOGIN_FORM)
	public ModelAndView formLogin() {
		return new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_LOGIN_FORM);
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.AUTH_PATH_LOGIN_ERROR)
	public ModelAndView loginError(HttpSession session) {
		String errorDetails = "";
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_DENIED);
		if(session != null) {
			errorDetails = (String) session.getAttribute(ControllerMappingConfigProcuracao.AUTH_MODEL_LOGIN_ERROR_DETAILS_NAME);
		} else {
			errorDetails = Messages.LOGIN_ERROR_UNHANDLED;
		}
		mv.addObject(ControllerMappingConfigProcuracao.MODEL_ERROR_DETAILS_NAME, errorDetails);
		return mv;
	}

	@GetMapping(ControllerMappingConfigProcuracao.AUTH_PATH_LOGOUT_SUCCESS)
	public ModelAndView logoutSuccess() {
		return new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_LOGOUT_SUCCESS);
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.AUTH_PATH_LOGOUT_CONCURRENCY)
	public ModelAndView logoutConcurrency() {
		return new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_LOGOUT_CONCURRENCY);
	}

	@GetMapping(ControllerMappingConfigProcuracao.AUTH_PATH_DENIED)
	public ModelAndView accessDenied() {
		return new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_DENIED);
	}
}