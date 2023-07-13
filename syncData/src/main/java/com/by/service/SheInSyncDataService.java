package com.by.service;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.by.dao.SyncDao;
import com.by.generator.bean.SheinSyncData;
import com.by.generator.dao.SheinSyncDataMapper;
import com.by.utils.ApiSignUtil;
import com.by.utils.HttpsUtils;
import com.by.vo.WebResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SheInSyncDataService {
	@Autowired
	WebResult result;
	@Autowired
	SheinSyncDataMapper sheinSyncDataMapper;
	@Autowired
	SyncDao syncDao;
	@Autowired
	HttpsUtils httpsUtils;
	@Autowired
	ApiSignUtil apiSignUtil;

    @Value("${sheinDevUrl}")
    private String sheinDevUrl;
    @Value("${sheinDevUrl2}")
    private String sheinDevUrl2;
    @Value("${sheinProdUrl}")
    private String sheinProdUrl;
    @Value("${syncUrl}")
    private String syncUrl;
    @Value("${syncUrl2}")
    private String syncUrl2;
    
    @Value("${openKeyId}")
    private String openKeyId;
    @Value("${secretKey}")
    private String secretKey;
	
	private static final Logger logger = LoggerFactory.getLogger(SheInSyncDataService.class);
	
	public Object sheInSyncData() {
		List<SheinSyncData> SheinSyncList = syncDao.selectAll();
		List<SheinSyncData> postList = new ArrayList<SheinSyncData>();
		int m = SheinSyncList.size();
		int num = 0;
		for (int i = 0; i < m; i++) {
			SheinSyncData SheinSyncData = SheinSyncList.get(i);
			postList.add(SheinSyncData); 
			if(i%99==0) {
				num++;
				String dataJson="";
				try {
					dataJson = new ObjectMapper().writeValueAsString(postList);
				} catch (JsonProcessingException e1) {
					e1.printStackTrace();
				}
				 //输出json数据
				logger.info("第"+num+"次同步数据如下：");
				logger.info(dataJson);
				String requestUrl = sheinDevUrl+syncUrl;
		        try {
					String rs = apiSignUtil.Post(requestUrl, dataJson,openKeyId,secretKey,syncUrl);
					logger.info("第"+num+"次同步数据返回结果："+rs);
				} catch (Exception e) {
					e.printStackTrace();
				}	
		        postList = new ArrayList<SheinSyncData>();
			}
			
		}
		return result.success("处理成功");
	}
	
	 
    private static RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();
 
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

}
