package cloudtu.blog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cloudtu.blog.model.Post;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * Blogger post extractor
 * 
 * @author cloudtu
 */
public class BloggerExtractor {
	private static final Logger logger = Logger.getLogger(BloggerExtractor.class);
	
	/**
	 * 從 Blogger 匯出的 atom 檔裡取出所有 blog post(blog 發布文章)<p/>
	 * 
	 * 注：只會取出 blog post，其它內容(e.g. comment,setting...etc)會過濾掉
	 * 
	 * @param blogAtomFilePath
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public List<Post> extract(String blogAtomFilePath) throws Exception{
		List<Post> posts = new ArrayList<>();
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(new File(blogAtomFilePath)));
		for (SyndEntry entry : feed.getEntries()) {					
			// 利用這個 flag 判斷	entry 是否代表 blog post
			// 程式只處理 blog post，其它內容(e.g. comment,setting...etc)會過濾掉	
			boolean isPost = false;
			
			for (SyndCategory category : entry.getCategories()) {
				// "kind#post" 代表該個 entry 專門記錄 blog post
				if(category.getName().contains("kind#post")){
					isPost = true;
					break;
				}
			}
			
			if(!isPost){
				continue;
			}
			
			Post post = new Post();
			post.setDate(entry.getPublishedDate());
			post.setTitle(entry.getTitle());
			post.setContent(entry.getContents().get(0).getValue());
			
			for (SyndCategory category : entry.getCategories()) {
				if(!category.getName().contains("kind#post")){
					post.addTag(category.getName());
				}
			}		
			
			for (SyndLink link : entry.getLinks()) {
				if(link.getRel().equals("alternate")){
					post.setFileName(StringUtils.substringAfterLast(link.getHref(), "/"));
					break;
				}
			}
			
			posts.add(post);
		}
		
		return posts;	
	}
}
