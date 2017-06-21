/*
 * 名称: Version
 * 描述: * 1.导入数据库
 * 2.setting.ini配置文件更改
 * 3.项目配置文件更改
 * 版本：  1.0.0
 * 作者： 刘峻峰
 * 修改:
 * 日期：2015年05月04日
 * 修改日期:
 */

package org.anyway.server.processor.setup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.anyway.common.ProcesserConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.utils.HexUtil;
import org.anyway.common.utils.SecretUtil;
import org.anyway.server.processor.validity.Version;

import com.nikhaldimann.inieditor.IniEditor;

public class PropertySet {
	public static final String SETCONFIG = "./cfg/properties.properties";
	public static final String MANAGECONDIF ="./webRoot/manage/WEB-INF/classes/jdbc.properties";
	public static final String BOXCONFIG ="./webRoot/box/WEB-INF/classes/conf.properties";

	public static String PATTERN = null;
	public static String DRIVER = null;
	public static String URLPRE = null;
	public static String URLSUFFIX= null;
	public static String DEFAULTNAME=null;
	public static String DEFAULTDATABASE=null;
	public static String WEBPORT=null;
	
	public static void init() throws IOException {
		IniEditor inifile = new IniEditor();
		inifile.load(SETCONFIG);
		PATTERN=inifile.get("default", "PATTERN");
		DRIVER=inifile.get("default", "DRIVER");
		URLPRE=inifile.get("default", "URLPRE");
		URLSUFFIX=inifile.get("default", "URLSUFFIX");
		DEFAULTNAME=inifile.get("default", "DEFAULTNAME");
		DEFAULTDATABASE=inifile.get("default", "DEFAULTDATABASE");
		inifile = null;
		inifile = new IniEditor();
		inifile.load(ProcesserConfig.CONFIG_FILE_NAME);
		WEBPORT=inifile.get("HTTP", "PORT");
		inifile = null;
	}
	
