package cloudtu.blog;

import java.util.List;

public interface Extractor {	
	public List<Article> extract(String blogAtomFilePath) throws Exception;
}
