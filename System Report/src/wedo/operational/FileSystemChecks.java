package wedo.operational;

import java.io.File;

import wedo.config.config;

public class FileSystemChecks {

	public static void main(String[] args) {
		System.out.println("DISK SPACE USAGE : ");
		System.out.println("path,Used Space MB,Used Space %,Available Space MB");
		getFileSystemDetails("RAID_HOME_FOLDER");
		getFileSystemDetails("DATA_HOME_FOLDER");
		getFileSystemDetails("SEARCH_HOME_FOLDER");
	}
	
	public static void getFileSystemDetails(String mountPoint){
		config con = new config();
		String path = con.getPropValue(mountPoint);
		System.out.println(path);
		//int threshold = _in.getInt("THRESHOLD");
		        
		File fileS = new File(path);
		long totalSpace = fileS.getTotalSpace(); //total disk space in bytes.
		long usableSpace = fileS.getUsableSpace(); ///unallocated / free disk space in bytes.

		double used_space_mb      = (totalSpace-usableSpace)/1024/1024; // in megabytes
		double used_space_percentage = 100 - (100*usableSpace/totalSpace) ; // in gigabytes
		double available_space_mb = usableSpace/1024/1024; // in megabytes
		
		System.out.println(path + "," + used_space_mb + "," + used_space_percentage + "," + available_space_mb);
	}
}
