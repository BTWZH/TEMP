package cc.julong.transfer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportTransfer {

  // 记录Report错误数的集合
  public static ConcurrentLinkedQueue<String> errMsg = new ConcurrentLinkedQueue<String>();
  // 记录Report进程的状态
  public static AtomicBoolean running = new AtomicBoolean(true);

  // 记录Report已正确完成数的集合
  public static AtomicInteger ReportDoneCount = new AtomicInteger(0);

  // 记录Report未正确完成数的集合
  public static AtomicInteger ReportErrDoneCount = new AtomicInteger(0);

  // 记录Report待完成数的集合
  public static AtomicInteger ReportUnDoneCount = new AtomicInteger(0);

  public static void addReportDoneCount(int delta) {
    ReportDoneCount.addAndGet(delta);
  }

  public static int getReportDoneCount() {
    return ReportDoneCount.get();
  }

  public static void addReportErrDoneCount(int delta) {
    ReportErrDoneCount.addAndGet(delta);
  }

  public static int getReportErrDoneCount() {
    return ReportErrDoneCount.get();
  }

  public static void setReportUnDoneCount(int value) {
    ReportUnDoneCount.set(value);
  }

  public static int getReportUnDoneCount() {
    return ReportUnDoneCount.get();
  }

  public static void setReportDoneCount(int value) {
    ReportDoneCount.set(value);
  }

  public static void setReportErrDoneCount(int value) {
    ReportErrDoneCount.set(value);
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
