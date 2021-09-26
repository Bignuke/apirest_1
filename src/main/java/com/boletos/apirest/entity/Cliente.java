package com.boletos.apirest.entity;

import com.boletos.apirest.bankly.models.Account;
import com.boletos.apirest.models.TipoPessoa;

public class Cliente {

	public static final Object TABELA = "cliente";
	
	private int id;
	private String tipoPessoa;
	private String razao;
	private String nome;
	private String documento;
	private Account conta;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTipoPessoa() {
		return tipoPessoa;
	}
	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	public String getRazao() {
		return razao;
	}
	public void setRazao(String razao) {
		this.razao = razao;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public Account getConta() {
		return conta;
	}
	public void setConta(Account conta) {
		this.conta = conta;
	}
	
	public TipoPessoa getTipo() {
		return TipoPessoa.getProLetra(tipoPessoa);
	}
	public void setTipo(TipoPessoa tipo) {
		if (tipo!=null)
			this.tipoPessoa = tipo.getLetra() ;
	}
	
	@Override
	public String toString() {
		return "Cliente [id=" + id + ", tipoPessoa=" + tipoPessoa + ", razao=" + razao + ", nome=" + nome
				+ ", documento=" + documento + ", conta=" + conta + "]";
	}

}
