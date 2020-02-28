package br.gov.go.sefaz.pat.procuracao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.ERRORS_PATH_ROOT)
public class ErrorsController {
	
	@GetMapping(ControllerMappingConfigProcuracao.ERRORS_PATH_404)
	public ModelAndView notFound(){
		return new ModelAndView(ControllerMappingConfigProcuracao.ERRORS_VIEW_PAGE_NOT_FOUND);
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.ERRORS_PATH_405)
	public ModelAndView notSupported(){
		return new ModelAndView(ControllerMappingConfigProcuracao.ERRORS_VIEW_METHOD_NOT_SUPPORTED);
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.ERRORS_PATH_500)
	public ModelAndView internalError() {
		return new ModelAndView(ControllerMappingConfigProcuracao.ERRORS_VIEW_INTERNAL_ERROR);
	}
}