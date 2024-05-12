package langage.interfaces;

/**
 * Represents a comparison expression in the sensor network query language.
 * This interface extends {@link IBexp} to provide functionalities specific to expressions
 * that involve comparing two numeric values, typically derived from sensor data or constants.
 * 
 * @author Dilyara Babanazarova
 * @author Céline Fan
 */
public interface ICexp extends IBexp{
	
	/**
     * Retrieves the first operand in the comparison expression.
     * This is usually a sensor value or a numeric constant.
     *
     * @return the first operand as {@link IRand}
     */
	public IRand getRand1();
	
	/**
     * Retrieves the first operand in the comparison expression.
     * This is usually a sensor value or a numeric constant.
     *
     * @return the first operand as {@link IRand}
     */
	public IRand getRand2();
}
