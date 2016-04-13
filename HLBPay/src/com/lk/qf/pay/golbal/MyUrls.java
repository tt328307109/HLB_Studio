package com.lk.qf.pay.golbal;

public class MyUrls {

	public static String ROOT_URL = "http://219.146.70.110:82/";//签到 消费  正式地址
	public static String ROOT_URL1 = "http://219.146.70.110:82";
	public static String ROOT_URL2 = "http://219.146.70.110:81";//图片地址
	
//	public static String ROOT_URL = "http://219.146.70.110:1234/";//测试地址
//	public static String ROOT_URL1 = "http://219.146.70.110:1234";
//	public static String ROOT_URL2 = "http://219.146.70.110:1235";//图片地址
	
	public static String ROOT_URL_USE = ROOT_URL+"user/";// 注册等
	public static String ROOT_URL_USE_TRANSFER = ROOT_URL+"transfer/";//转账
	public static String ROOT_URL_LICAI = ROOT_URL+"licai/";//理财
	public static String ROOT_URL_LOAN = ROOT_URL+"loah/";//贷款
	public static final String SUFFIX = ".ashx";
	
	public static String ROOT_URL_YIFUBAO_APP = ROOT_URL+"YifubaoPay/App/";//易付宝
	public static String ROOT_URL_BAIDU_APP = ROOT_URL+"BaifubaoPay/App/";//百付宝
	public static String ROOT_URL_WEIXIN_APP = ROOT_URL+"WeiXinPay/App/";//微信
	public static String ROOT_URL_ZHIFUBAO_APP = ROOT_URL+"AlipayPay/App/";//支付宝
	public static String ROOT_URL_SIGN = ROOT_URL+"signIn/";//签到
	public static String ROOT_URL_INDIANNA = ROOT_URL+"indianna/";//一元夺宝
	public static String ROOT_URL_CREDIT = ROOT_URL+"Credit/";//信用卡管理
	public static String ROOT_URL_TIXIAN_LIMIT = ROOT_URL+"tixian_limit/";//提现额度
	
	
	/**
	 * 轮播图片请求
	 */
	public static String VPLIST = ROOT_URL1 + "/viwepager/viwepagerList" + SUFFIX;
	
	/**
	 * 首页请求
	 */
	public static String TXMONEY = ROOT_URL_USE + "txMoney" + SUFFIX;
	/**
	 * 添加商户
	 */
	public static String MERADD = ROOT_URL_USE + "merAdd" + SUFFIX;
	
	/**
	 * 获取验证码
	 */
	public static String GETVERIFYCODE = ROOT_URL_USE+"getverifycode"+ SUFFIX;
	
	/**
	 * 上传图片
	 */
	public static String MERPHOTO = ROOT_URL_USE+"merPhoto"+ SUFFIX;
	/**
	 * 上传电子小票
	 */
	public static String RECEIPTS = ROOT_URL_USE+"receipts"+ SUFFIX;
	/**
	 * 上传各种证件
	 */
	public static String MERCERTIFICATEADD = ROOT_URL_USE+"merCertificateAdd"+ SUFFIX;
	
	/**
	 * 登录
	 */
	public static String LOGIN = ROOT_URL_USE+"login"+ SUFFIX;
	
	
	/**
	 * 修改登录密码
	 */
	public static String CHANGEPASSWORD = ROOT_URL_USE+"merActivation"+ SUFFIX;
	/**
	 * 支付密码
	 */
	public static String T0ACTIVATION = ROOT_URL_USE+"t0Activation"+ SUFFIX;
	/**
	 * 安全退出
	 */
	public static String EXITLOGIN = ROOT_URL_USE+"exitlogin"+ SUFFIX;
	/**
	 * 交易明细
	 */
	public static String QUERYPAYLIST = ROOT_URL_USE+"querypaylist"+ SUFFIX;
	/**
	 * 查询绑定的银行卡
	 */
	public static String QUERYCARDLIST = ROOT_URL_USE+"querycardlist"+ SUFFIX;
	/**
	 * 添加银行卡
	 */
	public static String ADDCARD = ROOT_URL_USE+"addcard"+ SUFFIX;
	/**
	 * 删除银行卡
	 */
	public static String DELETECARD = ROOT_URL_USE+"deletecard"+ SUFFIX;
	
