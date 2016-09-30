package cc.julong.transfer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImportTransfer {

  // 记录Import错误数的集合
  public static ConcurrentLinkedQueue<String> errMsg = new ConcurrentLinkedQueue<String>();
  // 记录Import进程的状态
  public static AtomicBoolean running = new AtomicBoolean(true);

  // 记录Import已正确完成数的集合
  public static AtomicInteger ImportDoneCount = new AtomicInteger(0);

  // 记录Import未正确完成数的集合
  public static AtomicInteger ImportErrDoneCount = new AtomicInteger(0);

  // 记录Import待完成数的集合
  public static AtomicInteger ImportUnDoneCount = new AtomicInteger(0);

  public static void addImportDoneCount(int delta) {
    ImportDoneCount.addAndGet(delta);
  }

  public static int getImportDoneCount() {
    return ImportDoneCount.get();
  }

  public static void addImportErrDoneCount(int delta) {
    ImportErrDoneCount.addAndGet(delta);
  }

  public static int getImportErrDoneCount() {
    return ImportErrDoneCount.get();
  }

  public static void setImportUnDoneCount(int value) {
    ImportUnDoneCount.set(value);
  }

  public static int getImportUnDoneCount() {
    return ImportUnDoneCount.get();
  }

  public static void setImportDoneCount(int value) {
    ImportDoneCount.set(value);
  }

  public static void setImportErrDoneCount(int value) {
    ImportErrDoneCount.set(value);
  }

  public static void addErrMsg(String value) {
    errMsg.add(value);
    if (errMsg.size() >= 101)
      errMsg.poll();
  }

  public static void ErrMsgClear() {
    errMsg.clear();
  }

  public static void main(String[] args) {
    System.out.println(ImportDoneCount.get());

    ImportDoneCount.set(0);
    System.out.println(ImportDoneCount.get());
    ImportDoneCount.set(100);
    System.out.println(ImportDoneCount.get());

    ImportDoneCount.set(0);
    System.out.println(ImportDoneCount.get());
  }

}
