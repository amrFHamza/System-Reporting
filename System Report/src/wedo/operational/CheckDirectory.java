package wedo.operational;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wedo.config.config;
import wedo.database.OracleJDBC;

public class CheckDirectory {

	static config con = new config();
	static String databaseIP = con.getPropValue("DATABASE_IP");
	static String databasePort = con.getPropValue("DATABASE_PORT");
	static String databaseSchema = con.getPropValue("RASAPP_SCHEMA_USERNAME");
	static String databasePassword = con.getPropValue("RASAPP_SCHEMA_PASSWORD");
	static String databaseServiceName = con.getPropValue("DB_SERVICE_NAME");
	
	public static void main(String[] args) {
		//getINFolders();
		//getOUTFolders();
		getErrFolders();
	}
	
	public static void getINFolders(){
		
		
		Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT distinct(SOURCE) AS inFOLDER FROM IM_T_BLOCK_TYPE   where BLOCK_TYPE IN (select DISTINCT(BLOCK_TYPE) AS BLOCK_TYPE from IM_T_IMPORT where TASK_TYPE = 1 GROUP BY BLOCK_TYPE)";
		    rs = stmt.executeQuery(sql);
		  //STEP 5: Extract data from result set
		    System.out.println("---------------------------IN Folders----------------------------------------");
		    System.out.println("Folder,oldest File,Recent File");
		    while(rs.next()){
		         String IN_FOLDER = rs.getString("inFOLDER");
		         IN_FOLDER = IN_FOLDER.replace("{0}", "");
		         File in = new File(IN_FOLDER);
		         File[] files = getFiles(in);
		         int count;
		         if(files == null)
		        	 count = 0;
		         else
		        	 count = files.length;
		         System.out.println(IN_FOLDER + "," + count + "," + getOldestFile(in,files,count)+ "," + getLastModifiedFile(in,files,count));
		      }
		    System.out.println("------------------------------------------------------------------------------------------");
		    rs.close();
		    conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void getOUTFolders(){
		Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
		System.out.println("-----------------------------------------OUTPUT FOLDERS------------------------------");
		try {
			stmt = conn.createStatement();
			String sql = "SELECT DISTINCT(DONE_PARAMETER) AS outFOLDER FROM IM_T_BLOCK_TYPE   where BLOCK_TYPE IN (select DISTINCT(BLOCK_TYPE) AS BLOCK_TYPE from IM_T_IMPORT where TASK_TYPE = 1 GROUP BY BLOCK_TYPE) AND DONE_ACTION = 2";
		    rs = stmt.executeQuery(sql);
		  //STEP 5: Extract data from result set
		    while(rs.next()){
		         String IN_FOLDER = rs.getString("outFOLDER");
		         IN_FOLDER = IN_FOLDER.replace("{0}", "");
		         File out = new File(IN_FOLDER);
		         File[] files = getFiles(out); 
		         int count = files.length;
		         System.out.println(IN_FOLDER + "," + count + "," + getOldestFile(out,files,count)+ "," + getLastModifiedFile(out,files,count));
		      }
		    rs.close();
		    conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void getErrFolders(){
		Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
		System.out.println("---------------- Files on Error --------------------------- ");
		try {
			stmt = conn.createStatement();
			String sql = "SELECT DISTINCT(ERROR_PARAMETER) AS errFOLDER FROM IM_T_BLOCK_TYPE   where BLOCK_TYPE IN (select DISTINCT(BLOCK_TYPE) AS BLOCK_TYPE from IM_T_IMPORT where TASK_TYPE = 1 GROUP BY BLOCK_TYPE) AND ERROR_ACTION = 2";
		    rs = stmt.executeQuery(sql);
		  //STEP 5: Extract data from result set
		    while(rs.next()){
		         String Err_Folder = rs.getString("errFOLDER");
		         Err_Folder = Err_Folder.replace("{0}", "");
		         File err = new File(Err_Folder);
		         if(err.exists()){
		        	 System.out.println(err.getAbsolutePath() + " : ");
			         File[] files = listFiles(err);
			         for (File file : files) {
						System.out.println("	" + file.getAbsolutePath());
					}
		         }
		         
		      }
		    System.out.println("------------------------------------------------------------");
		    rs.close();
		    conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	static FilenameFilter fileFilter = new FilenameFilter() {
        public boolean accept(File directory, String fileName) {
        	File f = new File(directory.getAbsolutePath() + "/" + fileName);
            return f.isFile();
        }
        };

	public static int getFilesCount(File folder) {
		if(folder.isDirectory()){
			return folder.listFiles(fileFilter).length;
		}
		return -1;
	}
	
	public static File[] getFiles(File folder) {
		if(folder.isDirectory()){
			return folder.listFiles(fileFilter);
		}
		return null;
	}
	
	public static File[] listFiles(File folder) {
		if(folder.isDirectory()){
			return folder.listFiles(fileFilter);
		}
		return null;
	}
	
	public static String getOldestFile(File folder, File[] files , int filesCount) {
		long lastMod = Long.MAX_VALUE;
	    File choice = null;
		if(folder.isDirectory()){
			if(filesCount <= 0)
				return "";
			for (File file : files) {
				if (file.lastModified() < lastMod) {
		            choice = file;
		            lastMod = file.lastModified();
		        }
			}
			if(choice == null)
				return "";
			return choice.getName();
		}
		return "";
	}
	
	public static String getLastModifiedFile(File folder, File[] files, int filesCount) {
		long lastMod = Long.MIN_VALUE;
	    File choice = null;
		if(folder.isDirectory()){
			if(filesCount <= 0)
				return "";
			for (File file : files) {
				if (file.lastModified() > lastMod) {
		            choice = file;
		            lastMod = file.lastModified();
		        }
			}
			if(choice == null)
				return "";
			return choice.getName();
		}
		return "";
	}
}
