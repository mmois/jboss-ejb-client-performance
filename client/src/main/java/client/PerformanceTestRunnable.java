package client;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.ejb.PerformanceTestBean;
import server.ejb.PerformanceTestRemote;

public class PerformanceTestRunnable implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceTestRunnable.class);
	private int numberOfIterations;
	private PerformanceTest performanceTest;
	private int messageSize;

	public PerformanceTestRunnable(int numberOfIterations, PerformanceTest performanceTest, int messageSize) {
		this.numberOfIterations = numberOfIterations;
		this.performanceTest = performanceTest;
		this.messageSize = messageSize;
	}

	public void run() {
		try {
			PerformanceTestRemote performanceTestRemote = lookupRemoteBean();
			logger.debug("Looked up remote bean.");
			for (int i = 0; i < numberOfIterations; i++) {
				performanceTestRemote.sendBytes(createRandomByteArray(messageSize * 1024 * 1024));
				logger.debug("Sent bytes to server.");
			}
			performanceTest.tellFinished();
		} catch (NamingException e) {
			logger.error(String.format("Invocation of remote bean failed: %s.", e.getMessage()), e);
		}
	}

	private byte[] createRandomByteArray(int size) {
		byte[] retVal = new byte[size];
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = (byte) Math.random();
		}
		return retVal;
	}

	private PerformanceTestRemote lookupRemoteBean() throws NamingException {
		logger.info("Using jboss-ejb-client.");
		final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		final String appName = "jboss-ejb-client-performance";
		final String moduleName = "server-ejb";
		final String distinctName = "";
		final String beanName = PerformanceTestBean.class.getSimpleName();
		final String viewClassName = PerformanceTestRemote.class.getName();
		String lookupString = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!"
				+ viewClassName + "?stateful";
		logger.debug(String.format("Lookking up: %s", lookupString));
		return (PerformanceTestRemote) context.lookup(lookupString);
	}

	private PerformanceTestRemote lookupRemoteBeanRemoteNaming() throws NamingException {
		logger.info("Using jboss-remote-naming.");
		Properties jndiProps = new Properties();
		jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		jndiProps.put(Context.PROVIDER_URL, "remote://127.0.0.1:4447");
		jndiProps.put(Context.SECURITY_PRINCIPAL, "jboss");
		jndiProps.put(Context.SECURITY_CREDENTIALS, "passwd");
		jndiProps.put("jboss.naming.client.ejb.context", true);
		Context ctx = new InitialContext(jndiProps);
		final String beanName = PerformanceTestBean.class.getSimpleName();
		final String viewClassName = PerformanceTestRemote.class.getName();
		return (PerformanceTestRemote) ctx.lookup("jboss-ejb-client-performance/server-ejb/" + beanName + "!"
				+ viewClassName);
	}
}
