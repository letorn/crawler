package crawler;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentClient {

	private static Logger logger = Logger.getLogger(DocumentClient.class);

	private static Integer timeout = 3000;
	private static String[] userAgents = { "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)", "Mozilla/5.0 (compatible; MSIE 11.0; Windows NT 6.1; Trident/7.0)", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36", "Baiduspider", "Sosospider" };
	private Random random = new Random();

	public Document get(String url) {
		return documentGetter(url, null);
	}

	public Document get(String url, Map<String, String> data) {
		return documentGetter(url, data);
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

}
