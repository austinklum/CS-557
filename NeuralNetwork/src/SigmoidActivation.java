/**
 * @author Austin Klum
 */

public class SigmoidActivation implements ActivationFunction 
{
	@Override
	public double Activate(double input) 
	{
		return 1 / (1 + Math.pow(Math.E, -input));
	}

	@Override
	public double ActivatePrime(double input)
	{
		double activate = Activate(input);
		return activate * (1 - activate);
	}
	
	
	
}