	/**
	 * 银行信息
	 */
	public static String BANKLIST = ROOT_URL_USE+"bankList"+ SUFFIX;
	/**
	 * 银行支行信息
	 */
	public static String BANK2LIST = ROOT_URL_USE+"bank2List"+ SUFFIX;
	/**
	 * 银行支行信息
	 */
	public static String ZHIHANGLIST = ROOT_URL_USE+"zhiHangList"+ SUFFIX;
	
	
	
	/**
	 * 公告
	 */
	public static String NOTICE = ROOT_URL_USE+"Notice"+SUFFIX;
	/**
	 * 超级转账
	 */
	public static String SUPERTRANSFER = ROOT_URL_USE+"supertransfer"+SUFFIX;
	/**
	 * 商户信息
	 */
	public static String MERVIEW = ROOT_URL_USE+"merView"+SUFFIX;
	/**
	 * 商户银行信息
	 */
	public static String MERBANKADD = ROOT_URL_USE+"merBankAdd"+SUFFIX;
	/**
	 * 商户企业信息
	 */
	public static String MERENTERPRISEADD = ROOT_URL_USE+"merEnterpriseAdd"+SUFFIX;
	/**
	 * 获取封顶 o单和代理
	 */
	public static String AGMAXLIST = ROOT_URL_USE+"agMaxList"+SUFFIX;
	/**
	 * 获取费率 商户
	 */
	public static String AGTAXLIST = ROOT_URL_USE+"agTaxList"+SUFFIX;
	/**
	 * 设置费率o单 代理
	 */
	public static String AGTAXADD = ROOT_URL_USE+"agTaxAdd"+SUFFIX;
	
	/**
	 * 获取费率 商户
	 */
	public static String MCC = ROOT_URL_USE+"MCC"+SUFFIX;
	/**
	 * 设置费率 商户
	 */
	public static String MERTAXADD = ROOT_URL_USE+"merTaxAdd"+SUFFIX;
	
	/**
	 * 提现 T0民生代付
	 */
	public static String MINSHENGPAY = ROOT_URL_USE+"minshengpay"+SUFFIX;
	/**
	 * 提现  额度申请
	 */
	public static String TIXIANLIMIT = ROOT_URL_TIXIAN_LIMIT+"limitedu"+SUFFIX;
	/**
	 * 提现  查询额度
	 */
	public static String LIMITEDU = ROOT_URL_TIXIAN_LIMIT+"limitedu"+SUFFIX;
	/**
	 * 提现 T0
	 */
	public static String T0TIXIAN = ROOT_URL_USE+"t0TiXian"+SUFFIX;
	/**
	 * 提现 T1
	 */
	public static String T1TIXIAN = ROOT_URL_USE+"t1TiXian"+SUFFIX;
	
	/**
	 * 查询提现记录
	 */
	public static String TXLIST = ROOT_URL_USE+"txList"+SUFFIX;
	
