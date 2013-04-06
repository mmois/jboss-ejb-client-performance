package server.ejb;

public interface PerformanceTestRemote {

	void sendBytes(byte[] bytes);

	void remove();
}
