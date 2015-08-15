package cloudtu.blog;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JbakeConverter {
	private static final Logger logger = Logger.getLogger(JbakeConverter.class);

	private static final int CONCURRENT_THREAD_AMOUNT = 100;
	
	private List<Article> articles;
	
	public JbakeConverter(List<Article> articles) {
		this.articles = articles;
	}

	public void convertToFile(final String outputFolderPath) throws Exception {
		ThreadPoolExecutor executor = null;
		try {
			// 先把舊資料清掉
			if(new File(outputFolderPath).exists()){
				FileUtils.deleteDirectory(new File(outputFolderPath));				
			}			
			
			executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(CONCURRENT_THREAD_AMOUNT);					
			
			for (Article article : articles) {
				final String year = DateFormatUtils.format(article.getDate(), "yyyy");
				final String month = DateFormatUtils.format(article.getDate(), "MM");
				String destSavedImgFileNamePrefix = DateFormatUtils.format(article.getDate(), "yyyyMMddHHmm") + "_";
				
				Document doc = Jsoup.parseBodyFragment(article.getContent());			
				
				// 處理 <img> tag
				Elements imgs = doc.select("img");
				int imgFileIndex = 1;
				for (Element img : imgs) {
					String tmpImgUrl = img.attr("src");
					// 當 <img> 被 <a> 包起來(e.g. <a><img/></a>) 時，取 <a> 裡的照片網址  					
					// href attribute 副檔名長度為 3 代表是圖檔網址
					if(img.parent().tagName().equalsIgnoreCase("a") && StringUtils.substringAfterLast(img.parent().attr("href"), ".").length() == 3){
						tmpImgUrl = img.parent().attr("href");
					}					
					final String srcImgUrl = tmpImgUrl; //圖片來源網址
					final String destSavedImgFileName = destSavedImgFileNamePrefix + imgFileIndex + "." + StringUtils.substringAfterLast(srcImgUrl, "."); //圖片存檔時的檔名
					imgFileIndex++;
					
					// <img> 裡的圖片檔存到 local
					executor.submit(new Runnable() {						
						@Override
						public void run() {											
							try {
								String destSavedImgFilePath = String.format("%s/img/%s/%s/%s", outputFolderPath, year, month, destSavedImgFileName);
								logger.info(String.format("save image file : [%s] to [%s]", srcImgUrl, destSavedImgFilePath));
								FileUtils.copyURLToFile(new URL(srcImgUrl),	new File(destSavedImgFilePath), 3000, 5000);
							}
							catch (Exception ex) {
								logger.error(String.format("get image file from [%s] fail.", srcImgUrl) + ex.getMessage());
							}							
						}
					});
					
					// 因為圖檔存到 local 時被改名了，所以 dom 裡的圖檔網址要改掉					
					String updatedImgUrl = String.format("/img/%s/%s/%s", year, month, destSavedImgFileName); 

					// 改成 <img src="{updatedImgUrl}">
					img.attr("src", updatedImgUrl);
					
					// 如果 <img> 外層包了 <a>(e.g. <a><img/><a>)，<a> 的內容改成  <a href="{updatedImgUrl}">		
					if(img.parent().tagName().equalsIgnoreCase("a")){
						img.parent().attr("href", updatedImgUrl);
					}
				}
				
				// 處理 <pre class="brush: xxx"> tag，讓它變成 <pre class="prettyprint"><code></code></pre>
				Elements pres = doc.select("pre[class^=brush:]");
				for (Element pre : pres) {
					pre.attr("class", "prettyprint");
					pre.html("<code>" + pre.html() + "</code>");
				}
				
				// 處理 <span style="color: xxx"> tag，讓它變成 <code> tag
				Elements spans = doc.select("span[style^=color]");
				for (Element span : spans) {
					span.removeAttr("style");
					span.tagName("code");
				}
				
				// 處理 <font color="xxx"> tag，讓它變成 <code> tag
				Elements fonts = doc.select("font");
				for (Element font : fonts) {
					font.removeAttr("color");
					font.tagName("code");
				}
				
				article.setContent(doc.body().html());
								
				// title 裡包含 "=" 會讓 jbake 產生文章標題時出錯，將它換成 "is"
				if(article.getTitle().contains("=")){									
					String fixedArticleTitle = article.getTitle().replaceAll("=", "is");
					logger.info(String.format("change artile title : [%s] to [%s]", article.getTitle(), fixedArticleTitle));
					article.setTitle(fixedArticleTitle);
				}
				
				StringBuilder jbakeFormatHtmlContent = new StringBuilder();				
				jbakeFormatHtmlContent.append("title=").append(article.getTitle()).append("\n")
								.append("date=").append(DateFormatUtils.format(article.getDate(), "yyyy-MM-dd HH:mm")).append("\n")
								.append("type=post\n")
								.append("tags=").append(article.getTagsAsString().toLowerCase().replaceAll(" ", "-")).append("\n")
								.append("status=published\n")
								.append("~~~~~~\n\n")
								.append(article.getContent());
				String savedHtmlFilePath = String.format("%s/blog/%s/%s/%s", outputFolderPath, year, month, article.getFileName());
				logger.info(String.format("save html file : [%s]", savedHtmlFilePath));
				FileUtils.writeStringToFile(new File(savedHtmlFilePath), jbakeFormatHtmlContent.toString(), Charset.forName("UTF-8"));
			}			
			logger.info("save html file amount : " + articles.size());
			
			boolean isAllImgSaved = false;
			while(true){
				isAllImgSaved = (executor.getQueue().isEmpty() & (executor.getActiveCount() == 0));
				if(isAllImgSaved){
					break;
				}
				
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					logger.warn(e.getMessage());
				} 				
			}			
		}
		finally{
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
}
