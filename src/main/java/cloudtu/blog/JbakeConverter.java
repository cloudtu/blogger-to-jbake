package cloudtu.blog;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cloudtu.blog.model.Post;
import cloudtu.blog.model.SummaryReport;
import cloudtu.blog.util.AppConfig;
import cloudtu.blog.util.FileDownloader;

/**
 * 將  blog post(blog 發布文章) 轉換成 Jbake 格式檔案
 * 
 * @author cloudtu
 */
public class JbakeConverter {
	private static final Logger logger = Logger.getLogger(JbakeConverter.class);
	
	private static final String MY_BLOGGER_URL;
	
	static{
		String tempMyBloggerUrl = AppConfig.getString("myBloggerUrl");
		if(!tempMyBloggerUrl.contains("http://")){
			tempMyBloggerUrl = "http://" + tempMyBloggerUrl; 
		}
		tempMyBloggerUrl = StringUtils.substringBeforeLast(tempMyBloggerUrl, ".");
		
		// 設定檔裡的值可能是 "http://{myBlogName}.blogspot.com/" 或 "{myBlogName}.blogspot.tw" 這種格式
		// 這裡的常數值只需要保留 "http://{myBlogName}.blogspot" 這段值，尾巴其它值都要去掉
		MY_BLOGGER_URL = tempMyBloggerUrl;
	}
	
	private List<Post> posts;
	
	public JbakeConverter(List<Post> posts) {
		this.posts = posts;
	}

	public void convertToFile(String outputFolderPath) throws Exception {
		deleteOldOutputFileIfExist(outputFolderPath);
		
		for (Post post : posts) {
			Document doc = Jsoup.parseBodyFragment(post.getContent());
			handleImgTag(post, doc, outputFolderPath);
			handleATag(doc);
			handlePreTag(doc);
			handleSpanTag(doc);
			handleFontTag(doc);									
			
			// DOM 的內容修改過，所以要把修改後的資料回存
			post.setContent(doc.body().html());
			
			genJbakeFormatPostFile(post, outputFolderPath);
		}			
		logger.info(String.format("%s records of post is handled", posts.size()));
	}
	
	/**
	 * 舊的輸出檔存在時要先把它刪掉
	 * 
	 * @param outputFolderPath
	 * 
	 * @throws Exception
	 */
	private void deleteOldOutputFileIfExist(String outputFolderPath) throws Exception{
		if(new File(outputFolderPath).exists()){
			FileUtils.deleteDirectory(new File(outputFolderPath));				
		}
	}
	
	/**
	 * 處理 &lt;img&gt; tag
	 * 
	 * @param post
	 * @param doc
	 * @param outputFolderPath
	 */
	private void handleImgTag(Post post, Document doc, String outputFolderPath){
		String year = DateFormatUtils.format(post.getDate(), "yyyy");
		String month = DateFormatUtils.format(post.getDate(), "MM");		
		String destImgFileNamePrefix = DateFormatUtils.format(post.getDate(), "yyyyMMddHHmm") + "_";		
		Elements imgs = doc.select("img");
		int imgFileIndex = 1;
		for (Element img : imgs) {
			String srcImgUrl = img.attr("src"); //圖片來源網址
			if(img.parent().tagName().equalsIgnoreCase("a")){
				String srcImgFileExtension = StringUtils.substringAfterLast(img.parent().attr("href"), ".");
				// 把附檔名為 "jpg","png","gif" 當作合法的圖片檔，其它附檔名都視為不合法
				if(srcImgFileExtension.equalsIgnoreCase("jpg") || srcImgFileExtension.equalsIgnoreCase("jpeg") || srcImgFileExtension.equalsIgnoreCase("png") || srcImgFileExtension.equalsIgnoreCase("gif")){
					// 當 <img> 被 <a> 包起來(e.g. <a><img/></a>) 時，取 <a> 裡的照片網址
					srcImgUrl = img.parent().attr("href");
				}							
			}
			
			String destImgFileName = StringUtils.substringAfterLast(srcImgUrl, "/");
			imgFileIndex++;
			
			// <img> 裡的圖片檔存到 local
			FileDownloader.download(srcImgUrl, String.format("%s/img/%s/%s/%s", outputFolderPath, year, month, destImgFileName));
			
			// 因為圖檔存到 local 時被改名了，所以 dom 裡的圖檔網址要改掉					
			String updatedImgUrl = String.format("/img/%s/%s/%s", year, month, destImgFileName); 

			// 改成 <img src="{updatedImgUrl}">
			img.attr("src", updatedImgUrl);
			
			// 如果 <img> 外層包了 <a>(e.g. <a><img/><a>)，<a> 的內容改成  <a href="{updatedImgUrl}">		
			if(img.parent().tagName().equalsIgnoreCase("a")){
				img.parent().attr("href", updatedImgUrl);
			}
		}	
	}

