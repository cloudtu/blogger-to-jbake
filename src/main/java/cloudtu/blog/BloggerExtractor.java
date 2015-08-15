package cloudtu.blog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class BloggerExtractor implements Extractor {
	private static final Logger logger = Logger.getLogger(BloggerExtractor.class);
	
	@Override
	public List<Article> extract(String blogAtomFilePath) throws Exception{
		List<Article> articles = new ArrayList<>();
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(new File(blogAtomFilePath)));
		for (SyndEntry entry : feed.getEntries()) {					
			// 利用這個 flag 判斷	entry 是否代表 blog article
			// 程式只處理 blog article，其它內容(e.g. comment,setting...etc)會被過濾掉	
			boolean isArticle = false;
			
			for (SyndCategory category : entry.getCategories()) {
				// "kind#post" 代表該個 entry 專門記錄 blog article
				if(category.getName().contains("kind#post")){
					isArticle = true;
					break;
				}
			}
			
			if(!isArticle){
				continue;
			}
			
			Article article = new Article();
			article.setDate(entry.getPublishedDate());
			article.setTitle(entry.getTitle());
			article.setContent(entry.getContents().get(0).getValue());
			
			for (SyndCategory category : entry.getCategories()) {
				if(!category.getName().contains("kind#post")){
					article.addTag(category.getName());
				}
			}		
			
			for (SyndLink link : entry.getLinks()) {
				if(link.getRel().equals("alternate")){
					article.setFileName(StringUtils.substringAfterLast(link.getHref(), "/"));
					break;
				}
			}
			
			articles.add(article);
		}
		
		return articles;	
	}
}
