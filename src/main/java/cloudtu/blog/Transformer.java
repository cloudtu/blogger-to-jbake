package cloudtu.blog;

import java.util.List;

public abstract class Transformer {	
	protected List<Article> articles;
	
	public Transformer(List<Article> articles) {
		this.articles = articles;
	}
	
	public abstract void traslateToFile(String outputFolderPath) throws Exception;
}
