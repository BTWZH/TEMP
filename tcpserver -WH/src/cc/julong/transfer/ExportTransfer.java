package cc.julong.transfer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ExportTransfer {

  // 记录Export错误数的集合
  public static ConcurrentLinkedQueue<String> errMsg = new ConcurrentLinkedQueue<String>();
  // 记录Export进程的状态
  public static AtomicBoolean running = new AtomicBoolean(true);

  // 记录Export已正确完成数的集合
  public static AtomicInteger ExportDoneCount = new AtomicInteger(0);

  // 记录Export未正确完成数的集合
  public static AtomicInteger ExportErrDoneCount = new AtomicInteger(0);

  // 记录Export待完成数的集合
  public static AtomicInteger ExportUnDoneCount = new AtomicInteger(0);

  public static void addExportDoneCount(int delta) {
    ExportDoneCount.addAndGet(delta);
  }

  public static int getExportDoneCount() {
    return ExportDoneCount.get();
  }

  public static void addExportErrDoneCount(int delta) {
    ExportErrDoneCount.addAndGet(delta);
  }

  public static int getExportErrDoneCount() {
    return ExportErrDoneCount.get();
  }

  public static void setExportUnDoneCount(int value) {
    ExportUnDoneCount.set(value);
  }

  public static int getExportUnDoneCount() {
    return ExportUnDoneCount.get();
  }

  public static void setExportErrDoneCount(int value) {
    ExportErrDoneCount.set(value);
  }

  public static void setExportDoneCount(int value) {
    ExportDoneCount.set(value);
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

  }

}
