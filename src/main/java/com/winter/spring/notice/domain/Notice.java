package com.winter.spring.notice.domain;

import java.sql.Timestamp;

public class Notice {
	private int noticeNo;
	private String noticeSubject;
	private String noticeContent;
	private String noticeWriter;
	private Timestamp nCreateDate;
	private Timestamp nUpdateDate;
	private String noticeFilename;
	private String noticeFilepath;
	private long noticeFilelength;
	

	public int getNoticeNo() {
		return noticeNo;
	}


	public void setNoticeNo(int noticeNo) {
		this.noticeNo = noticeNo;
	}


	public String getNoticeSubject() {
		return noticeSubject;
	}


	public void setNoticeSubject(String noticeSubject) {
		this.noticeSubject = noticeSubject;
	}


	public String getNoticeContent() {
		return noticeContent;
	}


	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}


	public String getNoticeWriter() {
		return noticeWriter;
	}


	public void setNoticeWriter(String noticeWriter) {
		this.noticeWriter = noticeWriter;
	}


	public Timestamp getnCreateDate() {
		return nCreateDate;
	}


	public void setnCreateDate(Timestamp nCreateDate) {
		this.nCreateDate = nCreateDate;
	}


	public Timestamp getnUpdateDate() {
		return nUpdateDate;
	}


	public void setnUpdateDate(Timestamp nUpdateDate) {
		this.nUpdateDate = nUpdateDate;
	}


	public String getNoticeFilename() {
		return noticeFilename;
	}


	public void setNoticeFilename(String noticeFilename) {
		this.noticeFilename = noticeFilename;
	}


	public String getNoticeFilepath() {
		return noticeFilepath;
	}


	public void setNoticeFilepath(String noticeFilepath) {
		this.noticeFilepath = noticeFilepath;
	}


	public long getNoticeFilelength() {
		return noticeFilelength;
	}


	public void setNoticeFilelength(long noticeFilelength) {
		this.noticeFilelength = noticeFilelength;
	}


	@Override
	public String toString() {
		return "공지사항 [번호=" + noticeNo + ", 제목=" + noticeSubject + ", 내용=" + noticeContent
				+ ", 작성자=" + noticeWriter + ", 작성날짜=" + nCreateDate + ", 수정날짜=" + nUpdateDate
				+ ", 파일이름=" + noticeFilename + ", 파일경로=" + noticeFilepath + ", 파일크기="
				+ noticeFilelength + "]";
	}
}
