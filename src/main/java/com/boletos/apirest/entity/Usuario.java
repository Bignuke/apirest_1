package com.boletos.apirest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Usuario {
	
	public static final String TABELA = "usuario";

	@JsonIgnore
	private int id;
	@JsonIgnore
	private Cliente cliente;
	private String username;
	private String password;
	@JsonIgnore
	private boolean administrador;
	
	public Usuario() {
	}
	public Usuario(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isAdministrador() {
		return administrador;
	}
	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}
	
	
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", username=" + username + ", password=" + password + ", administrador="
				+ administrador + "]";
	}
	
}
