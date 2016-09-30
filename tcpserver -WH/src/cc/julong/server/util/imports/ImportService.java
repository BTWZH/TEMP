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

    public static String workPath = "";// ����Ŀ¼

    public static String exceptionPath = "";// �쳣��ϢĿ¼

    public static String finishPath = "";// ���Ŀ¼

    private static int executorSize = 10;

    public static ConcurrentLinkedQueue<String> workclq = new ConcurrentLinkedQueue<String>();// ��������
    public static ConcurrentSkipListSet<String> currclq = new ConcurrentSkipListSet<String>();// ��ǰ��������
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
                    throw new JLRunTimeException("����������ʱ����������û����ȫ������"
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
