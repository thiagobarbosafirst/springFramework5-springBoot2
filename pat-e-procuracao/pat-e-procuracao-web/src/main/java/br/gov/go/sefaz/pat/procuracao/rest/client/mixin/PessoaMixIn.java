package br.gov.go.sefaz.pat.procuracao.rest.client.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;

public abstract class PessoaMixIn {

	@JsonIgnore
	public PessoaFisica pessoaFisica;
	
	@JsonIgnore
	public PessoaJuridica pessoaJuridica;
}