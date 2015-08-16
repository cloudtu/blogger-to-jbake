package cloudtu.blog.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 處理結果統計報表
 * 
 * @author cloudtu
 */
public class SummaryReport {
	private List<String> downloadFilePaths = new ArrayList<>();
	private List<String> jbakePostFilePaths = new ArrayList<>(); 
	
	private static SummaryReport report = new SummaryReport();
	
	private SummaryReport(){		
	}
	
	public static SummaryReport getInstance() {
		return report;
	}
	
	public synchronized void addDownloadFilePath(String downloadFilePath) {
		downloadFilePaths.add(downloadFilePath);
	}
	
	public synchronized void addJbakePostFilePath(String jbakePostFilePath) {
		jbakePostFilePaths.add(jbakePostFilePath);
	}	
	
	@Override
	public String toString() {
		StringBuilder reportContent = new StringBuilder();
		reportContent.append("---=== summary report - begin ===---\n");
		
		for (int i = 0; i < downloadFilePaths.size(); i++) {
			reportContent.append(String.format("downloadFile-[%s]-[%s]\n", (i+1), downloadFilePaths.get(i)));
		}
		
		for (int j = 0; j < jbakePostFilePaths.size(); j++) {
			reportContent.append(String.format("jbakePostFile-[%s]-[%s]\n", (j+1), jbakePostFilePaths.get(j)));
		}		
		
		reportContent.append("---=== summary report - end ===---\n");		
		return reportContent.toString();
	}
}
