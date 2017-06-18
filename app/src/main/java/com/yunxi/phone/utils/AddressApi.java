package com.yunxi.phone.utils;

/**
 * create by bond
 */

public class AddressApi {
	// 测试地址
//	public static final String HOST_NAME = "http://test.58yunxi.com";
	// 生产地址
	public static final String HOST_NAME = "http://phone.yunxi.cn";



	//第一次进入app内，获得身份信息
	public static final String GET_USERINFO=HOST_NAME+"/equipment";
	//发送验证码
	public static final String VALIDATION=HOST_NAME+"/code/regget";
	//重置密码时需要的发送验证码
	public static final String VALIDATION_FORGET=HOST_NAME+"/code/forgetget";
	//注册地址
	public static final String REGISTER=HOST_NAME+"/code/regcheck";
	//重置密码
	public static final String FIX_PWD=HOST_NAME+"/code/forgetcheck";
	//云朵中心
	public static final String CLOUD_CENTER=HOST_NAME+"/user/cloudcenter";

	//登陆
	public static final String LOGIN=HOST_NAME+"/login?";


	//排行
	public static final String RANK=HOST_NAME+"/Rank/RankInfo";
	//游戏列表
	public static final String GAME_LIST=HOST_NAME+"/game/GetGameInfo";

	//积分明细接口
	public static final String CLOUD_DETAILS=HOST_NAME+"/my/balance";

	//运动
	public static final String RUNNING_INFO=HOST_NAME+"/Running/UserRunningInfo";
	public static final String REPORT_STEP=HOST_NAME+"/Running/ReportRunning";


	//通话中心获取可用通话时间
	public static final String CALL_CENTER=HOST_NAME+"/user/gettalkminutes";

	//关于我们
	public static final String ABOUT_US=HOST_NAME+"/Html/About.html";
	//用户协议
	public static final String PROCTOL=HOST_NAME+"/Html/Agreement.html";

	//下载任务列表
	public static final String GET_USER_TASK=HOST_NAME+"/Task/Get_User_Task";
	//获取系统消息
	public static final String GET_SYSTEM_MSG=HOST_NAME+"/Notice/Get_User_Notice";
	//系统消息是否已读
	public static final String UPDATE_NOTICE=HOST_NAME+"/Notice/Update_User_Notice";
	//意见反馈
	public static final String FEED_BACK=HOST_NAME+"/Feedback/AddFeedback";
	//广告转发
	public static final String SEND_ADS=HOST_NAME+"/Advertise/Get_Advertise";
	//获取新闻列表
	public static final String NEWS_LIST=HOST_NAME+"/News/GetNewsData";
	//兑换比例
	public static final String GET_EXCHANGE=HOST_NAME+"/Exchange/GetExchangeData";
	//提交兑换
	public static final String SEDN_EXCHANGE=HOST_NAME+"/Exchange/ReportExchange";
	//兑换记录
	public static final String EXCHANGE_RECORD=HOST_NAME+"/Exchange/GetUserExchangeData";
	//点击广告分享到朋友圈
	public static final String SHARE_ADS=HOST_NAME+"/Advertise/Get_QrCode_Url";
	//我收藏的列表
	public static final String MY_COLLECTION=HOST_NAME+"/Collection/GetUserCollection";

	//上报获取奖励
	public static final String SEDN_TASK=HOST_NAME+"/Task/ReportUserTask";
	//上报应用
	public static final String SEDN_APP=HOST_NAME+"/Task/AddUserApp";
	//邀请
	public static final String GET_INVITATION=HOST_NAME+"/Invitation/GetInvitationData ";
	//邀请详情
	public static final String GET_INVITATION_INFO=HOST_NAME+"/Invitation/GetUserInvitationInfo ";
	//新闻分类
	public static final String GET_NEWS_KIND=HOST_NAME+"/News/GetNewsCategory ";


	//身份证验证系统
	public static final String ID_CARD_URL="https://api-cn.faceplusplus.com/cardpp/v1/ocridcard";
	//人脸识别系统
	public static final String FACE_DETECT_URL="https://api-cn.faceplusplus.com/facepp/v3/detect";
	//人脸对比系统
	public static final String FACE_COMPARE_URL="https://api-cn.faceplusplus.com/facepp/v3/compare";
	//自有身份验证接口
	public static final String YANZHENG=HOST_NAME+"/user/ReportIdentity";
	//壁纸列表
	public static final String GET_LOCK=HOST_NAME+"/LockScreen/GetLockScreen";
	//检查是否已收藏
	public static final String CHECK_COLLECT=HOST_NAME+"/Collection/IsCollection";
	//增加收藏
	public static final String ADD_COLLECT=HOST_NAME+"/Collection/AddUserCollection";
	//取消收藏
	public static final String DELETE_COLLECT=HOST_NAME+"/Collection/DeleteUserCollection";
	//邀请回调
	public static final String ADD_INVITATION=HOST_NAME+"/Invitation/AddInvitationRecord ";
	//监测更新
	public static final String VERSION=HOST_NAME+"/Version/Update";
	//提交通话接口
	public static final String SUBMIT_CALL=HOST_NAME+"/Telephone/ReceiveTelephoneRecord";



	//des PASSWORD
	public static final String DES_PASSWORD = "@bd8db5a";
	public static final int MSG_FROM_CLIENT = 0;
	public static final int MSG_FROM_SERVER = 1;
	public static final int REQUEST_SERVER = 2;


	//微信
	public static final String WEICHAT_APP_ID = "wx6950f5aaf8ac0d78";
	public static final String WEICHAT_APP_KEY = "01a07277ad7a850db67d2937065cf1a1";

	//微博
	public static final String SINA_APP_ID = "3476160462";
	public static final String SINA_APP_KEY = "f6f90c2e0cc10ea50a75436f1b71bcfc";

	//qq
	public static final String QQ_APP_ID = "1105900912";
	public static final String QQ_APP_KEY = "KEYa4MZwrILNW6JPaHf";

	//身份证，人脸识别用到的参数
	public static final String API_KEY="Gj800xIXKXVichhDGcUNrVYneR5aEbY9";
	public static final String API_SECRET="-M1blGoAwFpclXJcBKgJnFP94V45hI0H";



}
