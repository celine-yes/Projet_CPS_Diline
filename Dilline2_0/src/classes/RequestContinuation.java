package classes;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class RequestContinuation implements RequestContinuationI {
	
	private static final long serialVersionUID = 1L;
	private String uri;
	private QueryI code;
	private ConnectionInfoI clientConnectionInfo;
	private ExecutionStateI executionState;
	
	public RequestContinuation(String uri, QueryI code, ConnectionInfoI connectionInfo, ExecutionStateI executionState) {
		this.uri = uri;
		this.code = code;
		this.clientConnectionInfo = connectionInfo;
		this.executionState = executionState;
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
		return clientConnectionInfo;
	}

	@Override
	public ExecutionStateI getExecutionState() {
		return executionState;
	}

}
