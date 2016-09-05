package org.anyway.wechat.entity.message.req;

/**
 * 语音消息
 * @author beinfo
 *
 */
public class VoiceMessage extends MediaMessage {
	/**
	 * 语音格式，如amr，speex等
	 */
	private String Format;

	public VoiceMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VoiceMessage(String format) {
		super();
		Format = format;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}
}
