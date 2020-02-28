package br.gov.go.sefaz.pat.procuracao.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticado;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
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
@RequestMapping(ControllerMappingConfigProcuracao.USER_PATH_ROOT)
public class UserController {
	
	@GetMapping(ControllerMappingConfigProcuracao.USER_PATH_PROFILE)
	public ModelAndView profile(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) {
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.USER_VIEW_PROFILE);
		UsuarioAutenticado usuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado();
		mv.addObject(ControllerMappingConfigProcuracao.USER_MODEL_USUARIO_AUTENTICADO_DETAILS, usuarioAutenticadoDetails);
		mv.addObject(ControllerMappingConfigProcuracao.USER_MODEL_USUARIO_AUTENTICADO_NAME, usuarioAutenticado.getFromType());
		return mv;
	}
}