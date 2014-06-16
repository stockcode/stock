package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;


/**
 * Hello world!
 *
 */
public class DayLimit 
{
    public static void main( String[] args ) throws Exception
    {            	
    	 // Create an instance of HttpClient.
        HttpClient client = new HttpClient();
        //client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
  
        NumberFormat format = new DecimalFormat("#0.00");
    	
    	String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";  
    	String   driver= "net.sourceforge.jtds.jdbc.Driver";  
    	java.sql.Connection conn;
    	
		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		
    	String sql = "select * from stocklimit where statusid='402881e42ee70575012ee7129faf0002'";

    	StringBuilder sb = new StringBuilder();
    	Integer index = 1;
    	ResultSet resultSet = st.executeQuery(sql);
    	while (resultSet.next()) {
    		String pkid = resultSet.getString("pkid");
    		String stockcode = resultSet.getString("stockcode");
    		String stockType = "sh";
    		
    		if (stockcode.startsWith("0")) stockType = "sz";
            // Create a method instance.
            GetMethod method = new GetMethod("http://hq.sinajs.cn/list=" + stockType + stockcode);            
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
              //System.out.println(stockcode + "=" + results[0]);
              String name = results[0].trim();                            
              if (name.equals("")) continue;
              
              String openprice = results[1];
              String yesterdayprice = results[2];
              String closeprice = results[3];
              String highprice = results[4];
              String lowprice = results[5];
              String volume = results[8];
              String amount = results[9];
              String tradedate = results[30];
              
              if (volume.equals("0")) continue;
              
              Double yprice = Double.parseDouble(yesterdayprice);
              
              Double oprice = Double.parseDouble(openprice);
              Double percent = (oprice - yprice)/yprice * 100;
              
              if (percent > 1) {            	  
            	  sb.append(index + ". 代码：" + stockcode + ",名称：" + name + ",高开比例:" + format.format(percent) + "%\r\n");
              }

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
    	
    	Properties props = System.getProperties();
    	props.put( "mail.smtp.host" , "smtp.139.com");
    	props.put("mail.smtp.auth", "true");  
    	
    	Session session = Session.getDefaultInstance(props,null);
    	MimeMessage message = new MimeMessage(session);
    	message.setFrom(new InternetAddress("13613803575@139.com"));
    	message.addRecipient(Message.RecipientType.TO,new InternetAddress("13613803575@139.com"));
    	
    	DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
		Date tradedate = new Date();
    	message.setSubject(df.format(tradedate) + " 跳空高开提醒：");
    	String content = sb.toString();
    	if (StringUtils.isEmpty(content)) {
    		content = "今日没有跳空涨停股票。";
    	}
    	message.setText(content);
    	
    	Transport transport = session.getTransport( "smtp" );//指定的协议
    	transport.connect("smtp.139.com", "13613803575", "gk790624");
    	transport.sendMessage(message,message.getAllRecipients());
    	transport.close();
    }
}
