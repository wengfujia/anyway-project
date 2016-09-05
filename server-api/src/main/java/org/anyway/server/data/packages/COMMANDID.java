/*
 * 名称: CommandID
 * 描述: 包头定义类
 * 消息头为正数表示是发送包，负数表示为反馈包
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.data.packages;

public final class COMMANDID {
	
	//消 息头的定义
	public final static int TEST = 0;		//检测心跳，不用做处理
	public final static int REGISTER = 1;	//用户帐号注册
	public final static int LOGIN = 2; 		//显示上次登录时间\IP的包头
	public final static int WEIXIN_INFOR_REGISTER = 3; 	//注册微信用户资料
	
	//学校相关消息头
	public final static int SCHOOL_REGISTER = 10;		//申请学校
	public final static int SCHOOL_AUTH = 11; 			//认证学校
	public final static int SCHOOL_INFORMATION = 12;	//获取学校详情
	public final static int SCHOOL_ALLINFORMATION = 13;	//获取学校全部详情
	public final static int SCHOOL_FETCH = 100;			//获取学校列表
	
	//班级相关消息头	
	public final static int CLASS_REGISTER = 20;	//申请班级
	public final static int CLASS_JOIN = 21;		//加入班级
	public final static int CLASS_INFORMATION = 22; //获取班级详情
	public final static int CLASS_MASTER = 23; 		//获取班主任
	public final static int CLASS_DELETE_USER = 24; //从班级中删除用户
	public final static int CLASS_VALIDATECODE = 25;//获取班级认证码
	public final static int CLASS_JOIN_VALIDATECODE = 26; 	//通过认证码加入班级
	public final static int CLASS_FETCH = 200;				//获取班级列表
	public final static int CLASS_FETCH_USERS = 201;		//获取班级用户列表
	public final static int CLASS_FETCH_TEACHERS = 202;		//获取班级老师列表
	
	//用户相关肖息头
	public final static int USER_INFORMATION = 30;		//获取用户基本信息
	public final static int USER_CHECK_INFORMATION = 31;//判断用户基本信息
	public final static int USER_ADD_SCHOOLADMIN = 32;	//添加学校管理用户	
	public final static int USER_CATEGORY = 300;		//获取用户职能
	public final static int USER_FETCH_CHILDREN = 301;	//获取用户孩子信息列表
	public final static int USER_FETCH_CLASSES = 302;	//获取用户班级编号列表
	public final static int USER_SCHOOL_FETCH = 303;	//获取用户学校列表
	
	//消息发送相关消息头
	public final static int	MESSAGE_SEND = 40;	//发送消息（单发）
	public final static int	MESSAGE_CLASS_SEND = 41;//发送消息（群发）
	public final static int	MESSAGE_ADVISORY = 42;	//咨询消息
	public final static int	MESSAGE_REPLY = 43;		//回复咨询
	public final static int	MESSAGE_NOTICE = 44;	//公告消息
	public final static int	MESSAGE_FETCH = 400; 	//获取新消息（家长接收）即当天的消息
	public final static int	MESSAGE_FETCH_ADVISORY = 401;	//获取新咨询（老师接收）即当天的消息
	public final static int	MESSAGE_FETCH_HISTORY = 402;	//获取历史发送消息 按时间段查询
	public final static int	MESSAGE_FETCH_HISTORY_ADVISORY = 403;	//获取历史咨询消息
	public final static int	MESSAGE_FETCH_READED = 404;	//获取已读列表（只对群发有效）
	public final static int	MESSAGE_FETCH_REPLY = 405;	//获取咨询的回复内空
	public final static int	MESSAGE_FETCH_NOTICE = 406;	//获取最新公告
	public final static int	MESSAGE_FETCH_HISTORY_NOTICE = 407;	//获取历史公告
	
	//成绩相关消息头
	public final static int SCORE_DEPLOY = 50;	//发布成绩
	public final static int SCORE_FETCH = 500;	//获取最近成绩
	
	//考勤相关消息头
	public final static int KQ_SETUP = 60;		//考勤设置
	public final static int KQ_ADD = 61;		//添加考勤
	public final static int KQ_FETCH = 600;		//查询个人历史考勤
	
	//空间
	public final static int ZONE_ADD_FILE_PRE = 6000;	//开始上传文件
	public final static int ZONE_ADD_FILE = 6001;		//完成上传文件
	public final static int ZONE_DEL_FILE_PRE = 6002;	//开始删除文件
	public final static int ZONE_DEL_FILE = 6003;		//完成删除文件
	public final static int ZONE_SHARE_FILE = 6004;		//共享文件到班级
	public final static int ZONE_CANCEL_SHARE_FILE = 6006;//取消共享
	public final static int ZONE_NEW_DIR = 6007;	//新建目录
	public final static int ZONE_LIST_FILE = 60000;	//获取我的文件
	public final static int ZONE_LIST_CLASSSHARE_FILE = 60001;	//获取班级共享文件
	
	//微信/client相关消息头
	public final static int WEIXIN_REQUEST = 70;//微信消息请求
	public final static int WEB_REQUEST = 71;	//web消息请求
	public final static int WEB_RESPONSE = 72;	//web消息返回
	
	//微信消息处理
	public final static int WEIXIN_REG_CLICK = 80000;	//点注册菜单
	public final static int WEIXIN_MSG_CLICK = 80001;	//点消息菜单
	public final static int WEIXIN_LOCATION = 80002;	//地理位置
	
	public final static int WEIXIN_SUBSCRIBE = 88888;	//定阅
	public final static int WEIXIN_UNSUBSCRIBE = 88889;	//取消定阅
	
	//其它
	public final static int DEFAULTRESPONSE = 10000;	//总的统一返回消息头
	public final static int INIT_FINAL = 10001;
	public final static int UPGRADE_CLIENT = 10002;	//更新
	
}
