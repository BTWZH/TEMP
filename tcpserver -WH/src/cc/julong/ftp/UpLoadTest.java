package cc.julong.ftp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 压力测试用类
 * 
 * @author zz_mwq
 */
public class UpLoadTest extends Thread {

    private static long bank = 0;
    private static long sleep = 0;
    private static long count = 0;

    public UpLoadTest(int bank, long sleep) {
        UpLoadTest.bank = bank;
        UpLoadTest.sleep = sleep;
    }

    @Override
    public void run() {
        String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File fsnDate = new File(UpLoadCore.getFileRoot() + "/" + now);
        if (!fsnDate.exists()) {
            fsnDate.mkdirs();
        }
        //
        String bankName;
        String filePath;
        while (true) {
            try {
                bankName = "/bankpoint_"
                        + ((int) (Math.random() * UpLoadTest.bank));
                File fsnBank = new File(fsnDate.getPath() + bankName);
                if (!fsnBank.exists()) {
                    fsnBank.mkdir();
                }
                filePath = "/"
                        + new SimpleDateFormat("yyyyMMddHHmmsssss")
                                .format(new Date()) + "_" + ++count;
                new File(fsnBank.getPath() + filePath + ".fsn").createNewFile();

                Thread.sleep((int) (Math.random() * UpLoadTest.sleep));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
