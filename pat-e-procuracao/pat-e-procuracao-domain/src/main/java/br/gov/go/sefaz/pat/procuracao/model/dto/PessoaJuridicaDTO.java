package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.math.BigInteger;

public class PessoaJuridicaDTO {
	
	private Integer idPessoa;
	private String numeroCnpj;
	private String nomeFantasia;
	private String cnpjBase;
	private Character indiHomologacaoCadastro;
	private Character tipoPessoaJuridica;
	private BigInteger codigoNire;
	private BigInteger numeroInscricaoMunicipal;
	
	public Integer getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}
	public String getNumeroCnpj() {
		return numeroCnpj;
	}
	public void setNumeroCnpj(String numeroCnpj) {
		this.numeroCnpj = numeroCnpj;
	}
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
	public String getCnpjBase() {
		return cnpjBase;
	}
	public void setCnpjBase(String cnpjBase) {
		this.cnpjBase = cnpjBase;
	}
	public Character getIndiHomologacaoCadastro() {
		return indiHomologacaoCadastro;
	}
	public void setIndiHomologacaoCadastro(Character indiHomologacaoCadastro) {
		this.indiHomologacaoCadastro = indiHomologacaoCadastro;
	}
	public Character getTipoPessoaJuridica() {
		return tipoPessoaJuridica;
	}
	public void setTipoPessoaJuridica(Character tipoPessoaJuridica) {
		this.tipoPessoaJuridica = tipoPessoaJuridica;
	}
	public BigInteger getCodigoNire() {
		return codigoNire;
	}
	public void setCodigoNire(BigInteger codigoNire) {
		this.codigoNire = codigoNire;
	}
	public BigInteger getNumeroInscricaoMunicipal() {
		return numeroInscricaoMunicipal;
	}
	public void setNumeroInscricaoMunicipal(BigInteger numeroInscricaoMunicipal) {
		this.numeroInscricaoMunicipal = numeroInscricaoMunicipal;
	}

}
