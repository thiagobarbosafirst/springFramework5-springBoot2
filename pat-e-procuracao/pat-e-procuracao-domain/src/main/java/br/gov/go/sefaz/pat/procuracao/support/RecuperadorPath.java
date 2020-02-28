package br.gov.go.sefaz.pat.procuracao.support;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/*@Scope(value="request")*/
public class RecuperadorPath  {
	
	@Autowired
	private ServletContext context;
	
	public String recuperarPath(String path){
		return context.getRealPath(path);
	}

}
