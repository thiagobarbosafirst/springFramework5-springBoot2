package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumTipoEndereco { 
	
	RESIDENCIAL(1, "Residencial"),
	COMERCIAL(2, "Comercial"),
	CORRESPONDENCIA(3, "Correspondência");
	
	private final Integer codigo;
	private final String descricao;	
	
	private EnumTipoEndereco(Integer codigo, String descricao) {
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