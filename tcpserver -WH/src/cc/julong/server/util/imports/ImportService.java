package cc.julong.server.util.imports;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cc.julong.server.exception.JLRunTimeException;
import cc.julong.server.util.FolderTask;

public class ImportService {

    public static String workPath = "";// 工作目录

    public static String exceptionPath = "";// 异常信息目录

    public static String finishPath = "";// 完成目录

    private static int executorSize = 10;

    public static ConcurrentLinkedQueue<String> workclq = new ConcurrentLinkedQueue<String>();// 工作队列
    public static ConcurrentSkipListSet<String> currclq = new ConcurrentSkipListSet<String>();// 当前工作队列
    public static ConcurrentHashMap<String, Integer> errorHashMap = new ConcurrentHashMap<String, Integer>();
    public static AtomicBoolean running = new AtomicBoolean(true);
    public static AtomicInteger runningThread = new AtomicInteger(0);

    private static void clear() {
        workclq.clear();
        currclq.clear();
        errorHashMap.clear();
    }

    public static void start() throws JLRunTimeException {
        int timeout = 30000;
        while (runningThread.get() != 0) {
            try {
                timeout = timeout - 50;
                if (timeout < 0) {
                    throw new JLRunTimeException("服务启动超时；工作进程没有完全结束："
                            + runningThread.get());
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continue;
        }
        clear();
        run();
    }

    public static void stop() throws JLRunTimeException {
        running.set(false);
        workclq.clear();
        currclq.clear();
    }

    private static void run() {

        Executor executor = Executors.newFixedThreadPool(executorSize);

        for (int i = 0; i < executorSize - 1; i++) {
            executor.execute(new ImortFileToDB());
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                runningThread.incrementAndGet();
                while (running.get()) {
                    if (workclq.peek() == null) {
                        FolderTask.GetTodoFiles(workclq, currclq, workPath,
                                "e_");
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                runningThread.decrementAndGet();
            }
        });

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ImportService.run();

    }

}
