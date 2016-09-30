package cc.julong.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
/*
 * @author lbf
 * @time：2013年12月26日 上午10:33:42
 * @function:写日志文件
 */

public class LogHelper {
	private  Logger  _log;
	private  FileAppender  _appender;
     public LogHelper(String logRoot)
     {
    	 this._appender=(FileAppender) Logger.getRootLogger().getAppender("logfile");
    	 this._appender.setFile(new StringBuffer().append(logRoot).append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append(".log").toString() );
    	 this._appender.activateOptions();
     }
     public void Info(String msg)
     {	
 		 this._log.info(msg);
     }
}
