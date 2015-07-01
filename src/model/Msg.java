package model;

public class Msg {

	private String toUserName;
	private String fromUserName;
	private Long createTime;
	private String msgType;
	private String content;

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toXml() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<xml>");
		stringBuffer.append(String.format("<ToUserName><![CDATA[%s]]></ToUserName>", toUserName));
		stringBuffer.append(String.format("<FromUserName><![CDATA[%s]]></FromUserName>", fromUserName));
		stringBuffer.append(String.format("<CreateTime>%d</CreateTime>", createTime));
		stringBuffer.append(String.format("<MsgType><![CDATA[%s]]></MsgType>", msgType));
		stringBuffer.append(String.format("<Content><![CDATA[%s]]></Content>", content));
		stringBuffer.append("</xml>");
		return stringBuffer.toString();
	}

}
