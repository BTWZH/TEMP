package cc.julong.transfer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GatHerTransfer {

  // 记录GatHer错误数的集合
  public static ConcurrentLinkedQueue<String> errMsg = new ConcurrentLinkedQueue<String>();
  // 记录GatHer进程的状态
  public static AtomicBoolean running = new AtomicBoolean(true);

  // 记录GatHer已正确完成数的集合
  public static AtomicInteger GatHerDoneCount = new AtomicInteger(0);

  // 记录GatHer未正确完成数的集合
  public static AtomicInteger GatHerErrDoneCount = new AtomicInteger(0);

  // 记录GatHer待完成数的集合
  public static AtomicInteger GatHerUnDoneCount = new AtomicInteger(0);

  public static void addGatHerDoneCount(int delta) {
    GatHerDoneCount.addAndGet(delta);
  }

  public static int getGatHerDoneCount() {
    return GatHerDoneCount.get();
  }

  public static void addGatHerErrDoneCount(int delta) {
    GatHerErrDoneCount.addAndGet(delta);
  }

  public static int getGatHerErrDoneCount() {
    return GatHerErrDoneCount.get();
  }

  public static void setGatHerUnDoneCount(int value) {
    GatHerUnDoneCount.set(value);
  }

  public static int getGatHerUnDoneCount() {
    return GatHerUnDoneCount.get();
  }

  public static void setGatHerErrDoneCount(int value) {
    GatHerErrDoneCount.set(value);
  }

  public static void setGatHerDoneCount(int value) {
    GatHerDoneCount.set(value);
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
