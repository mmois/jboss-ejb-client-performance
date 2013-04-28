package server.ejb;

import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.ejb3.annotation.Clustered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateful
@Clustered
@Remote(PerformanceTestRemote.class)
public class PerformanceTestBean implements PerformanceTestRemote {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceTestBean.class);
	private long byteSum = 0L;

	public void sendBytes(byte[] bytes) {
		byteSum += bytes.length;
		logger.info("sendBytes(): received " + bytes.length + " bytes (total: " + byteSum + ").");
	}

	@Remove
	public void remove() {
		byteSum = 0L;
	}
}
