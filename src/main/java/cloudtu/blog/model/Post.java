package cloudtu.blog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * blog post(blog 發布文章)
 * 
 * @author cloudtu
 */
public class Post {
	private Date date;
	private String title;
	private String content;
	private List<String> tags = new ArrayList<>();
	private String fileName;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTagsAsString() {
		return StringUtils.join(tags, ",");
	}
	public void addTag(String tag) {
		tags.add(tag);
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}		
}
