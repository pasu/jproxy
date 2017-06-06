package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MinimalServlets
{
    public static void main( String[] args ) throws Exception
    {
        // Create a basic jetty server object that will listen on port 8080.
        // Note that if you set this to port 0 then a randomly available port
        // will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(8080);

        // The ServletHandler is a dead simple way to create a context handler
        // that is backed by an instance of a Servlet.
        // This handler then needs to be registered with the Server object.
        ServletHandler handler = new ServletHandler();
       
        
        server.setHandler(handler);

        // Passing in the class for the Servlet allows jetty to instantiate an
        // instance of that Servlet and mount it on a given context path.

        // IMPORTANT:
        // This is a raw Servlet, not a Servlet that has been configured
        // through a web.xml @WebServlet annotation, or anything similar.
        handler.addServletWithMapping(HelloServlet.class, "/wind/*");

        // Start things up!
        server.start();

        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See
        // http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();
    }

    @SuppressWarnings("serial")
    public static class HelloServlet extends HttpServlet
    {
        @Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {
        	String strType = "image/jpg";
        	String strUrl = null;
        	Boolean bProxy = true;
        	
        	String strName = request.getRequestURI();
        	if(strName.indexOf("Proxy") == -1)
        	{
        		bProxy = false;
        	}
        	
        	if(bProxy)
        	{
        		Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {  
                    String paramName = (String) paramNames.nextElement();  
          
                    String[] paramValues = request.getParameterValues(paramName);  
                    if (paramValues.length == 1) {  
                        String paramValue = paramValues[0];  
                        if (paramValue.length() != 0) {
                        	if(paramName.equalsIgnoreCase("type")){
                        		strType = paramValue; 
                        	}
                        	
                        	if(paramName.equalsIgnoreCase("url")){
                        		strUrl = paramValue; 
                        	}
                        }  
                    }  
                }  
            	//FileUtils.readFileToByteArray(file)
//            	String strResult = HttpRequest.sendGet(strUrl,"");
//            	response.setContentType(strType);
//            	OutputStream outputStream = response.getOutputStream();//获取OutputStream输出流
//            	byte[] dataByteArr = strResult.getBytes("UTF-8");//将字符转换成字节数组，指定以UTF-8编码进行转换
//            	outputStream.write(dataByteArr);//使用OutputStream流向客户端输出字节数组
                ////////////////////////////////////////////////////////
                try {
                	String reqUrl = strUrl;
                	
                	URL url = new URL(reqUrl);
                	
                	///////////////////////////////
//                	TrustManager[] trustAllCerts = new TrustManager[]{
//                            new X509TrustManager() {
//
//                                public java.security.cert.X509Certificate[] getAcceptedIssuers()
//                                {
//                                    return null;
//                                }
//                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
//                                {
//                                    //No need to implement.
//                                }
//                                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
//                                {
//                                    //No need to implement.
//                                }
//                            }
//                    };
//                	
//                	SSLContext sc = SSLContext.getInstance("SSL");
//                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
//                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                	///////////////////////////////
                	HttpURLConnection con = (HttpURLConnection)url.openConnection();
    	        	
                	con.setRequestProperty("accept", "*/*");
                	con.setRequestProperty("connection", "Keep-Alive");
                	con.setRequestProperty("user-agent",
    	                     "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                	
                	con.setRequestProperty("referer", "https://earth.nullschool.net/");
                	
                	con.setDoOutput(true);
                	con.setRequestMethod(request.getMethod());

                	response.setContentType(con.getContentType());
                	IOUtils.copy(con.getInputStream(), response.getOutputStream(),2048);
                	
//                	int data;
//                	InputStream is = con.getInputStream();
//                	OutputStream os = response.getOutputStream();
//    	            while((data = is.read()) != -1) {
//    	            	os.write(data);
//    	            }
//    	            is.close();

                } catch(Exception e) {
                System.out.println(0);
                System.out.println(e);

                	response.setStatus(500);
                }
                ///////////////////////////////////////////////////////////
        	}
        	else
        	{
        		if(strName.indexOf("favicon") != -1)
        		{
        			strName = "/wind/favicon.ico";
        		}
        		
        		File remoteFile = new File("." + strName);
        		
        		byte[] strContent = FileUtils.readFileToByteArray(remoteFile);
        		response.setContentType("text/html; charset=utf-8");
        		OutputStream outputStream = response.getOutputStream();
            	outputStream.write(strContent);
                response.setStatus(HttpServletResponse.SC_OK);

        	}
        }
    }
}