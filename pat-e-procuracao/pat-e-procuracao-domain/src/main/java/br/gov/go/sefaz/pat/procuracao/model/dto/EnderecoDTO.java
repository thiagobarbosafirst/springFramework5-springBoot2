package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.List;

import br.gov.go.sefaz.javaee.corporativo.model.Endereco;

public class EnderecoDTO {
	
	private Integer id;
	
	private List<Endereco> enderecos;

	private Endereco endereco;

	public EnderecoDTO(){}
	
	public EnderecoDTO(List<Endereco> enderecos) {
		
		if(enderecos.size() == 1){
			this.endereco = enderecos.get(0);
		} else {
			this.enderecos = enderecos;
		}
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Endereco getEndereco() {
		return endereco;
	}
	
	public void addEndereco(Endereco endereco){
		if(endereco == null) throw new NullPointerException("Endereço não pode ser nullo.");
		enderecos.add(endereco);
	}
	
	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Endereco> getEnderecos() {
		return enderecos;
	}

}
