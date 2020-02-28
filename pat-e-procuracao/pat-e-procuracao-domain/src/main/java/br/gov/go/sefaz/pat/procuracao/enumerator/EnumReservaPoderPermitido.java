package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumReservaPoderPermitido {
	
	SemPoderesParaSubstabelecer('1', "Sem poderes para substabelecer"),
	SomenteComReserva('2', "somente com reserva de poderes"),
	SomenteSemReserva('3', "somente sem reserva de poderes"),
	ComOuSemReserva('4', "com ou sem reserva de poderes"); 
	
	private final Character indiReservaPermitido;
	private final String significadoReservaPermitido;	
	
	private EnumReservaPoderPermitido(Character indiReservaPermitido, String significadoReservaPermitido) {
		this.indiReservaPermitido = indiReservaPermitido;
		this.significadoReservaPermitido = significadoReservaPermitido;
	}

	public Character getIndiReservaPermitido() {
		return indiReservaPermitido;
	}


	public String getSignificadoReservaPermitido() {
		return significadoReservaPermitido;
	}
	
	public static EnumReservaPoderPermitido parse(Character indiReservaPermitido){
		EnumReservaPoderPermitido  item = null;
		  for(EnumReservaPoderPermitido s : values()){
			  if(s.getIndiReservaPermitido().equals(indiReservaPermitido)){ 
				  item = s;
				  break;
			  }
		  }
		  return item;
	  }
	
} 