	public static void main(String[] args) throws IOException{	
		init();
		Connection conn = null; //数据库连接
		//控制台读入
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String[] info = null;
		System.out.println("import Mysql DataBase?(yes/no):");
		try {
			String tempread = read(br);
			if(tempread.toLowerCase().equals("y")||tempread.toLowerCase().equals("yes")){
				if((info=checkInfo(br))!=null){
					try{
						//尝试本机mysql导入
						importSql(info[2], info[3], info[0], info[4], info[1], info[6]);
						try{
							doChange(info, br);
							//是否以服务方式启动
							onServer(br);
						}catch(Exception e){
							e.printStackTrace();
							System.out.println("system  error,exit!");
							System.exit(0);
						}
					}catch (Exception e) {
						//本机未安装mysql方式导入
						conn = null;
						conn = getConnection(DRIVER, info[5], info[2], info[3]);
						boolean b = importNoClients(info, conn);
						if(b){
							try{
								doChange(info, br);
								//以服务方式启动
								onServer(br);
							}catch(Exception es){
								System.out.println("system error,exit!");
								System.exit(0);
							}
						}else{
							Thread.sleep(1000);
							System.exit(0);
						}
						conn.close();
					}
				}else{
					System.exit(0);
				}
			}else{
				System.out.println("DataBase not Import!");
				Thread.sleep(1000);
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int createDate() throws IOException{	
		File f = new File(System.getProperty("user.dir")+"/"+"key");
		if(!f.exists()){
			System.out.println("Error:Authorization File Not Found!");
			System.exit(0);
		}
		
		String date = "[usedate="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]";
		byte[] bdate=SecretUtil.Encrypt(date.getBytes(), CryptEnum.DES);
		date=HexUtil.bytesToHexString(bdate);
		//读取密钥
		String s=Version.readFile(f);
		byte[] decryptData = HexUtil.hexStringToByte(s);
		decryptData = SecretUtil.Decrypt(decryptData,CryptEnum.DES);
		s=new String(decryptData);
		
		int exp = 0;
		if(s.indexOf("exp")<0){
			System.out.println("Error:Authorization File Infomation Error!");
		}else{
			String exps=s.substring(s.indexOf("["),s.indexOf("]")+1);
			exp = Integer.parseInt(exps.substring(exps.indexOf("exp")+4,exps.indexOf("]")));
		}
		FileWriter fw = new FileWriter(f,true);
		fw.write(date);
		fw.flush();
		fw.close();
		return exp;
	}
	
	public static void onServer(BufferedReader br){
		try{
		String asServer=inputString("startup as server?(yes/no)", br);
		if(asServer.equals("y")||asServer.equals("yes")){
			File file = new File("/etc/rc.d/init.d/rycc");
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"/rycc")));
			BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			String line = null;
			while((line=read.readLine())!=null){
				if(line.indexOf("SERVER=")<0)
					write.write(line);
				else{
					line=line.substring(0, "SERVER=".length())+System.getProperty("user.dir");
					write.write(line);
				}
				write.newLine();
			}
			write.flush();
			read.close();
			write.close();
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("chmod 777 /etc/rc.d/init.d/rycc");
			runtime.exec("chkconfig --level 2345 rycc on");
			System.out.println("********************************");
			System.out.println("*                              *");
			System.out.println("*                              *");
			System.out.println("*     join server,restart      *");
			System.out.println("*                              *");
			System.out.println("*                              *");
			System.out.println("********************************");
		}else{
			System.out.println("Manual start when need!");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void doChange(String[] info,BufferedReader br) throws IOException{
		//更改setting.ini
		Map<String,Map<String,String>> map = getSetting(info, br);
		setFile(map,ProcesserConfig.CONFIG_FILE_NAME);
		boolean ishttps = (map.get("web").get("https")).equals("1");
		//更改Manage配置文件
		map = new HashMap<String, Map<String,String>>();
		Map<String,String> inner = new HashMap<String, String>();
		String manageURL="jdbc\\:mysql\\://"+info[0]+"\\:"+info[4]+"/"+info[1]+"?useUnicode\\=true&characterEncoding\\=utf-8&zeroDateTimeBehavior\\=round";
		inner.put("jdbc.mysql.url", manageURL);
		inner.put("jdbc.mysql.username", info[2]);
		inner.put("jdbc.mysql.password ", info[3]);
		map.put("manage", inner);
		setFile(map,MANAGECONDIF);
		//更改Box配置文件
		map = new HashMap<String, Map<String,String>>();
		inner = new HashMap<String, String>();
		String site=null;
		if(ishttps)
			site="https\\://"+info[8]+"\\:"+WEBPORT+"/";
		else
			site="http\\://"+info[8]+"\\:"+WEBPORT+"/";
		inner.put("server.site", site);
		map.put("box", inner);
		setFile(map,BOXCONFIG);
	}
	
	//setting.ini要更改的数据
	public static Map<String,Map<String,String>> getSetting(String[] info,BufferedReader br) throws IOException{
		Map<String,Map<String,String>> map = new HashMap<String, Map<String,String>>();
		Map<String,String> inner = new HashMap<String, String>();
		inner.put("sid", info[7]);
		inner.put("userid", info[2]);
		//加密密码
		byte[] b=SecretUtil.Encrypt(info[3].getBytes(), CryptEnum.DES);
		String pwd=HexUtil.bytesToHexString(b);
		inner.put("pwd ", pwd);
		map.put("database", inner);
		String isweb=inputString("open web?(yes/no)", br);
		String ishttps=null;
		boolean flag=isweb.equals("y")||isweb.equals("yes");
		if(flag)
			ishttps=inputString("open https?(yes/no):",br);
		inner = new HashMap<String, String>();
		inner.put("web", flag?"1":"0");
		inner.put("https", ishttps==null?"0":ishttps.equals("y")||ishttps.equals("yes")?"1":"0");
		map.put("web", inner);
		inner = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		inner.put("ver.date", sdf.format(new Date()));
		return map;
	}
	
	//判断参数
	public static boolean isNull(String s){
		if(s==null||s.trim().isEmpty())
			return true;
		else
			return false;
	}
	
	//读取控制台参数
	public static String read(BufferedReader r) throws IOException{
		return r.readLine().trim();
	}
	
	//获取数据参数
	public static String inputString(String tips,BufferedReader br) throws IOException{
		System.out.println(tips);
		return read(br);
	}
	
	//获取mysql地址
	public static String getmysqlIp(String tips,BufferedReader br) throws IOException{
		String s=inputString(tips, br);
		String ip = getRealIp();
		s=(isNull(s)?ip:(s.equals("y")||s.equals("yes"))?ip:s);
		return s;
	}
	
	//获取数据库连接
	public static Connection getConnection(String driver,String url,String user,String pwd){
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pwd);
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: Driver not found!");
			return null;
		} catch (SQLException e) {
			System.out.println("ERROR: the IP Address(/port/user/password) is Error! ");
			return null;
		}
		return conn;
	}
	
