package cloudtu.blog;

import java.util.List;

public abstract class Converter {	
	protected List<Article> articles;
	
	public Converter(List<Article> articles) {
		this.articles = articles;
	}
	
	public abstract void convertToFile(String outputFolderPath) throws Exception;
}
