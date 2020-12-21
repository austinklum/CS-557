
public enum Action
{	
	LEFT("<", "\u2190"),
	UP("^", "\u2191"),
	RIGHT(">", "\u2192"),
	DOWN("v", "\u2193"),
	LEFT_RIGHT("-", "\u2194"),
	UP_DOWN("|", "\u2195"),
	UP_LEFT("\\", "\u2196"),
	UP_RIGHT("/", "\u2197"),
	DOWN_RIGHT("\\", "\u2198"),
	DOWN_LEFT("//", "\u2199"),
	UP_DOWN_RIGHT(">", "\u22a2"),
	UP_DOWN_LEFT("<", "\u22a3"),
	DOWN_LEFT_RIGHT("v", "\u22a4"),
	UP_LEFT_RIGHT("^", "\u22a5"),
	UP_DOWN_LEFT_RIGHT("+", "+");
	
	private String ascii;
	private String unicode;
	
	Action(String ascii, String unicode)
	{
		this.ascii = ascii;
		this.unicode = unicode;
	}
	
	public String getRepresentation()
	{
		return getRepresentation(false); 
	}
	
	public String getRepresentation(boolean useUnicode)
	{
		if (useUnicode)
		{
			return getUnicode();
		}
		
		return getAscii();
	}
	
	public String getAscii()
	{
		return ascii;
	}
	
	public String getUnicode()
	{
		return unicode;
	}
}
