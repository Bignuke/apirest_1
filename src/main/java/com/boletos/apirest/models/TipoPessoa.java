package com.boletos.apirest.models;

public enum TipoPessoa {

	FISICA("F", "PF", "Pessoa Física"), 
	JURIDICA("J", "PJ", "Pessoa Jurídica");

	private String letra;
	private String sigla;
	private String descricao;

	private TipoPessoa(String letra, String sigla, String descricao) {
		this.letra = letra;
		this.sigla = sigla;
		this.descricao = descricao;
	}
	
	public String getLetra() {
		return letra;
	}
	public String getSigla() {
		return sigla;
	}
	public String getDescricao() {
		return descricao;
	}

	public static TipoPessoa getProLetra(String letra) {
		for (TipoPessoa t : TipoPessoa.values()) {
			if (t.getLetra().equals(letra))
				return t;
		}
		return null;
	}
}