	/**
	 * 交易记录
	 */
	public static String TRADELISTMANAGE = ROOT_URL_USE+"tradeListManage"+SUFFIX;
	/**
	 * 交易记录
	 */
	public static String SELECTTRADELIST = ROOT_URL_USE+"selectTradeList"+SUFFIX;
	/**
	 * 钱包 商户版查询余额
	 */
	public static String MERACCOUNT = ROOT_URL_USE+"merAccount"+SUFFIX;
	/**
	 * 分润
	 */
	public static String FENRUN = ROOT_URL_USE+"fenrun"+SUFFIX;
	/**
	 * 调单查询
	 */
	public static String ORDTRLIST = ROOT_URL_USE+"ordtrlist"+SUFFIX;
	/**
	 * 赎回 充值
	 */
	public static String LICAI_IN_OUT = ROOT_URL_USE+"Licai_Op"+SUFFIX;
	/**
	 * 机具查询
	 */
	public static String HANGPOSLIST = ROOT_URL_USE+"hangPosList"+SUFFIX;
	/**
	 * 绑定设备
	 */
	public static String HANDPOS = ROOT_URL_USE+"handPos"+SUFFIX;
	/**
	 * 累计收益记录
	 */
	public static String LICAI_SHOUYILIST = ROOT_URL_USE+"licai_shouyilist"+SUFFIX;
	/**
	 * 理财记录
	 */
	public static String LICAI_SELECT = ROOT_URL_USE+"licai_select"+SUFFIX;
	
	/**
	 * 理财首页
	 */
	public static String LICAI_DATA = ROOT_URL_USE+"licai_data"+SUFFIX;
	
	/**
	 * 实名查询
	 */
	public static String REALMANGE = ROOT_URL_USE+"realmange"+SUFFIX;
	/**
	 * 客服
	 */
	public static String MEROPINIONADD = ROOT_URL_USE+"merOpinionAdd"+SUFFIX;
	/**
	 * 客服历史问题
	 */
	public static String MEROPINIONLIST = ROOT_URL_USE+"MerOpinionList"+SUFFIX;
	
	
	/**
	 * 终端签到
	 */
	public static String SIGN = ROOT_URL+"sign"+SUFFIX;
	/**
	 * 支付
	 */
	public static String POS_PAY = ROOT_URL+"pospay"+SUFFIX;
	/**
	 * nfc支付
	 */
	public static String POS_PAY_NFC = ROOT_URL+"nfcpospay"+SUFFIX;
	
	/**
	 * 省
	 */
	public static String PROVINCE = ROOT_URL_USE + "province"+SUFFIX;
	/**
	 * 市
	 */
	public static String CITY = ROOT_URL_USE + "city"+SUFFIX;
	/**
	 * 区
	 */
	public static String COUNTY = ROOT_URL_USE + "county"+SUFFIX;
	
	/**
	 * 交易分润
	 */
	public static String MERFENRUN = ROOT_URL_USE + "merFenRun"+SUFFIX;
	
	/**
	 * 装机分润
	 */
	public static String ZHUANGJIJIANG = ROOT_URL_USE + "zhuangjijiang"+SUFFIX;
	/**
	 * 装机提现
	 */
	public static String ZJTIXIAN = ROOT_URL_USE + "zjtixian"+SUFFIX;
	/**
	 * 分润提现
	 */
	public static String FENRUNTIXIAN = ROOT_URL_USE + "fenruntixian"+SUFFIX;
	/**
	 * 三级分销提现明细查询
	 */
	public static String WITHDRAWALLIST = ROOT_URL_USE + "withdrawalList"+SUFFIX;
	
	/**
	 * 三级分销查询金牌银牌个数
	 */
	public static String MERLEVEL = ROOT_URL_USE + "merLevel"+SUFFIX;
	/**
	 * 查询升级金额
	 */
	public static String UPLEVELPARAM = ROOT_URL_USE + "uplevelparam"+SUFFIX;
	
	/**
	 * 升级
	 */
	public static String UPLEVEL = ROOT_URL_USE + "uplevel"+SUFFIX;
	/**
	 * pos贷 补全数据
	 */
	public static String POSLOANADD = ROOT_URL_USE + "posLoanAdd"+SUFFIX;
	/**
	 * pos贷添加订单
	 */
	public static String LOANADD = ROOT_URL_USE + "loanAdd"+SUFFIX;
	
