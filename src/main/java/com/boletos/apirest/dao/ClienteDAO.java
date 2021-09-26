package com.boletos.apirest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.boletos.apirest.bankly.models.Account;
import com.boletos.apirest.entity.Cliente;
import com.boletos.apirest.exception.DAOException;
import com.boletos.apirest.utils.StringUtils;

public class ClienteDAO extends AbstractDAO {

	private static ClienteDAO instance;
	public static ClienteDAO getInstance() {
		if (instance==null)	instance = new ClienteDAO();
		return instance;
	}
	
	public ClienteDAO() {
	}
	
	public Cliente buscar(int id) throws DAOException {
		return buscar(id, null);
	}
	
	public Cliente buscar(String documento) throws DAOException {
		return buscar(null, documento);
	}
	
	private Cliente buscar(Integer id, String documento) throws DAOException {
		StringBuilder query = new StringBuilder();
		query.append("SELECT id, tipoPessoa, documento, nome, razaoSocial, conta_agencia, conta_numero");
		query.append(" FROM ").append(Cliente.TABELA).append(" WHERE 1=1");
		if (id != null)
			query.append(" AND id=").append(id);
		if (!StringUtils.isEmpty(documento))
			query.append(" AND documento='").append(documento).append("'");
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(query.toString());
			rs = ps.executeQuery();
			
			Cliente bean = new Cliente();
			if (rs.next()) {
				bean.setId(rs.getInt("id"));
				bean.setTipoPessoa(rs.getString("tipoPessoa"));
				bean.setDocumento(rs.getString("documento"));
				bean.setNome(rs.getString("nome"));
				bean.setRazao(rs.getString("razaoSocial"));
				String branch = rs.getString("conta_agencia");
				String number = rs.getString("conta_numero");
				if (!StringUtils.isEmpty(branch) && !StringUtils.isEmpty(number))
					bean.setConta(new Account(branch, number));
			}
			return bean;
		} catch (SQLException e) {
			log.error("Erro [{}] {}", query, e);
			throw new DAOException(e);
		} finally {
			close(con, ps, rs);
		}
	}

	
	
}
