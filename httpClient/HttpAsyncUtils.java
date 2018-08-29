package testHttpClient;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

public class HttpAsyncUtils{
	
	private static CloseableHttpAsyncClient httpAsyncClient;
	
	/**
	 * 异步post请求
	 * @param url
	 * @param data
	 * @param timeout
	 * @param callback
	 * @throws Exception
	 */
	public static void post(String url, String data, int timeout, FutureCallback<HttpResponse> callback){
		try{
			CloseableHttpAsyncClient httpAsyncClient = getHttpAsyncClient();
			HttpPost request = new HttpPost(url);
			
			//设置超时
			if(timeout > 0){
				RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(timeout)
					.setConnectTimeout(2000)
					.setConnectionRequestTimeout(1000)
					.setExpectContinueEnabled(false)
					.build();
				request.setConfig(requestConfig);
			}
		
			StringEntity entity = new StringEntity(data, Consts.UTF_8.name());
			entity.setContentEncoding(Consts.UTF_8.name());
			entity.setContentType(ContentType.APPLICATION_JSON.toString());
			request.setEntity(entity);
			
			httpAsyncClient.start();
			httpAsyncClient.execute(request, callback);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取httpAsyncClient
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpAsyncClient getHttpAsyncClient() throws Exception{
		if(httpAsyncClient == null){
			//配置IO线程
			IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
					.setIoThreadCount(Runtime.getRuntime().availableProcessors())
					.build();
			ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
			
			/**http & https访问方式*/
			//指定信任密钥存储对象和连接套接字工厂
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(keyStore, new TrustStrategy(){
				
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException{
					//信任任何链接
					return true;
				}
			}).build();
			
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(sslContext, hostnameVerifier);
			
			RegistryBuilder<SchemeIOSessionStrategy> registryBuilder = RegistryBuilder.<SchemeIOSessionStrategy> create();
			registryBuilder.register("http", NoopIOSessionStrategy.INSTANCE);
			registryBuilder.register("https", sslSessionStrategy);
			
			Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = registryBuilder.build();
			
			//设置连接管理器
			PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor, sessionStrategyRegistry);
			connManager.setMaxTotal(400);//设置连接池线程最大数量
			connManager.setDefaultMaxPerRoute(200);//设置单个路由最大的连接线程数量
			
			httpAsyncClient = HttpAsyncClients.custom().setConnectionManager(connManager).build();
		}
		
		return httpAsyncClient;
	}
	
	public static void main(String[] args) throws Exception{
//	    String url = "http://10.45.4.33:10000/zcm-testing/project/queryProjects";
//	    String tpl = "{\"page\": ${page}, \"rowNum\": ${rowNum}}";
	    String url = "http://10.45.5.53:8088/services/TrueWebServices.TrueWebServicesHttpSoap11Endpoint/";
	    String tpl = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://com.ztesoft.zsmart/xsd\">"
                        + "   <soapenv:Header>                  "
                        + "      <xsd:AuthHeader>               "
                        + "         <Username>?</Username>      "
                        + "         <Password>?</Password>      "
                        + "         <username>?</username>      "
                        + "         <password>?</password>      "
                        + "      </xsd:AuthHeader>              "
                        + "   </soapenv:Header>                 "
                        + "   <soapenv:Body>                    "
                        + "      <xsd:queryVPNGroup>            "
                        + "         <QueryVPNGroupReqDto>       "
                        + "            <RequestID>?</RequestID> "
                        + "         </QueryVPNGroupReqDto>      "
                        + "      </xsd:queryVPNGroup>           "
                        + "   </soapenv:Body>                   "
                        +"</soapenv:Envelope>";
	    Map<String, Object> map = new HashMap<>();
	    map.put("page", 1);
	    map.put("rowNum", 10);
	    
	    post(url, FreeMarkerUtils.process("name", tpl, map), 3000, new HttpAsyncCallback(tpl));
	    
	}
}
