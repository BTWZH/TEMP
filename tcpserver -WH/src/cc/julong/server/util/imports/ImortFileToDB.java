package cc.julong.server.util.imports;

public class ImortFileToDB implements Runnable {

    @Override
    public void run() {
        ImportService.runningThread.incrementAndGet();
        while (ImportService.running.get()) {
            String fileName = ImportService.workclq.poll();// ȡ����Ҫ�����Ŀ¼
            if (fileName == null) {
                try {
                    Thread.sleep(50);// �����Ϣ������ ������
                } catch (InterruptedException e) {
                    // StatMoniter.warn("�߳� ˯���쳣");
                    // e.printStackTrace();
                }
            } else {
                // insert data to db
            }
        }
        ImportService.runningThread.decrementAndGet();
    }

}
