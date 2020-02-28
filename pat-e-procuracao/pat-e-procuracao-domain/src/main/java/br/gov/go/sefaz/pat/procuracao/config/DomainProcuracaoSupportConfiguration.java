package br.gov.go.sefaz.pat.procuracao.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {WebProcuracaoConfigurationParameters.PACKAGE_SUPPORT})
public class DomainProcuracaoSupportConfiguration {
}