	/**
	 * pos贷详细
	 */
	public static String LOANLIST = ROOT_URL_USE + "loanList"+SUFFIX;
	
	/**
	 * 获取消费总金额
	 */
	public static String MPOSPERCOUNT = ROOT_URL_USE + "mposPerCount"+SUFFIX;
	/**
	 * 押金赎回
	 */
	public static String YAJINSHUHUI = ROOT_URL_USE + "yaJinShuHui"+SUFFIX;
	/**
	 * 获取商户名字
	 */
	public static String USERINFO = ROOT_URL_USE_TRANSFER + "userinfo"+SUFFIX;
	/**
	 * 转账
	 */
	public static String TRANSFER_OP = ROOT_URL_USE_TRANSFER + "transfer_Op"+SUFFIX;
	/**
	 * 转账明细
	 */
	public static String TRANSFER_DETAIL = ROOT_URL_USE_TRANSFER + "transfer_Detail"+SUFFIX;
	
	/**
	 * 查询常用转账列表
	 */
	public static String COMMONUSER = ROOT_URL_USE_TRANSFER + "CommonUser"+SUFFIX;
	
	/**
	 * 添加转账人
	 */
	public static String ADDCOMMUSER = ROOT_URL_USE_TRANSFER + "addcommuser"+SUFFIX;
	
	
	
	/**
	 * 添加信用卡
	 */
	public static String CREDITCARDADD = ROOT_URL_USE + "creditCardAdd"+SUFFIX;
	/**
	 * 根据卡号获取信用卡银行名
	 */
	public static String BANKSELECT = ROOT_URL_USE + "bankSelect"+SUFFIX;
	/**
	 * 获取用户信用卡列表
	 */
	public static String CREDITCARDLIST = ROOT_URL_USE + "creditCardList"+SUFFIX;
	/**
	 * 还款
	 */
	public static String CREDITCARDHUAN = ROOT_URL_USE + "creditCardHuan"+SUFFIX;
	/**
	 * 还款列表
	 */
	public static String CREDITCARDVIEW = ROOT_URL_USE + "CreditCardView"+SUFFIX;
	/**
	 * 删除信用卡
	 */
	public static String CREDITCARDDELETE = ROOT_URL_USE + "creditCardDelete"+SUFFIX;
	
	
	
	/**
	 * 新理财 理财产品
	 */
	public static String LICAIPROLIST = ROOT_URL_LICAI + "licaiprolist"+SUFFIX;
	/**
	 * 新理财 理财记录
	 */
	public static String LICAIPROUSER = ROOT_URL_LICAI + "licaiprouser"+SUFFIX;
	/**
	 * 新理财 买入理财
	 */
	public static String PAYLICAI = ROOT_URL_LICAI + "paylicai"+SUFFIX;
	
	
	
	
	/**
	 * 申请保理贷
	 */
	public static String BL_LOAHADD = ROOT_URL_LOAN + "bl_loahAdd"+SUFFIX;
	/**
	 * 查询授信额度  保理贷
	 */
	public static String LINESCOUNT = ROOT_URL_LOAN + "linesCount"+SUFFIX;
	
	/**
	 *  保理贷列表
	 */
	public static String BL_LOAHLIST = ROOT_URL_LOAN + "bl_loahList"+SUFFIX;
	/**
	 *  保理贷还款
	 */
	public static String BL_HUAN = ROOT_URL_LOAN + "bl_huan"+SUFFIX;
	/**
	 *  保理贷还款记录
	 */
	public static String BL_HUANLIST = ROOT_URL_LOAN + "bl_huanList"+SUFFIX;
	
	
	
