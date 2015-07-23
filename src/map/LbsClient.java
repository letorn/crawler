package map;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class LbsClient {

	private static Logger logger = Logger.getLogger(LbsClient.class);

	private static Integer timeout = 3000;
	private static String[] userAgents = { "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)", "Mozilla/5.0 (compatible; MSIE 11.0; Windows NT 6.1; Trident/7.0)", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36", "Baiduspider", "Sosospider" };
	private Random random = new Random();

	public Double[] getPoint(String address) {
		return getPoint(address, null);
	}

	public Double[] getPoint(String address, String area) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("ak", "YcnpFYovxoDCqPvnsL89VD8U");
		data.put("output", "json");
		if (null != area) {
			data.put("city", area);
		}
		data.put("address", address);
		JSONObject jsonObject = jsonObjectGetter("http://api.map.baidu.com/geocoder/v2/", data);
		if (null != jsonObject) {
			Integer status = jsonObject.getInt("status");
			if (0 == status) {
				JSONObject locationObject = jsonObject.getJSONObject("result").getJSONObject("location");
				Double lon = locationObject.getDouble("lng");
				Double lat = locationObject.getDouble("lat");
				return new Double[] { lon, lat };
			}
		}
		return null;
	}

	private Document documentGetter(String url, Map<String, String> data) {
		Document document = null;
		String userAgent = userAgents[random.nextInt(userAgents.length - 1)];
		try {
			if (null == data) {
				document = Jsoup.connect(url).timeout(timeout).userAgent(userAgent).followRedirects(false).get();
			} else {
				document = Jsoup.connect(url).data(data).timeout(timeout).userAgent(userAgent).followRedirects(false).get();
			}
		} catch (IOException e) {
			logger.error(e.toString() + ", [userAgent=" + userAgent + ", url=" + url + "]will try again with random userAgent after 5min");
			try {
				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			document = documentGetter(url, data);
		}
		return document;
	}

	private JSONObject jsonObjectGetter(String url, Map<String, String> data) {
		JSONObject jsonObject = null;
		String userAgent = userAgents[random.nextInt(userAgents.length - 1)];
		try {
			Response response = null;
			if (null == data) {
				response = Jsoup.connect(url).timeout(timeout).userAgent(userAgent).execute();
			} else {
				response = Jsoup.connect(url).data(data).timeout(timeout).userAgent(userAgent).execute();
			}
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setIgnoreDefaultExcludes(true);
			if (StringUtils.isNotBlank(response.body())) {
				jsonObject = JSONObject.fromObject(response.body(), jsonConfig);
			}
		} catch (IOException e) {
			logger.error(e.toString() + ", [userAgent=" + userAgent + ", url=" + url + "]will try again with random userAgent after 5min");
			try {
				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			jsonObject = jsonObjectGetter(url, data);
		}
		return jsonObject;
	}

}
