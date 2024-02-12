package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import langage.interfaces.QueryI;

public class Request implements RequestI{
	
	private static final long serialVersionUID = 1L;
	private String uri;
	private QueryI code;
	
	public Request(String uri, QueryI code) {
		this.uri = uri;
		this.code = code;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ConnectionInfoI clientConnectionInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
