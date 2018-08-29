package testHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;

/**
 * http异步回调
 */
public class HttpAsyncCallback implements FutureCallback<HttpResponse>{
	
	private String requestBody;
	
	public HttpAsyncCallback(String requestBody){
		this.requestBody = requestBody;
	}
	
	@Override
	public void completed(HttpResponse response){
		String responseBody = getResponseBody(response);
		System.out.println(responseBody);
	}
	
	@Override
	public void failed(Exception e){
		e.printStackTrace();
	}

	@Override
	public void cancelled(){
		
	}
	
	/**
	 * 获取响应结果(同时释放连接)
	 * @param response
	 * @return
	 */
	private String getResponseBody(HttpResponse response){
		String body = "";
		
		int statusCode = response.getStatusLine().getStatusCode();
		if(statusCode == HttpStatus.SC_OK){
			try{
				//使用EntityUtils.toString()方式时会大概率报错，原因：未接受完毕，链接已关
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream input = null;
					try{
						input = entity.getContent();
						StringBuilder sb = new StringBuilder();
						char[] tmp = new char[1024];
						Reader reader = new InputStreamReader(input, Consts.UTF_8.name());
						int l;
						while((l = reader.read(tmp)) != -1){
							sb.append(tmp, 0, l);
						}
						
						body = sb.toString();
					}
					finally{
						if(input != null){
							try{
								input.close();
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						
						EntityUtils.consume(entity);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(statusCode == HttpStatus.SC_BAD_REQUEST){
			body = "{\"code\":\"" + HttpStatus.SC_BAD_REQUEST + "\",\"message\":\"请求的服务不存在\"}";
		}
		else if(statusCode == HttpStatus.SC_FORBIDDEN){
			body = "{\"code\":\"" + HttpStatus.SC_FORBIDDEN + "\",\"message\":\"请求被禁止\"}";
		}
		else if(statusCode == HttpStatus.SC_NOT_FOUND){
			body = "{\"code\":\"" + HttpStatus.SC_NOT_FOUND + "\",\"message\":\"请求的资源不可用\"}";
		}
		else if(statusCode == HttpStatus.SC_REQUEST_TIMEOUT){
			body = "{\"code\":\"" + HttpStatus.SC_REQUEST_TIMEOUT + "\",\"message\":\"网络连接超时\"}";
		}
		else if(statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR){
			body = "{\"code\":\"" + HttpStatus.SC_INTERNAL_SERVER_ERROR + "\",\"message\":\"服务器出错\"}";
		}
		else{
			body = "{\"code\":\"" + statusCode + "\",\"message\":\"未知错误\"}";
		}
		
		return body;
	}
}
