package org.anyway.wechat.entity.menu;

/**
 * 菜单
 * @author lkl
 *
 */
public class Menu {
	/**
	 * 菜单按钮
	 */
	private Button[] button;
	
	public Menu() {
		super();
	} 
	
	public Menu(Button[] button) {
		super();
		this.button = button;
	}

	public Button[] getButton() {
		return button;
	}

	public void setButton(Button[] button) {
		this.button = button;
	}
}
