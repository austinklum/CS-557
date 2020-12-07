
public class SigmoidActivation implements ActivationFunction 
{
	@Override
	public double Activate(double input) 
	{
		return 1 / (1 + Math.pow(Math.E, -input));
	}
}
