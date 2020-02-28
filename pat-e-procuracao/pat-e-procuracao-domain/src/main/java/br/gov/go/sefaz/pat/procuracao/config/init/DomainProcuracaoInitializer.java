package br.gov.go.sefaz.pat.procuracao.config.init;

import br.gov.go.sefaz.pat.procuracao.config.DomainProcuracaoJdbcConfiguration;
import br.gov.go.sefaz.pat.procuracao.config.DomainProcuracaoJpaConfiguration;
import br.gov.go.sefaz.pat.procuracao.config.DomainProcuracaoServiceConfiguration;
import br.gov.go.sefaz.pat.procuracao.config.DomainProcuracaoSupportConfiguration;

public class DomainProcuracaoInitializer {

	public static Class<?>[] getRootConfigClasses() {
		return new Class[]{
				DomainProcuracaoJpaConfiguration.class,
				DomainProcuracaoJdbcConfiguration.class,
				DomainProcuracaoServiceConfiguration.class,
				DomainProcuracaoSupportConfiguration.class};
	}
}