package br.gov.go.sefaz.pat.procuracao.model.dto;

public class Estabelecimento {
	
	private Integer codigoEstabelecimento;
	private String nomeEstabelecimento;
	
	private Integer idPessoa;
	private String nomeFantasia;
	
	public Integer getCodigoEstabelecimento() {
		return codigoEstabelecimento;
	}
	
	public void setCodigoEstabelecimento(Integer codigoEstabelecimento) {
		this.codigoEstabelecimento = codigoEstabelecimento;
	}
	
	public String getNomeEstabelecimento() {
		return nomeEstabelecimento;
	}
	
	public void setNomeEstabelecimento(String nomeEstabelecimento) {
		this.nomeEstabelecimento = nomeEstabelecimento;
	}

	public Integer getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
}
