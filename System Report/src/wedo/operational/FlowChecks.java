package wedo.operational;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wedo.config.config;
import wedo.database.OracleJDBC;

public class FlowChecks {

	static config con = new config();
	static String databaseIP = con.getPropValue("DATABASE_IP");
	static String databasePort = con.getPropValue("DATABASE_PORT");
	static String databaseSchema = con.getPropValue("RASAPP_SCHEMA_USERNAME");
	static String databasePassword = con.getPropValue("RASAPP_SCHEMA_PASSWORD");
	static String databaseServiceName = con.getPropValue("DB_SERVICE_NAME");
	
	public static void main(String[] args) {
		getFlowsExceptions();
		getRedSemaphores();
		getExecutionTimeofRunningFlows();
	}
	

	public static void getRedSemaphores(){
		
		
		String sql = "select Name from BPM_T_SEMAPHORE where VALUE = 0";
		Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("Red Semaphores :");
			while(rs.next()){
		        System.out.println("	" + rs.getString("Name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void getExecutionTimeofRunningFlows(){
		
		String sql = "SELECT TO_CHAR (r.created_date, 'YYYYMMDD HH24:MI:SS') EXEC_DATE, 24 * (sysdate - r.created_date) as Time_Elapsed, FLOW_RUN_ID,";
		sql = sql + "f.flow_name, rtrim(replace(replace(replace(extract(xmltype(flow_definition), '//flow/tasklist/task[@class=" ;
		sql = sql + "\"wedo.ecc.im.services.core.LoaderTask\"]/parameter[@name=\"ConnectionName\"]').getStringVal(),'<parameter name=\"ConnectionName\" type=\"String\" vbtype=\"String\">',''),'</parameter>',','),'<parameter name=\"ConnectionName\" type=\"String\">',''),',')  as Satellite , r.instance_id";
        sql = sql + ",DECODE (r.flow_status, '2', 1, 0) RUNNING, b.block_type, j.job_id FROM bpm_t_flows f, bpm_c_running_flows r, im_t_block_type b, im_t_job j";
        sql = sql + " WHERE f.flow_id = r.flow_id(+) AND f.status = 3 AND r.flow_status = 2 and b.description(+) = substr(f.flow_name, 5) and j.description(+) = substr(f.flow_name, 5) ORDER BY TIME_ELAPSED desc";
		
        
        
        Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("Running Flows Execution Time :");
			System.out.println("EXEC_DATE,TIME_ELAPSED,FLOW_RUN_ID,FLOW_NAME,INSTANCE_ID,Satellite,BLOCK_TYPE,JOB_ID");
			while(rs.next()){
		        System.out.println(rs.getString("EXEC_DATE") + "," + rs.getString("TIME_ELAPSED") + "," +  rs.getString("FLOW_RUN_ID")
		        		 +  "," + rs.getString("FLOW_NAME") + "," +  rs.getString("INSTANCE_ID") + "," +  rs.getString("Satellite") 
		        		 +  "," + rs.getString("BLOCK_TYPE") + "," +  rs.getString("JOB_ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void getFlowsExceptions(){
		
		String sql = "SELECT TO_CHAR (r.created_date, 'YYYYMMDD HH24:MI:SS') EXEC_DATE,TO_CHAR (r.modified_date, 'YYYYMMDD HH24:MI:SS') as end_date";
		sql = sql + ", f.flow_name, r.flow_run_id , b.block_type, c.category_id, nvl (rtrim(replace(replace(replace(extract(xmltype(flow_definition), '//flow/tasklist/task[@class=\"wedo.ecc.im.services.core.LoaderTask\"]/parameter[@name=\"ConnectionName\"]').getStringVal(),'<parameter name=\"ConnectionName\" type=\"String\" vbtype=\"String\">',''),'</parameter>',','),'<parameter name=\"ConnectionName\" type=\"String\">',''),','), 'UC')  as \"Satellite\", r.exception";
		sql = sql + " FROM bpm_t_flows f, bpm_c_running_flows r, bpm_t_category c, im_t_block_type b WHERE     f.flow_id = r.flow_id(+)";
		sql = sql + "AND f.category_id = c.category_id(+) and b.description(+) = substr(f.flow_name, 5) AND r.flow_status = 3  AND f.status = 3 AND r.created_date > sysdate - 3 ORDER BY 1 DESC";
		
		Connection conn = OracleJDBC.createJDBCConnection(databaseIP, databasePort, databaseServiceName, databaseSchema, databasePassword);
		Statement stmt;
		ResultSet rs;
			
		System.out.println("Flows Exceptions :");
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("EXEC_DATE,END_DATE,FLOW_NAME,FLOW_RUN_ID,BLOCK_TYPE,category_id,Satellite,EXCEPTION");
			while(rs.next()){
		        System.out.println(rs.getString("EXEC_DATE") + "," + rs.getString("end_date") + "," +  rs.getString("FLOW_NAME")
		        		 +  "," + rs.getString("FLOW_RUN_ID") + "," +  rs.getString("BLOCK_TYPE") + "," +  rs.getString("category_id") 
		        		 +  "," + rs.getString("Satellite") + "," +  rs.getString("EXCEPTION"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
