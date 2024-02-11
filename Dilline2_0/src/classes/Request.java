package classes;

import javax.management.Query;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class Request implements RequestI{
	
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
