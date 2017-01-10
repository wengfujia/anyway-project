/*
 * 名称: Version
 * 描述: 根据时限，版本有效期控制
 * 版本：  1.0.0
 * 作者： 刘峻峰
 * 修改:
 * 日期：2015年05月04日
 * 修改日期:
 */

package org.anyway.server.dbase.validity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.anyway.common.enums.CryptEnum;
import org.anyway.common.utils.uHexUtil;
import org.anyway.common.utils.uSecretUtil;

public class Version {
	public static boolean check(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			File f = new File(System.getProperty("user.dir")+"/key");
			String s = readFile(f);
			if(s==null){
				return false;
			}
			
			byte[] decryptData = uHexUtil.hexStringToByte(s);
			decryptData = uSecretUtil.Decrypt(decryptData,CryptEnum.DES);
			s = new String(decryptData);
			if(s.indexOf("exp")<0 || s.indexOf("usedate")<0){
				System.out.println("Error:Authorization File Infomation Error!");
				return false;
			}else{
				String now = sdf.format(new Date());
				String exps=s.substring(s.indexOf("["),s.indexOf("]")+1);
				int exp = Integer.parseInt(exps.substring(exps.indexOf("exp")+4,exps.indexOf("]")));
				s=s.substring(s.indexOf("]")+1);
				String usedate = s.substring(s.indexOf("usedate")+8,s.indexOf("]"));
				int days = daysBetween(usedate,now);
				if(days>exp||days<0){
					System.out.println("Error: Service expired!");
					return false;
				}
				String lastuse = null;
				if(s.indexOf("lastuse")>=0){
					s=s.substring(s.indexOf("]")+1);
					lastuse=s.substring(s.indexOf("lastuse")+8,s.indexOf("]"));
					Date d1=sdf.parse(now);
					Date d2=sdf.parse(lastuse);
					if(d1.before(d2)){
						System.out.println("Error!");
						return false;
					}
				}
				return true;
			}
		}catch(Exception e){
			System.out.println("Error:Certificate error!");
			return false;
		}
	}
	public static String readFile(File f) throws IOException{
		if(!f.exists()){
			System.out.println("Error:Authorization File Not Found!");
			return null;
		}
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(f));
		String s=null;
		StringBuffer sbuff = new StringBuffer();
		while((s=br.readLine())!=null){
			sbuff.append(s);
		}
		s=sbuff.toString();
		return s;
	}
    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws IOException 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }    
      
    /** 
     *字符串的日期格式的计算 
     */  
    public static int daysBetween(String smdate,String bdate) throws ParseException{  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sdf.parse(smdate));    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sdf.parse(bdate));    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    } 

	public static void serverUseDate() throws IOException{
		File f = new File(System.getProperty("user.dir")+"/key");
		String s = readFile(f);
		if(s==null){
			System.out.println("Error:Authorization File Not Found!");
			System.exit(0);
		}
		byte[] decryptData = uHexUtil.hexStringToByte(s);
		decryptData = uSecretUtil.Decrypt(decryptData,CryptEnum.DES);
		s=new String(decryptData);
		if(s.indexOf("lastuse")<0){
			s = s+"[lastuse="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]";
		}else{
			s = s.substring(0, s.indexOf("lastuse"))+"lastuse="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]";
		}
		byte[] bt=uSecretUtil.Encrypt(s.getBytes(), CryptEnum.DES);
		s=uHexUtil.bytesToHexString(bt);
		if(f.exists())
			f.delete();
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.flush();
		fw.close();
	}

}
