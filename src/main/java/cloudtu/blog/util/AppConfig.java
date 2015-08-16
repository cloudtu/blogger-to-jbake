package cloudtu.blog.util;

import java.util.ResourceBundle;

/**
 * 讀取 application.properties 裡的設定
 * 
 * @author cloudtu
 */
public class AppConfig
{  
    private static final ResourceBundle rb = ResourceBundle.getBundle("application");
    
    private AppConfig(){
    }
    
    /**
     * 
     * 
     * @param key
     * 
     * @return
     * 
     */
    public static String getString(String key){
    	return rb.getString(key);
    }
}