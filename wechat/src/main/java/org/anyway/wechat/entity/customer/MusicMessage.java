package org.anyway.wechat.entity.customer;

/**
 * 音乐消息
 * @author beinfo
 *
 */
public class MusicMessage extends CustomerBaseMessage {
	/**
	 * 音乐对象
	 */
	private Music music;

	public MusicMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MusicMessage(org.anyway.wechat.entity.customer.Music music) {
		super();
		this.music = music;
	}

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}
}