	//获取IP,数据库名,数据库用户,密码,端口号,数据库连接url,sql文件,写入setting.ini字符串,本机真实ip
	public static String[] getInfo(BufferedReader br) throws IOException{
		String[] info = new String[9];
		while(!(info[0]=getmysqlIp("mysql in localhost(yes?) or input Ip Address for remote host:", br)).matches(PATTERN)){
			System.out.println("Error Ip Address Input(example:127.0.0.1),Input Again:");
		}
		//数据库名
		info[1]=inputString("Input DataBase Name:",br);
		//数据库用户名
		info[2]=inputString("Input DataBase User:",br);
		//数据库密码
		info[3]=inputString("Input DataBase password:",br);
		//数据库端口号
		info[4]=inputString("Input DataBase port/entry to use default port:",br);
		info[4] = isNull(info[4])?"3306":info[4];
		//默认数据库连接url
		info[5]=URLPRE+info[0]+":"+info[4]+DEFAULTDATABASE+URLSUFFIX;
		//sql文件路径
		info[6]=inputString("Input Absolutely File Path/entry to use default Path:",br);
		info[6] = isNull(info[6])?System.getProperty("user.dir")+"/"+DEFAULTNAME:info[6];
		info[7]=URLPRE+info[0]+":"+info[4]+"/"+info[1]+URLSUFFIX;
		info[8]=getRealIp();
		return info;
	}
	
	//验证输入正确性
	public static String[] checkInfo(BufferedReader br) throws IOException {
		String[] rt		= getInfo(br);
		Connection conn	= getConnection(DRIVER, rt[5], rt[2], rt[3]);
		int tryCount = 0;
		while(conn==null&&tryCount<2){
			tryCount++;
			rt=getInfo(br);
			conn=getConnection(DRIVER, rt[5], rt[2], rt[3]);
		}
		if(tryCount>=3||conn==null){
			System.out.println("Infomation Error or mysql service not start,system exit!");
			rt=null;
		}
		File f = new File(rt[6]);
		if(!f.exists()) {
			System.out.println("ERROR: File not found!");
			return null;
		}
		return rt;
	}
	
	//本地没有mysql数据库脚本方式导入
	/*
	 * 此方式导入两处更改：
	 * 1.导出文件中不能存在  多行注释嵌套单行注释，否则会执行出错。
	 * 2.将CHARSET utf8替换为空
	 * 
	 * 以数据库插入方式导入，执行速度慢
	 */
	public static boolean importNoClients(String[] info,Connection conn) throws IOException, SQLException{
		System.out.println("waiting...");
		BufferedReader read = null;
		read = new BufferedReader(new InputStreamReader(new FileInputStream(info[6]),"UTF-8"));
		StringBuffer buff = new StringBuffer("");
		String s=null;
		Statement stat=null;
		try {
			stat = conn.createStatement();
			stat.execute("Drop Database if Exists "+info[1]);
			stat.execute("Create Database If Not Exists "+info[1]+" Character Set UTF8");
			stat.execute("use "+info[1]);
			while((s=read.readLine())!=null){
				if(s.trim().length()==0||s.startsWith("--"))continue;
				if(s.indexOf("CHARSET utf8")!=-1) s=s.replace("CHARSET utf8", " ");
				if(s.endsWith("DELIMITER ;;")){
					while((s=read.readLine())!=null){
						if(s.trim().length()==0)continue;
						if(s.indexOf("#")!=-1){
							s=s.replaceFirst("#", "/*");
							s=s+"*/";
						}
						if(s.indexOf("CHARSET utf8")!=-1) s=s.replace("CHARSET utf8", " ");
						if(!s.endsWith(";;")){
							buff.append(s);
							continue;
						}else{
							buff.append(s);
							stat.execute(buff.toString());
							buff.delete(0, buff.length());
							s=read.readLine();
							break;
						}
					}
				}
				if(s.endsWith("DELIMITER ;")){
					continue;
				}
				if(!s.endsWith(";")){
					buff.append(s);
					continue;
				}else{
					buff.append(s);
					stat.execute(buff.toString());
					buff.delete(0, buff.length());
				}
			}
			int days=createDate();
			stat.execute("update `ry_version_info` set SERVER_EXPIRE="+days+",SERVER_CREATE=sysdate()");
			read.close();
			System.out.println("success!");
			stat.close();
			return true;
		} catch (SQLException e) {
			stat.execute("Drop Database if Exists "+info[1]);
			read.close();
			stat.close();
			System.out.println("ERROR: database import failed!");
			return false;
		} 
	}
	
