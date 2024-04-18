package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import langage.interfaces.QueryI;

public class Request implements RequestI{
	
	private static final long serialVersionUID = 1L;
	private String uri;
	private QueryI code;
	private ConnectionInfoI clientConnectionInfo;
	private boolean async;
	
	public Request(String uri, QueryI code) {
		this.uri = uri;
		this.code = code;
		this.clientConnectionInfo = null;
		this.async = false;
	}
	
	public void setConnectionInfo(ConnectionInfoI connectionInfo) {
		clientConnectionInfo = connectionInfo;
	}
		
	@Override
	public String requestURI() {
		return this.uri;
	}

	@Override
	public QueryI getQueryCode() {
		return code;
	}

	@Override
	public boolean isAsynchronous() {
		return async;
	}
	
	public void setAsynchronous() {
		async = true;
	}

	@Override
	public ConnectionInfoI clientConnectionInfo() {
		return clientConnectionInfo;
	}

}
