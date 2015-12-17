


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/rs/")
public class RESTfulApplication extends Application
{
	private final Set<Class<?>> classes;

	public RESTfulApplication()
	{
		classes = new HashSet<>();

		classes.add(NewsletterResource.class);
	}

	@Override
	public Set<Class<?>> getClasses()
	{
		return classes;
	}
}
