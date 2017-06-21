/*
 * 名称: ExpireCreate
 * 描述: 有效期生成类
 * 版本：  1.0.0
 * 作者： 刘峻峰
 * 修改:
 * 日期：2015年05月04日
 * 修改日期:
 */

package org.anyway.server.processor.validity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.anyway.common.enums.CryptEnum;
import org.anyway.common.utils.HexUtil;
import org.anyway.common.utils.SecretUtil;

public class ExpireCreate {
    //创建密钥文件
	public static void createCertFile(int expDate) throws IOException {
		String date = "[usedate="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]";
		String usedays = "[exp="+Integer.toString(expDate)+"]";
		String sourceStr = usedays+date;
		
		byte[] sourceBtye=SecretUtil.Encrypt(sourceStr.getBytes(), CryptEnum.DES);
		String encStr= HexUtil.bytesToHexString(sourceBtye);
			
		File f = new File(System.getProperty("user.dir")+"/"+"key");
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.write(encStr);
		fw.flush();
		fw.close();
	}
	
    public static void main(String args[]) throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	String s=null;
    	boolean flag = true;
    	while(flag){
    		System.out.println("Input A number for expired days:");
    		s=br.readLine();
    		if(s==null||s.trim().isEmpty()){
    			System.out.println("Valid for 90 days!");
    			s="90";
    			flag = false;
    		}else if(!s.matches("^\\d+$")){
    			System.out.println("Not A Number!");
    			continue;
    		}
    		flag = false;
    	}
    	createCertFile(Integer.parseInt(s));
    }
}
