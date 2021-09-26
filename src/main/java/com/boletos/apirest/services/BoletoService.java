package com.boletos.apirest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boletos.apirest.bankly.BanklyAPI;
import com.boletos.apirest.dao.BoletoDAO;
import com.boletos.apirest.dao.ParametroDAO;
import com.boletos.apirest.dao.UsuarioDAO;
import com.boletos.apirest.entity.Boleto;
import com.boletos.apirest.entity.Usuario;
import com.boletos.apirest.exception.APIException;
import com.boletos.apirest.exception.DAOException;
import com.boletos.apirest.exception.ServiceException;
import com.boletos.apirest.models.BoletoEmitido;
import com.boletos.apirest.models.BoletoSolicitado;
import com.boletos.apirest.bankly.models.Account;
import com.boletos.apirest.bankly.models.Authentication;
import com.boletos.apirest.bankly.models.BankSlip;

public class BoletoService {
	
	private final static Logger log = LoggerFactory.getLogger(BoletoService.class);
	
	private BanklyAPI api;
	private String accountBranch;
	private String accountNumber;
	private String documentNumber;
	
	public BoletoService() {
		try {
			ParametroDAO dao = ParametroDAO.getInstance();
			
			String urlAuth = dao.buscarUrlAuth();
			String urlApi = dao.buscarUrlApi();
			this.accountBranch = dao.buscarAccountBranch();
			this.accountNumber = dao.buscarAccountNumber();
			this.documentNumber = dao.buscarDocumentNumber();
			this.api = new BanklyAPI(urlAuth, urlApi);

		} catch (DAOException e) {
			log.error("Erro ao carregar parametros da Bankly.", e);
		}
	}
	
	public Usuario buscar(Usuario user) throws ServiceException {
		try {
			Usuario usuario = UsuarioDAO.getInstance().login(user);
			log.debug("Usuario {}", usuario);
			return usuario;
		} catch (DAOException e) {
			log.error("Erro ao verificar usuário.", e);
			throw new ServiceException(e);
		}
	}
	
	public String getToken() {
		try {
			ParametroDAO dao = ParametroDAO.getInstance();

			Authentication auth = new Authentication();
			auth.setAccessToken(dao.buscarAccessToken());
			auth.setExpiresIn(dao.buscarExpireIn());
			auth.setCreateToken(dao.buscarCreateToken());
			if (!auth.isTokenValid()) {
				String grantType = dao.buscarGrantType();
				String clientId = dao.buscarClientId();
				String clientSecret = dao.buscarClientSecret();
				
				auth = api.getAuth(grantType, clientId, clientSecret);
				log.debug("Novo token gerado. {}", auth);
				dao.inserir(auth);
				log.debug("Novo token gravado. {}", auth);
			}
			return auth.getAccessToken();

		} catch (DAOException e) {
			log.error("Erro na busca do token no banco de dados. {}", e);
		} catch (APIException e) {
			log.error("Erro na geração de novo token. {}", e);
		}
		return null;
	}

	public BankSlip emitir(BoletoSolicitado bean) throws ServiceException {
		if (bean.getAccount()==null)		bean.setAccount(getConta(bean.getUser()));
		if (bean.getDocumentNumber()==null) bean.setDocumentNumber(getDocumento(bean.getUser()));
		
		try {
			BoletoEmitido emitido = api.emitir(getToken()
				, bean.getAlias()
				, bean.getAccount().getBranch()
				, bean.getAccount().getNumber()
				, bean.getDocumentNumber()
				, bean.getAmount()
				, bean.getDueDate()
				, bean.getEmissionFee()
				, bean.getType()
				, bean.getPayer()
			);
			log.debug("Boleto emitido na Bankly. {}", emitido.getAuthenticationCode());
			
			// gravar no banco
			BoletoDAO dao = BoletoDAO.getInstance();
			
			Boleto boleto = new Boleto(bean, emitido);
			dao.persistir(boleto);
			log.debug("Boleto salvo no banco de dados. {}", boleto);
			
			BankSlip bankslip = atualizar(dao, emitido.getAccount().getBranch(), emitido.getAccount().getNumber(), emitido.getAuthenticationCode());
			return bankslip;
			
		} catch (APIException e) {
			log.error("Erro na solicitação de boleto na Bankly.", e);
			throw new ServiceException(e);
		} catch (DAOException e) {
			log.error("Erro ao salvar boleto emitido.", e);
			throw new ServiceException(e);
		}
	}

