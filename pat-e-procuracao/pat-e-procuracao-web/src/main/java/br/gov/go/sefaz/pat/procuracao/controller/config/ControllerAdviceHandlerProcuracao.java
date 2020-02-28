package br.gov.go.sefaz.pat.procuracao.controller.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.procuracao.config.WebProcuracaoConfigurationParameters;
import br.gov.go.sefaz.pat.procuracao.constants.Messages;

@ControllerAdvice(basePackages = {WebProcuracaoConfigurationParameters.PACKAGE_CONTROLLER})
public class ControllerAdviceHandlerProcuracao {

	private static final Logger logger = LogManager.getLogger(ControllerAdviceHandlerProcuracao.class);
	
	@InitBinder
    public void registerCustomEditors(WebDataBinder binder, WebRequest request) {
    	binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
    }
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request, @AuthenticationPrincipal User user){
    	logger.error("Attempted unauthorized access: " + request.getRequestURI() + "[" + user.getUsername() + "]");
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.AUTH_VIEW_DENIED);
		mv.addObject(ControllerMappingConfigProcuracao.MODEL_ERROR_DETAILS_NAME, String.format(Messages.ACCESS_DENIED, request.getRequestURI()));
		return mv;
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ModelAndView handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
		logger.error("Method not supported!");
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.ERRORS_VIEW_METHOD_NOT_SUPPORTED);
		return mv;
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleIllegalArgumentException(IllegalArgumentException ex){
		return ex.getLocalizedMessage();
	}
	
	@ExceptionHandler(AjaxRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String[] handleAjaxRequestException(AjaxRequestException ex){
		return ex.getDetalhes();
	}
	
	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleThrowable(Throwable ex){
		logger.error("A general error has occurred!", ex);
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.ERRORS_VIEW_INTERNAL_ERROR);
		mv.addObject(ControllerMappingConfigProcuracao.MODEL_ERROR_DETAILS_NAME, new SefazException(ex.getMessage(), ex));
		mv.addObject(MessageElementTagParameters.FORM_STACKTRACE_MESSAGE_KEY, ExceptionUtils.getStackTrace(ex));
		return mv;
	}
}
