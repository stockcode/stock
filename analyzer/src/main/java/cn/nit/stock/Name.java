package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**
 * Hello world!
 *
 */
public class Name 
{
    public static void main( String[] args ) throws Exception
    {            	
    	 // Create an instance of HttpClient.
        HttpClient client = new HttpClient();
        //client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
  
    	
    	String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";  
    	String   driver= "net.sourceforge.jtds.jdbc.Driver";  
    	java.sql.Connection conn;
    	
		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		
    	String sql = "select * from stock where name is null";

    	
    	ResultSet resultSet = st.executeQuery(sql);
    	while (resultSet.next()) {
    		String pkid = resultSet.getString("pkid");
    		String stockcode = resultSet.getString("code");

            // Create a method instance.
            GetMethod method = new GetMethod("http://hq.sinajs.cn/list=" + resultSet.getString("type") + stockcode);            
    		method.addRequestHeader("Content-type" , "text/html; charset=utf-8"); 
            // Provide custom retry handler is necessary
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
            		new DefaultHttpMethodRetryHandler(3, false));            
            
            try {
            	//定义一个输入流
            	InputStream ins = null;
            	//定义文件流
            	BufferedReader br =null;
            	
              // Execute the method.
              int statusCode = client.executeMethod(method);

              if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
              }

              ins = method.getResponseBodyAsStream();
              String charset = method.getResponseCharSet();
              if(charset.toUpperCase().equals("ISO-8859-1")){
              charset = "gbk";
              }
              //按服务器编码字符集构建文件流，这里的CHARSET要根据实际情况设置
              br = new BufferedReader(new InputStreamReader(ins,method.getResponseCharSet()));
              StringBuffer sbf = new StringBuffer();
              String line = null;
              while ((line = br.readLine()) != null)
              {
              sbf.append(line);
              }
              String result = new String(sbf.toString().getBytes(method.getResponseCharSet()),charset);
              //输出内容
              String[] results = result.split("\"");
              results = results[1].split(",");
              System.out.println(stockcode + "=" + results[0]);
              String name = results[0].trim();                            
              if (name.equals("")) continue;
              
              sql = "update stock set name='" + name + "' where pkid=" + pkid;
      	    System.err.println(sql);
      	    updateStatement.execute(sql);   

            } catch (HttpException e) {
              System.err.println("Fatal protocol violation: " + e.getMessage());
              e.printStackTrace();
            } catch (IOException e) {
              System.err.println("Fatal transport error: " + e.getMessage());
              e.printStackTrace();
            } finally {
              // Release the connection.
              method.releaseConnection();
            }
    		//break;
 	    
    	}
    	st.close();
    	conn.close();
    	
    }
}
