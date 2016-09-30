package cc.julong.server.util.imports;

public class ImortFileToDB implements Runnable {

    @Override
    public void run() {
        ImportService.runningThread.incrementAndGet();
        while (ImportService.running.get()) {
            String fileName = ImportService.workclq.poll();// 取出需要处理的目录
            if (fileName == null) {
                try {
                    Thread.sleep(50);// 如果信息不存在 做休眠
                } catch (InterruptedException e) {
                    // StatMoniter.warn("线程 睡眠异常");
                    // e.printStackTrace();
                }
            } else {
                // insert data to db
            }
        }
        ImportService.runningThread.decrementAndGet();
    }

}
