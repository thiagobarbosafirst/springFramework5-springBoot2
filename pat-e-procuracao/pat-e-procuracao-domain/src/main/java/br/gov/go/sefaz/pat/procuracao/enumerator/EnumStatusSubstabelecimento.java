package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumStatusSubstabelecimento {
	
	Incompleto('1', "Pendente de Assinatura Digital"),
	Expirado('2', "Expirado"),
	Completo('3', "Completo");
	
	private final Character codigo;
	private final String descricao;	
	
	private EnumStatusSubstabelecimento(Character codigo, String descricao) {
		this.codigo = codigo; 
		this.descricao = descricao;
	}

	public Character getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static EnumStatusSubstabelecimento parse(Character valor){
		EnumStatusSubstabelecimento enumStatusSubstabelecimento = null;
		for(EnumStatusSubstabelecimento e : values()) {
			if(e.getCodigo() == valor){
				enumStatusSubstabelecimento = e;
				break;
			}		
		}
		return enumStatusSubstabelecimento;
	}
	
	
} 