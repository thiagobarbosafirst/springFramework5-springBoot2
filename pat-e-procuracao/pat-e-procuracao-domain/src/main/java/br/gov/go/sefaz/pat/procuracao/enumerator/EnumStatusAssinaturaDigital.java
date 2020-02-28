package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumStatusAssinaturaDigital {
	
	PendenteAssinatura("S", "Pendente Assinatura"),
	Assinado("N", "Assinado");
	
	private final String codigo;
	private final String descricao;	
	
	private EnumStatusAssinaturaDigital(String codigo, String descricao) {
		this.codigo = codigo; 
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
		
    public static EnumStatusAssinaturaDigital parse(String codigo){
    	EnumStatusAssinaturaDigital  item = null;
		  for(EnumStatusAssinaturaDigital s : values()){
			  if(s.getCodigo().equals(codigo)){
				  item = s;
				  break;
			  }
		  }
		  return item;
	  }
	
} 