	/**
	 * 易付宝二维码
	 */
	public static String YFB_CREATECODE = ROOT_URL_YIFUBAO_APP + "CreateCode"+SUFFIX;
	/**
	 * 易付宝扫描二维码
	 */
	public static String SCANCODE = ROOT_URL_YIFUBAO_APP + "ScanCode"+SUFFIX;
	/**
	 * 易付宝订单查询是否成功
	 */
	public static String QUERYORDER = ROOT_URL_YIFUBAO_APP + "QueryOrder"+SUFFIX;
	/**
	 * 三方支付 订单详情
	 */
	public static String OTHERTRADEDETAIL = ROOT_URL + "OtherTradeDetail"+SUFFIX;
	
	/**
	 * 百付宝  生成二维码
	 */
	public static String BAIDU_GETCODE = ROOT_URL_BAIDU_APP + "GetCode"+SUFFIX;
	/**
	 * 百付宝  扫码支付
	 */
	public static String BAIDU_SCANCODEPAY = ROOT_URL_BAIDU_APP + "ScanCodePay"+SUFFIX;
	/**
	 * 百付宝  查询订单状态
	 */
	public static String BAIDU_QUERYORDER = ROOT_URL_BAIDU_APP + "QueryOrder"+SUFFIX;
	
	/**
	 * 微信 二维码
	 */
	public static String WEIXIN_NATIVEPAYPAGE = ROOT_URL_WEIXIN_APP + "NativePayPage"+SUFFIX;
	/**
	 * 微信 扫描二维码
	 */
	public static String WEIXIN_MICROPAYPAGE = ROOT_URL_WEIXIN_APP + "MicroPayPage"+SUFFIX;
	/**
	 * 微信  查询订单状态
	 */
	public static String WEIXIN_ORDERPAYPAGE = ROOT_URL_WEIXIN_APP + "OrderQueryPage"+SUFFIX;
	
	/**
	 * 支付宝二维码
	 */
	public static String ZHIFUBAO_NATIVEPAYPAGE = ROOT_URL_ZHIFUBAO_APP + "NativePayPage"+SUFFIX;
	/**
	 * 支付宝 扫描二维码
	 */
	public static String ZHIFUBAO_MICROPAYPAGE = ROOT_URL_ZHIFUBAO_APP + "MicroPayPage"+SUFFIX;
	/**
	 * 支付宝  查询订单状态
	 */
	public static String ZHIFUBAO_ORDERPAYPAGE = ROOT_URL_ZHIFUBAO_APP + "QueryState"+SUFFIX;
	
	
	
	
	/**
	 * 签到得快易币
	 */
	public static String SIGNIN_SIGNIN = ROOT_URL_SIGN + "signIn"+SUFFIX;
	/**
	 * 夺宝商品列表
	 */
	public static String ROOT_URL_INDIANA_LIST = ROOT_URL_INDIANNA + "indiana_list"+SUFFIX;
	/**
	 * 夺宝下单
	 */
	public static String ROOT_URL_INDIANA_INDANA_INORDER= ROOT_URL_INDIANNA + "indiana_inOrder"+SUFFIX;
	/**
	 * 加入购物车
	 */
	public static String ROOT_URL_INDIANA_CART= ROOT_URL_INDIANNA + "indiana_Cart"+SUFFIX;
	
	/**
	 * 充值快易币
	 */
	public static String ROOT_URL_DBB_EXCHANGE = ROOT_URL_INDIANNA + "dbb_Exchange"+SUFFIX;
	/**
	 * 中奖记录
	 */
	public static String ROOT_URL_WINNINGLIST = ROOT_URL_INDIANNA + "winningList"+SUFFIX;
	/**
	 * 最新揭晓记录
	 */
	public static String ROOT_URL_WILL_LOTTERY = ROOT_URL_INDIANNA + "will_lottery"+SUFFIX;
	/**
	 * 收货地址列表
	 */
	public static String ROOT_URL_ADRESSLIST = ROOT_URL_INDIANNA + "adressList"+SUFFIX;
	/**
	 * 编辑收货地址
	 */
	public static String ROOT_URL_ADRESSEIDT = ROOT_URL_INDIANNA + "addressEidt"+SUFFIX;
	/**
	 * 删除收货地址
	 */
	public static String ROOT_URL_ADRESSDELE = ROOT_URL_INDIANNA + "adressDele"+SUFFIX;
	/**
	 * 添加兑换订单
	 */
	public static String ROOT_URL_DELIVERYEDIT = ROOT_URL_INDIANNA + "deliveryEdit"+SUFFIX;
	