	/**
	 * 如果 &lt;a&gt; tag 裡的 href attribute 指向自己 blog 裡特定文章網址(e.g. "http://{myName}.blogspot.com/{year}/{month}/{myPost.html}")，
	 * 把它改成 "/blog/{year}/{month}/{myPost.html}" 這種格式的網址 
	 * 
	 * @param post
	 * @param doc
	 */
	private void handleATag(Document doc){		
		Elements aTags = doc.select("a");		
		for (Element aTag : aTags) {
			String link = aTag.attr("href");		
			if(link.startsWith(MY_BLOGGER_URL)){				
				String postUrl = StringUtils.substringAfter(link.replaceFirst(MY_BLOGGER_URL, ""), "/");
				if(postUrl.contains("html")){
					aTag.attr("href", String.format("/blog/%s", postUrl));					
				}
			}
		}		
	}	
	
	/**
	 * 處理 &lt;pre class="brush: xxx"&gt; tag，讓它變成 &lt;pre class="prettyprint"&gt;&lt;/pre&gt;
	 * 
	 * @param doc
	 */
	private void handlePreTag(Document doc){		
		Elements pres = doc.select("pre[class^=brush:]");
		for (Element pre : pres) {
			pre.attr("class", "prettyprint");
			pre.html(pre.html());
		}		
	}
		
	/**
	 * 處理 &lt;span style="color: xxx"&gt; tag，讓它變成 &lt;code&gt; tag
	 * 
	 * @param doc
	 */
	private void handleSpanTag(Document doc){
		Elements spans = doc.select("span[style^=color]");
		for (Element span : spans) {
			span.removeAttr("style");
			span.tagName("code");
		}		
	}
	
	/**
	 * 處理 &lt;font color="xxx"&gt; tag，讓它變成 &lt;code&gt; tag
	 * 
	 * @param doc
	 */
	private void handleFontTag(Document doc){
		Elements fonts = doc.select("font");
		for (Element font : fonts) {
			font.removeAttr("color");
			font.tagName("code");
		}		
	}
	
	/**
	 * 產生 Jbake 格式的 post file
	 * 
	 * @param post
	 * 
	 * @throws Exception 
	 */
	private void genJbakeFormatPostFile(Post post, String outputFolderPath) throws Exception{
		// title 裡包含 "=" 會讓 jbake 產生文章標題時出錯，將它換成 "is"
		String fixedPostTitle = post.getTitle().replaceAll("=", "is");
		if(post.getTitle().contains("=")){
			logger.info(String.format("change artile title : [%s] to [%s]", post.getTitle(), fixedPostTitle));
		}
		
		StringBuilder postContent = new StringBuilder();				
		postContent.append("title=").append(fixedPostTitle).append("\n")
						.append("date=").append(DateFormatUtils.format(post.getDate(), "yyyy-MM-dd HH:mm")).append("\n")
						.append("type=post\n")
						.append("tags=").append(post.getTagsAsString().toLowerCase().replaceAll(" ", "-")).append("\n")
						.append("status=published\n")
						.append("~~~~~~\n\n")
						.append(post.getContent());
		String postFilePath = String.format("%s/blog/%s/%s/%s", outputFolderPath, 
						DateFormatUtils.format(post.getDate(), "yyyy"), 
						DateFormatUtils.format(post.getDate(), "MM"), post.getFileName());
		logger.info(String.format("save jbake post file : [%s]", postFilePath));
		FileUtils.writeStringToFile(new File(postFilePath), postContent.toString(), Charset.forName("UTF-8"));
		SummaryReport.getInstance().addJbakePostFilePath(postFilePath);
	}	
}
