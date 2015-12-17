
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class SubscriberList extends ArrayList<Subscriber>
{
	private static final long serialVersionUID = 1;

	@XmlElement(name="subscriber")
	public List<Subscriber> getCatalogEntries()
	{
		return this;
	}
}
