package cc.julong.transfer;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DeleteTransfer {

  // 记录Delete错误数的集合
  public static ConcurrentLinkedQueue<String> errMsg = new ConcurrentLinkedQueue<String>();
  // 记录Delete进程的状态
  public static AtomicBoolean running = new AtomicBoolean(true);

  // 记录Delete已正确完成数的集合
  public static AtomicInteger DeleteDoneCount = new AtomicInteger(0);

  // 记录Delete未正确完成数的集合
  public static AtomicInteger DeleteErrDoneCount = new AtomicInteger(0);

  // 记录Delete待完成数的集合
  public static AtomicInteger DeleteUnDoneCount = new AtomicInteger(0);

  public static void addDeleteDoneCount(int delta) {
    DeleteDoneCount.addAndGet(delta);
  }

  public static int getDeleteDoneCount() {
    return DeleteDoneCount.get();
  }

  public static void addDeleteErrDoneCount(int delta) {
    DeleteErrDoneCount.addAndGet(delta);
  }

  public static int getDeleteErrDoneCount() {
    return DeleteErrDoneCount.get();
  }

  public static void setDeleteUnDoneCount(int value) {
    DeleteUnDoneCount.set(value);
  }

  public static int getDeleteUnDoneCount() {
    return DeleteUnDoneCount.get();
  }

  public static void setDeleteErrDoneCount(int value) {
    DeleteErrDoneCount.set(value);
  }

  public static void setDeleteDoneCount(int value) {
    DeleteDoneCount.set(value);
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
