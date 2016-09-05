/*
 * 名称: SeqidCacheRunnable
 * 描述: 保存自增序号线程
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年8月28日
 * 修改日期:
 */

package org.anyway.server.web.cache.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

import org.anyway.common.uGlobalVar;
import org.anyway.cache.ehcache.EhCacheWrapper;
import org.anyway.server.web.cache.CacheManager;

public class SeqidCacheRunnable implements Runnable {

	public void run() {
		while (true) {
			try {
				EhCacheWrapper<String, Integer> cache = CacheManager.getInstance().getConfigCache().getSeqIdCache();
				if (null != cache) {
					// 开启指定的文件
					File file = new File(uGlobalVar.AppPath + "/data/seqid.txt");
					if (file.exists() == false) {
						file.createNewFile();
					}
			        BufferedWriter buf = new BufferedWriter(new FileWriter(file));   
					for (Object key : cache.getCache().getKeys()) {
						String sb = String.valueOf(key) + "\t" + cache.get((String)key) + "\r\n";
						// 将文字编辑区的文字写入文件
				        buf.write(sb);
					}
					buf.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				TimeUnit.SECONDS.sleep(30); //停留半分钟
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // 这里设置保存时间
		}

	}
}
