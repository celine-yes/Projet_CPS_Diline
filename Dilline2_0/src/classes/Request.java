package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import langage.interfaces.QueryI;


/**
 * Implements the {@link RequestI} interface to encapsulate details about a request in a sensor network.
 * This includes support for both synchronous and asynchronous operations, managing query codes,
 * and handling connection information of the client.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */

public class Request implements RequestI {
	
	private static final long serialVersionUID = 1L;
	private String uri;
	private QueryI code;
	private ConnectionInfoI clientConnectionInfo;
	private boolean async;
	private long startTime;
	
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
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime() {
		startTime = System.nanoTime();
	}

}
