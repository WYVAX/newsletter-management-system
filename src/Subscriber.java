import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Subscriber 
{
	private String name;
	private String email;
	
	public Subscriber(){}
	public Subscriber(String name, String email)
	{
		this.name = name;
		this.email = email;
	}
	
	@XmlElement(name="subscriberName")
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	@XmlElement(name="subscriberEmail")
	public String getEmail() 
	{
		return email;
	}
	
	public void setEmail(String email) 
	{
		this.email = email;
	}
}
