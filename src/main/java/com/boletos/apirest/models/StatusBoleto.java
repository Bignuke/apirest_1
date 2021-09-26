package com.boletos.apirest.models;

public enum StatusBoleto {

	REGISTERED("Registered"),
	ACCEPTED("Accepted"),
	CANCELLED("Cancelled"),
	SETTLED("Settled");
	
	private StatusBoleto(String descricao) {
		this.descricao = descricao;
	}
	
	private String descricao;
	public String getDescricao() {
		return descricao;
	}
	
	public static StatusBoleto get(String status) {
		for (StatusBoleto bean : StatusBoleto.values()) {
			if (bean.getDescricao().equals(status)) {
				return bean;
			}
		}
		return null;
	}
	
}
