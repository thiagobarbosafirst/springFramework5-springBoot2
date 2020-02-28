package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumModalidadeEletronicaPresencial {
	
	PRESENCIAL(Character.valueOf('S'), "Presencial"),
	ELETRONICA(Character.valueOf('N'), "Eletrônica"); 
	
	private final Character codigo;
	private final String descricao;	
	
	private EnumModalidadeEletronicaPresencial(Character codigo, String descricao) {
		this.codigo = codigo; 
		this.descricao = descricao;
	}

	public char getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static EnumModalidadeEletronicaPresencial parse(Character codigo){
		EnumModalidadeEletronicaPresencial enumModalidadeEletronicaPresencial = null;
		  for(EnumModalidadeEletronicaPresencial t : values()){
			  if(t.getCodigo() == codigo){
				  enumModalidadeEletronicaPresencial = t;
				  break;
			  }
		  }
		  return enumModalidadeEletronicaPresencial;
	  }
}