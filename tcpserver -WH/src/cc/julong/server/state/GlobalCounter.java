package cc.julong.server.state;

import java.util.concurrent.atomic.AtomicInteger;

public class GlobalCounter {

	public AtomicInteger trafficCount = new AtomicInteger(0);

	public AtomicInteger successCount = new AtomicInteger(0);

	public AtomicInteger errorCount = new AtomicInteger(0);

	public AtomicInteger currConn = new AtomicInteger(0);

}
