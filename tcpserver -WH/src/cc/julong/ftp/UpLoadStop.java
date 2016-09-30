package cc.julong.ftp;

public class UpLoadStop extends Thread {

	private final UpLoadMain main;
	private final int sleep;

	public UpLoadStop(UpLoadMain main, int sleep) {
		this.main = main;
		this.sleep = sleep;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		main.stop();
	}

}
