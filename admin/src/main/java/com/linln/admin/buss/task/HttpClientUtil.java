package com.linln.admin.buss.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClientUtil
 * houkun
 * @Description
 * @Date 2018年9月27日
 */
public final class HttpClientUtil {
	static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
	public static final int DEFAULT_TIME_OUT_MS = 5000;
	public static final int DEFAULT_KEEP_TIME=300;
	
	private HttpClientUtil() {
	}

	private static PoolingHttpClientConnectionManager cm = null;

	private static void init() {
		if (cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(500);
			cm.setDefaultMaxPerRoute(100);
		}
	}

	
	 /**
     * Http connection keepAlive 设置
     */
    public static ConnectionKeepAliveStrategy defaultStrategy = new ConnectionKeepAliveStrategy() {
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            int keepTime = DEFAULT_KEEP_TIME;
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("format KeepAlive timeout exception, exception:" + e.toString());
                    }
                }
            }
            return keepTime * 1000;
        }
    };
	
	private static CloseableHttpClient buildHttpClient(int timeout) {
		init();
		ConnectionConfig connectionConfig = ConnectionConfig.copy(ConnectionConfig.DEFAULT).build();
		RequestConfig requestConfig = RequestConfig.copy(RequestConfig.DEFAULT).setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout).setConnectTimeout(timeout) .build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultConnectionConfig(connectionConfig)
				.setDefaultRequestConfig(requestConfig).setConnectionManager(cm).setKeepAliveStrategy(defaultStrategy).build();
		return httpClient;
	}

	/**
	 * 发送HTTP_GET请求
	 * 
	 * @see 该方法会自动关闭连接,释放资源
	 * @param requestURL
	 *            请求地址(含参数)
	 * @param decodeCharset
	 *            解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
	 * @param timeout
	 *            超时秒数（单位：ms）
	 * @return 远程主机响应正文
	 */
	public static String sendGetRequest(String reqURL, String decodeCharset, int timeout, Map<String, String> head) throws Exception {
		// long responseLength = 0; //响应长度
		String responseContent = null; // 响应内容
		CloseableHttpClient httpClient = buildHttpClient(timeout);
		CloseableHttpResponse response = null;
		HttpGet httpGet = new HttpGet(reqURL); // 创建org.apache.http.client.methods.HttpGet
		try {
			
			if (null != head && !head.isEmpty()) { // 头文件不为空，设置
				for (String key : head.keySet()) {
					httpGet.setHeader(key, head.get(key));
				}
			}
			
			
			response = httpClient.execute(httpGet); // 执行GET请求
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(
						"resp status is " + response.getStatusLine().getStatusCode() + ", not " + HttpStatus.SC_OK);
			}
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
				EntityUtils.consume(entity); // Consume response content
			}
		} catch (ClientProtocolException e) {
			LOGGER.error("该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下", e);
			throw e;
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		} catch (IOException e) {
			LOGGER.error("该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下", e);
			throw e;
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭连接发生异常,堆栈信息如下", e);
			}
		}
		return responseContent;
	}

	/**
	 * 发送get请求
	 * 
	 * @param reqURL
	 *            请求地址
	 * @param timeout
	 *            超时：ms
	 * @return
	 */
	public static String sendGetRequest(String reqURL, int timeout, Map<String, String> head) throws Exception {
		return sendGetRequest(reqURL, null, timeout, head);
	}

	/**
	 * 发送HTTP_POST请求
	 * 
	 * @see 该方法为<code>sendPostRequest(String,String,boolean,String,String)</code>的简化方法
	 * @see 该方法在对请求数据的编码和响应数据的解码时,所采用的字符集均为UTF-8
	 * @see 当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][
	 *      ]等特殊字符进行<code>URLEncoder.encode(string,"UTF-8")</code>
	 * @param isEncoder
	 *            用于指明请求数据是否需要UTF-8编码,true为需要
	 */
	public static String sendPostRequest(String reqURL, String sendData, boolean isEncoder, int timeout)
			throws Exception {
		return sendPostRequest(reqURL, sendData, isEncoder, null, null, timeout);
	}

	/**
	 * 发送HTTP_POST请求
	 * 
	 * @see 该方法会自动关闭连接,释放资源
	 * @see 当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][
	 *      ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
	 * @param reqURL
	 *            请求地址
	 * @param sendData
	 *            请求参数,若有多个参数则应拼接成param11=value11¶m22=value22¶m33=value33的形式后,传入该参数中
	 * @param isEncoder
	 *            请求数据是否需要encodeCharset编码,true为需要
	 * @param encodeCharset
	 *            编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
	 * @param decodeCharset
	 *            解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
	 * @return 远程主机响应正文
	 */
	public static String sendPostRequest(String reqURL, String sendData, boolean isEncoder, String encodeCharset,
			String decodeCharset, int timeout) throws Exception {
		String responseContent = null;
		CloseableHttpClient httpClient = buildHttpClient(timeout);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(reqURL);
		// httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;
		// charset=UTF-8");
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
		try {
			if (isEncoder) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (String str : sendData.split("&")) {
					formParams.add(new BasicNameValuePair(str.substring(0, str.indexOf("=")),
							str.substring(str.indexOf("=") + 1)));
				}
				httpPost.setEntity(new StringEntity(
						URLEncodedUtils.format(formParams, encodeCharset == null ? "UTF-8" : encodeCharset)));
			} else {
				httpPost.setEntity(new StringEntity(sendData));
			}

			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(
						"resp status is " + response.getStatusLine().getStatusCode() + ", not " + HttpStatus.SC_OK);
			}

			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			LOGGER.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
			throw e;
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭连接发生异常,堆栈信息如下", e);
			}
		}
		return responseContent;
	}

	/**
	 * 发送HTTP_POST请求
	 * 
	 * @see 该方法会自动关闭连接,释放资源
	 * @see 该方法会自动对<code>params</code>中的[中文][|][
	 *      ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
	 * @param reqURL
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param encodeCharset
	 *            编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
	 * @param decodeCharset
	 *            解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
	 * @return 远程主机响应正文
	 */
	public static String sendPostRequest(String reqURL, Map<String, String> head, Map<String, String> params,
			String encodeCharset, String decodeCharset, int timeout) throws Exception {
		String responseContent = null;
		CloseableHttpClient httpClient = buildHttpClient(timeout);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(reqURL);
		if (null != head && !head.isEmpty()) { // 头文件不为空，设置
			for (String key : head.keySet()) {
				httpPost.setHeader(key, head.get(key));
			}
		}
		List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 创建参数队列
		for (Map.Entry<String, String> entry : params.entrySet()) {
			formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset == null ? "UTF-8" : encodeCharset));

			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(
						"resp status is " + response.getStatusLine().getStatusCode() + ", not " + HttpStatus.SC_OK);
			}
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			LOGGER.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
			throw e;
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭连接发生异常,堆栈信息如下", e);
			}
		}

		return responseContent;
	}

	public static String sendPostRequest(String reqURL, Map<String, String> head, Map<String, String> params)
			throws Exception {
		return sendPostRequest(reqURL, head, params, null, null, DEFAULT_TIME_OUT_MS);
	}

	public static String sendPostRequest(String reqURL, Map<String, String> head, Map<String, String> params,
			int timeout) throws Exception {
		return sendPostRequest(reqURL, head, params, null, null, timeout);
	}

	public static byte[] readStream(String url) throws IOException {
		InputStream inStream = null;

		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			inStream = conn.getInputStream();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			// 每次读取的字符串长度，如果为-1，代表全部读取完毕
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			// 关闭输入流
			inStream.close();
			return outStream.toByteArray();
		} catch (IOException e) {
			LOGGER.error("IOException error in catch.", e);
			throw e;
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				LOGGER.error("IOException error.", e);
			}
		}
	}
	
	
	public static String sendPostJSON(String reqURL, Map<String, String> head,String requestStr, int timeout) throws Exception {
		String responseContent = null;
		CloseableHttpClient httpClient = buildHttpClient(timeout);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(reqURL);
		if (null != head && !head.isEmpty()) { // 头文件不为空，设置
			for (String key : head.keySet()) {
				httpPost.setHeader(key, head.get(key));
			}
		}
		httpPost.setHeader("Content-Type", "application/json");
		try {
			
			StringEntity requestEntity = new StringEntity(requestStr, Charset.forName("UTF-8"));
			httpPost.setEntity(requestEntity);

			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(
						"resp status is " + response.getStatusLine().getStatusCode() + ", not " + HttpStatus.SC_OK);
			}
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, Charset.forName("UTF-8"));
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			LOGGER.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
			throw e;
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭连接发生异常,堆栈信息如下", e);
			}
		}

		return responseContent;
	}

	
	public static String sendPostJSONGZIP(String reqURL, Map<String, String> head,String requestStr, int timeout) throws Exception {
		String responseContent = null;
		CloseableHttpClient httpClient = buildHttpClient(timeout);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(reqURL);
		if (null != head && !head.isEmpty()) { // 头文件不为空，设置
			for (String key : head.keySet()) {
				httpPost.setHeader(key, head.get(key));
			}
		}
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Content-Encoding", "gzip");
		try {
			
			GzipCompressingEntity requestEntity = new GzipCompressingEntity(new StringEntity(requestStr, Charset.forName("UTF-8")));
			httpPost.setEntity(requestEntity);

			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(
						"resp status is " + response.getStatusLine().getStatusCode() + ", not " + HttpStatus.SC_OK);
			}
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity, Charset.forName("UTF-8"));
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			LOGGER.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
			throw e;
		} finally {
			try {
				if (null != response) {
					response.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭连接发生异常,堆栈信息如下", e);
			}
		}

		return responseContent;
	}
}