	//本地安装有mysql导入方式
	/**
	 * 根据属性文件的配置把指定位置的指定文件内容导入到指定的数据库中
	 * 在命令窗口进行mysql的数据库导入一般分三步走：
	 * 第一步是登到到mysql； mysql -uusername -ppassword -hhost -Pport -DdatabaseName;如果在登录的时候指定了数据库名则会
	 * 直接转向该数据库，这样就可以跳过第二步，直接第三步； 
	 * 第二步是切换到导入的目标数据库；use importDatabaseName；
	 * 第三步是开始从目标文件导入数据到目标数据库；source importPath；
	 * @param properties
	 * @throws IOException 
	 */
	public static void importSql(String dbUser,String dbPwd,String dbIp,String dbport,String dbName,String filePath) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		//因为在命令窗口进行mysql数据库的导入一般分三步走，所以所执行的命令将以字符串数组的形式出现
		String cmdarray[] = getImportCommand(dbUser,dbPwd,dbIp,dbport,dbName,filePath);//根据属性文件的配置获取数据库导入所需的命令，组成一个数组
		//runtime.exec(cmdarray);//这里也是简单的直接抛出异常
		Process process = runtime.exec(cmdarray[0]);
		//执行了第一条命令以后已经登录到mysql了，所以之后就是利用mysql的命令窗口
		//进程执行后面的代码
		OutputStream os = process.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		//命令1和命令2要放在一起执行
		//执行结束后更新表
		int days = createDate();
		String upversion = "update `ry_version_info` set SERVER_EXPIRE="+days+",SERVER_CREATE=sysdate()";
		writer.write(cmdarray[1] + "\r\n" + cmdarray[2]+ "\r\n" + cmdarray[3]+ "\r\n" + cmdarray[4]+ "\r\n"+upversion);
		writer.flush();
		writer.close();
		os.close();
	}
	
	/**
	 * 根据属性文件的配置，分三步走获取从目标文件导入数据到目标数据库所需的命令
	 * 如果在登录的时候指定了数据库名则会
	 * 直接转向该数据库，这样就可以跳过第二步，直接第三步； 
	 * @param properties
	 * @return
	 */
	private static String[] getImportCommand(String dbUser,String dbPwd,String dbIp,String dbport,String dbName,String filePath) {
		String username = dbUser;//用户名
		String password = dbPwd;//密码
		String host = dbIp;//导入的目标数据库所在的主机
		String port = dbport;//使用的端口号
		String importDatabaseName = dbName;//导入的目标数据库的名称
		String importPath = filePath;//导入的目标文件所在的位置
		//第一步，获取登录命令语句
		String loginCommand = new StringBuffer().append("mysql -u").append(username).append(" -p").append(password).append(" -h").append(host)
		.append(" -P").append(port).append(" --default-character-set=utf8 ").toString();
		//删除原有数据库
		/**
		 * 直接删除原有同名数据库
		 * 危险操作
		 */
		String DropDBCommand = new StringBuffer("Drop Database if Exists ").append(importDatabaseName).append(";").toString();
		//创建数据库
		String createDBCommand = new StringBuffer("Create Database If Not Exists ").append(importDatabaseName).append(";").toString();
		//第二步，获取切换数据库到目标数据库的命令语句
		String switchCommand = new StringBuffer("use ").append(importDatabaseName).append("").toString();
		//第三步，获取导入的命令语句
		String importCommand = new StringBuffer("source ").append(importPath).append("").toString();
		//需要返回的命令语句数组
		String[] commands = new String[] {loginCommand , DropDBCommand , createDBCommand, switchCommand, importCommand};
		return commands;
	}
	
	 public static void writeData(String key, String value,String filePath) {
        Properties prop = new Properties();
        InputStream fis = null;
        OutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();//一定要在修改值之前关闭fis
            fos = new FileOutputStream(file);
            prop.setProperty(key, value);
            prop.store(fos, "Update '" + key + "' value");
            fos.close();
            
        } catch (IOException e) {
            System.err.println("Visit " + filePath + " for updating "
            + value + " value error");
        } 
        finally{
            try {
                fos.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	 
	 //保存配置文件
	public static void setFile(Map<String, Map<String,String>> map,String file) throws IOException{
		IniEditor inifile = new IniEditor();
		inifile.load(file);
		Iterator<String> it = map.keySet().iterator();
		Iterator<String> itk =null;
		String node =null;
		Map<String,String> keymap = null;
		while(it.hasNext()){
			node = it.next();
			keymap=map.get(node);
			itk=keymap.keySet().iterator();
			while(itk.hasNext()){
				String key = itk.next();
				inifile.set(node, key , keymap.get(key));
			}
		}
		inifile.save(file);
		inifile = null;
	}
	
	//获取本机ip地址
	public static String getRealIp() throws SocketException {
		// 本地IP，如果没有配置外网IP则返回它
		String localip = null; 
		// 外网IP
		String netip = null;

		Enumeration<NetworkInterface> netInterfaces = 
			NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		// 是否找到外网IP
		boolean finded = false;
		while (netInterfaces.hasMoreElements() && !finded) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() 
						&& !ip.isLoopbackAddress() 
						// 内网IP
						&& ip.getHostAddress().indexOf(":") == -1) {
					netip = ip.getHostAddress();
					finded = true;
					break;
				} else if (ip.isSiteLocalAddress() 
						&& !ip.isLoopbackAddress() 
						&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
					localip = ip.getHostAddress();
				}
			}
		}
	
		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}
}
