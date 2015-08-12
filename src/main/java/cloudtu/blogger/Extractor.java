package cloudtu.blogger;

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

public class Extractor {
	private static final Logger logger = Logger.getLogger(Extractor.class);
	
	public List<Article> extract(String bloggerAtomFilePath) throws RuntimeException{
		try {
			List<Article> articles = new ArrayList<>();
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(new File(bloggerAtomFilePath)));
			for (SyndEntry entry : feed.getEntries()) {					
				boolean isArticle = false;
				for (SyndCategory category : entry.getCategories()) {
					// category 包含 "kind#post" 代表該個 entry 專門記錄 blog article
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
						String linkUrl = link.getHref();
						article.setFileName(StringUtils.substringAfterLast(linkUrl, "/"));
						break;
					}				
				}
				
				articles.add(article);
			}
			
			return articles;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
}
