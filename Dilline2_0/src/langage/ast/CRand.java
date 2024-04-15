package langage.ast;

import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import langage.interfaces.IRand;

public class CRand implements IRand{
	private double reelle;

	public CRand(double reelle) {
		super();
		this.reelle = reelle;
	}

	public double getReelle() {
		return reelle;
	}

	@Override
	public Object eval(ExecutionStateI data) {
		return reelle;
	}

}