	public BankSlip cancelar(BoletoEmitido bean) throws ServiceException {
		if (bean.getAccount()==null)	bean.setAccount(getConta(bean.getUser()));
		
		String branch = bean.getAccount().getBranch();
		String number = bean.getAccount().getNumber();
		String authenticationCode = bean.getAuthenticationCode();
		
		try {
			api.cancelar(getToken(), branch, number, authenticationCode);
			log.debug("Boleto cancelado na Bankly. {}", bean.getAuthenticationCode());

			// gravar no banco
			BoletoDAO dao = BoletoDAO.getInstance();

			BankSlip bankslip = atualizar(dao, branch, number, authenticationCode);
			return bankslip;
			
		} catch (APIException e) {
			log.error("Erro no cancelamento de boleto na Bankly.", e);
			throw new ServiceException(e);
		} catch (DAOException e) {
			log.error("Erro ao atualizar dados no banco.", e);
			throw new ServiceException(e);
		}
	}
	
	public BankSlip finalizar(BoletoEmitido bean) throws ServiceException {
		if (bean.getAccount()==null)	bean.setAccount(getConta(bean.getUser()));
		
		String branch = bean.getAccount().getBranch();
		String number = bean.getAccount().getNumber();
		String authenticationCode = bean.getAuthenticationCode();
		
		try {
			api.finalizar(getToken(), branch, number, authenticationCode);
			log.debug("Baixa de boleto na Bankly. {}", bean.getAuthenticationCode());

			// gravar no banco
			BoletoDAO dao = BoletoDAO.getInstance();

			BankSlip bankslip = atualizar(dao, branch, number, authenticationCode);
			return bankslip;
			
		} catch (APIException e) {
			log.error("Erro na baixa de boleto na Bankly.", e);
			throw new ServiceException(e);
		} catch (DAOException e) {
			log.error("Erro ao atualizar dados no banco.", e);
			throw new ServiceException(e);
		}
	}
	
	public BankSlip buscar(BoletoEmitido bean) throws ServiceException {
		if (bean.getAccount()==null)	bean.setAccount(getConta(bean.getUser()));
		
		String branch = bean.getAccount().getBranch();
		String number = bean.getAccount().getNumber();
		String authenticationCode = bean.getAuthenticationCode();
		
		try {
			// gravar no banco
			BoletoDAO dao = BoletoDAO.getInstance();
			
			BankSlip bankslip = atualizar(dao, branch, number, authenticationCode);
			return bankslip;
			
		} catch (APIException e) {
			log.error("Erro ao buscar boleto na Bankly.", e);
			throw new ServiceException(e);
		} catch (DAOException e) {
			log.error("Erro ao atualizar dados no banco.", e);
			throw new ServiceException(e);
		}
	}

	private BankSlip atualizar(BoletoDAO dao, String branch, String number, String authenticationCode) throws APIException, DAOException {
		BankSlip bankslip = api.buscar(getToken(), branch, number, authenticationCode);
		log.debug("Obter dados do boleto na Bankly. {}", bankslip);
		Boleto boleto = dao.buscar(branch, number, authenticationCode);
		log.debug("Boleto no banco de dados. {}", boleto);
		
		Boleto bean = new Boleto(boleto.getId(), bankslip);
		dao.persistir(bean);
		log.debug("Boleto atualizado no banco de dados. {}", bean);
		return bankslip;
	}
	

	private Account getConta(Usuario user) {
		if (user.getCliente().getConta()!=null)
			return user.getCliente().getConta();
		return new Account(accountBranch, accountNumber);
	}
	
	private String getDocumento(Usuario user) {
		if (user.getCliente().getDocumento()!=null)
			return user.getCliente().getDocumento();
		return documentNumber;
	}
	
}
