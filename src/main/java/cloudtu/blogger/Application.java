package cloudtu.blogger;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Application {
	private static final Logger logger = Logger.getLogger(Application.class);
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("application");	
	private static final String BLOGGER_ATOM_FILE_PATH = rb.getString("bloggerAtomFilePath");	
	private static final String OUTPUT_FOLDER_PATH = StringUtils.substringBeforeLast(rb.getString("outputFolderPath"), "/");
	
	public static void main(String[] args) {
		logger.info("BloggerToHtml start");
		try {						
			List<Article> articles = new Extractor().extract(BLOGGER_ATOM_FILE_PATH);
			new Transformer(articles, OUTPUT_FOLDER_PATH).traslateToJbakeFormatHtmlFile();
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("BloggerToHtml stop");
	}
}
