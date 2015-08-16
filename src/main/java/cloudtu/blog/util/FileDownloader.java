package cloudtu.blog.util;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cloudtu.blog.model.SummaryReport;

/**
 * 檔案下載器<p/>
 * 
 * 注：一定要在主程式(main) 的最後一行加上 FileDownloader.shutdown()，否則主程式會一直執行不會終止
 * 
 * @author cloudtu
 */
public class FileDownloader {
	private static final Logger logger = Logger.getLogger(FileDownloader.class);	
	
	private static final int CONCURRENT_THREAD_AMOUNT = 100;
	
	private static ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(CONCURRENT_THREAD_AMOUNT);
	
	private FileDownloader(){		
	}
	
	/**
	 * 以 multi thread 的方式進行檔案下戴
	 * 
	 * @param srcUrl
	 * @param destFilePath
	 */
	public static void download(final String srcUrl,final String destFilePath){
		executor.submit(new Runnable() {			
			@Override
			public void run() {
				try {
					logger.info(String.format("download file from [%s] to [%s]", srcUrl, destFilePath));
					FileUtils.copyURLToFile(new URL(srcUrl), new File(destFilePath), 3000, 5000);
					SummaryReport.getInstance().addDownloadFilePath(destFilePath);
				}
				catch (Exception ex) {
					logger.error(String.format("download file from [%s] fail.", srcUrl) + ex.getMessage());
				}					
			}
		});
	}
	
	public static void shutdown(){
		boolean isAllTaskFinished = false;
		while(true){
			isAllTaskFinished = (executor.getQueue().isEmpty() & (executor.getActiveCount() == 0));
			if(isAllTaskFinished){
				break;
			}
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				logger.warn(e.getMessage());
			} 				
		}
		
		try {
			if(executor != null){
				executor.shutdown();					
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}		
	}
}
