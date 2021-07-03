package com.hlj.domain;

import java.util.Date;

/**
 * Message entity. @author MyEclipse Persistence Tools
 */

public class Message implements java.io.Serializable {

	// Fields

	private Integer id;
	private String sender;
	private String getter;
	private String content;
	private Date sendTime;
	private String attachment;

	// Constructors

	/** default constructor */
	public Message() {
	}

	/** minimal constructor */
	public Message(String sender, String getter, String content, Date sendTime) {
		this.sender = sender;
		this.getter = getter;
		this.content = content;
		this.sendTime = sendTime;
	}

	/** full constructor */
	public Message(String sender, String getter, String content, Date sendTime,
			String attachment) {
		this.sender = sender;
		this.getter = getter;
		this.content = content;
		this.sendTime = sendTime;
		this.attachment = attachment;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSender() {
		return this.sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getGetter() {
		return this.getter;
	}

	public void setGetter(String getter) {
		this.getter = getter;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getAttachment() {
		return this.attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}