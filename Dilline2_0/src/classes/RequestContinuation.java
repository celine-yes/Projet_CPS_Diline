package classes;

import java.io.Serializable;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * The class <code>RequestContinuation</code> implements the methods
 * of the interface <code>RequestContinuationI</code> including setter methods.
 *
 * @author Dilyara Babanazarova
 * @author Céline Fan

 */

public class RequestContinuation implements RequestContinuationI, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String uri;
	private QueryI code;
	private ConnectionInfoI clientConnectionInfo;
	private ExecutionStateI executionState;
	private boolean async;
	
	public RequestContinuation(String uri, QueryI code, ConnectionInfoI connectionInfo, ExecutionStateI executionState) {
		this.uri = uri;
		this.code = code;
		this.clientConnectionInfo = connectionInfo;
		this.executionState = ((ExecutionState) executionState).copy();
		this.async = false;
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

	@Override
	public ExecutionStateI getExecutionState() {
		return executionState;
	}

}
