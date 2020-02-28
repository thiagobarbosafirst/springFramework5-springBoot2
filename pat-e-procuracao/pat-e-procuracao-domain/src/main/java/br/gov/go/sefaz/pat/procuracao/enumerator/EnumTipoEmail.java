package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumTipoEmail { 

	COMERCIAL(1, "Comercial"),
	PESSOAL(2, "Pessoal"),
	COBRANCA(3, "Cobrança");
	
	private final Integer codigo;
	private final String descricao;	
	
	private EnumTipoEmail(Integer codigo, String descricao) {
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