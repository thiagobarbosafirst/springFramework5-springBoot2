package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumTipoTelefone { 
	
	COMERCIAL(1, "Comercial"),
	RESIDENCIAL(2, "Residencial"),
	CELULAR(3, "Celular"),
	CONTATO(4, "Contato"),
	RECADO(5, "Recado"),
	FAX(6, "Fax");
	
	private final Integer codigo;
	private final String descricao;	
	
	private EnumTipoTelefone(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public Integer getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
} 