package wedo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class config {

	public  String getPropValue(String key){
		System.out.println(key);
		Properties prop = new Properties();
		String propFilename  = System.getenv("RAID_SUPPORT_CONFIG");
		//InputStream input = getClass().getClassLoader().getResourceAsStream(propFilename);
		InputStream input;
		try {
			input = new FileInputStream(new File(propFilename));
			if(input != null){
				prop.load(input);
				return prop.getProperty(key);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
}




