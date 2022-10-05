package bd;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class BdInterface {
	private SessionFactory factory;
	private Session session;

	public BdInterface() {
		@SuppressWarnings("unused")
		org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger("org.hibernate");
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.OFF);

		Configuration config = new Configuration();
		config.configure("./hibernate.cfg.xml");
		factory = config.buildSessionFactory();
		session = factory.openSession();
	}

	public Session getSession() {
		return session;
	}

	public void close() {
		session.close();
		factory.close();
	}
}