	/**
	 * 查看中奖地址
	 */
	public static String ROOT_URL_ADRESSVIEW = ROOT_URL_INDIANNA + "adressView"+SUFFIX;
	
	/**
	 * 确认收货
	 */
	public static String ROOT_URL_QURENSHOUHUO = ROOT_URL_INDIANNA + "qurenshouhuo"+SUFFIX;
	/**
	 * 夺宝记录
	 */
	public static String ROOT_URL_INDIANA_BUY_HISTORY_INFO = ROOT_URL_INDIANNA + "indiana_buy_history_info"+SUFFIX;
	/**
	 * 查询本产品所有用户
	 */
	public static String ROOT_URL_INDIANA_GOING_OR_PUBLISH = ROOT_URL_INDIANNA + "indiana_going_or_publish"+SUFFIX;
	/**
	 * 查询夺宝号
	 */
	public static String ROOT_URL_INDIANA_GET_DBNUMBER = ROOT_URL_INDIANNA + "indiana_get_dbnumber"+SUFFIX;
	/**
	 * 查询商品详情
	 */
	public static String ROOT_URL_INDIANA_GET_ORDER_DETAIL = ROOT_URL_INDIANNA + "indiana_get_order_detail"+SUFFIX;
	/**
	 * 同意服务协议
	 */
	public static String ROOT_URL_INDIANA_XIEYI = ROOT_URL_INDIANNA + "indianaXieYi"+SUFFIX;
	/**
	 * 添加晒单记录列表
	 */
	public static String ROOT_URL_INDIANA_ADD_SDRECORD = ROOT_URL_INDIANNA + "shaidanAdd"+SUFFIX;
	/**
	 * 加载晒单记录
	 */
	public static String ROOT_URL_INDIANA_LOAD_SDRECORD = ROOT_URL_INDIANNA + "shaidanList" + SUFFIX;
	
	
	/**
	 * 信用卡管理  接口  添加AddCard  列表SelectCard  修改UpdateCard  删除DeleteCard
	 */
	public static String ROOT_URL_CREDIT_HANDLE = ROOT_URL_CREDIT + "HandleCreditCard" + SUFFIX;
	/**
	 * 信用卡管理   查询银行名
	 */
	public static String ROOT_URL_CHECKBANK = ROOT_URL_CREDIT + "CheckBank" + SUFFIX;
	/**
	 * 信用卡管理   申请还款
	 */
	public static String ROOT_URL_REFUNDAPPLY = ROOT_URL_CREDIT + "RefundApply" + SUFFIX;
	/**
	 * 信用卡管理   信用卡还款记录
	 */
	public static String ROOT_URL_REFUNDRECORD = ROOT_URL_CREDIT + "RefundRecord" + SUFFIX;
	
	/**
	 * 支付手续费
	 */
	public static String ROOT_URL_PAYSXF = ROOT_URL_CREDIT + "PayRefundOrder" + SUFFIX;
	/**
	 * 信用卡赎回
	 */
	public static String ROOT_URL_PAYSH = ROOT_URL_CREDIT + "RedeemCreditCard" + SUFFIX;
	/**
	 * 信用卡确认收货
	 */
	public static String ROOT_URL_CREDITCARDAOG = ROOT_URL_CREDIT + "CreditCardAOG" + SUFFIX;
	/**
	 * 获取公司收件地址
	 */
	public static String ROOT_URL_GETADDRESS = ROOT_URL_CREDIT + "GetAddress" + SUFFIX;
}
