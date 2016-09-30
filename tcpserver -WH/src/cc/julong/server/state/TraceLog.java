package cc.julong.server.state;

import java.util.Date;

public class TraceLog {

	private Date traceTime;

	private String comment;

	private TraceState state;

	public TraceLog(Date traceTime, String comment, TraceState state) {
		super();
		this.traceTime = traceTime;
		this.comment = comment;
		this.state = state;
	}

	public Date getTraceTime() {
		return traceTime;
	}

	public void setTraceTime(Date traceTime) {
		this.traceTime = traceTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public TraceState getState() {
		return state;
	}

	public void setState(TraceState state) {
		this.state = state;
	}

}
