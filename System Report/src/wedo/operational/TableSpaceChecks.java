package wedo.operational;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wedo.config.config;
import wedo.database.OracleJDBC;

public class TableSpaceChecks {
	
	/*
	 * Check tables spaces
	 * */
	
	static config con = new config();
	static String databaseIP = con.getPropValue("DATABASE_IP");
	static String databasePort = con.getPropValue("DATABASE_PORT");
	static String databaseSchema = con.getPropValue("RASAPP_SCHEMA_USERNAME");
	static String databasePassword = con.getPropValue("RASAPP_SCHEMA_PASSWORD");
	static String databaseServiceName = con.getPropValue("DB_SERVICE_NAME");
	static String tableSpaceLike = con.getPropValue("TABLE_SPACE_LIKE"); 
	

	public static void main(String[] args) {
		tableSpaceStatus();
	}
	
	
	public static void tableSpaceStatus(){
		

		// TODO: not tested yet
		String sql ="select df.tablespace_name as tablespace_name,nvl(df.totalspace, 0) - nvl(fs.freespace, 0) as used_mb,nvl(fs.freespace, 0) as free_mb,nvl(df.totalspace, 0) as total_mb";
		sql = sql + ",round(100 * (nvl(fs.freespace, 0) / nvl(df.totalspace, 0))) as free_space_percentage,round(100 * (1 - (nvl(fs.freespace, 0) / nvl(df.totalspace, 0)))) as used_space_percentage";
		sql = sql + "from (select tablespace_name,sum(nvl(bytes, 0)) / 1024 / 1024 totalspace from dba_data_files group by tablespace_name) df ,(select tablespace_name,(sum(nvl(bytes, 0)) / 1024 / 1024) freespace";
		sql = sql + "from dba_free_space group by tablespace_name ) fs where df.tablespace_name = fs.tablespace_name(+) 	and round(100 * (1 - (nvl(fs.freespace, 0) / nvl(df.totalspace, 0)))) > 90";
		// like 'RAID%'
		sql = sql + "and df.tablespace_name like '";
		sql = sql + tableSpaceLike ;
		sql = sql + "%'";
		
		Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("Tables Spaces :");
			while(rs.next()){

			System.out.println(rs.getString("tablespace_name") + "," + rs.getString("used_mb") + "," +  rs.getString("free_mb")
       		 +  "," + rs.getString("total_mb") + "," +  rs.getString("free_space_percentage") + "," +  rs.getString("used_space_percentage") 
       		 );
	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
