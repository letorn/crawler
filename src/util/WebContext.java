package util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class WebContext implements ApplicationContextAware {

	private static Boolean inited = false;
	private static String scheme;
	private static String serverName;
	private static Integer serverPort;
	private static String contextPath;
	private static String serverHost;
	private static String serverLink;
	private static String appRoot = System.getProperty("webapp.root");
	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static void init(HttpServletRequest request) {
		if (!inited) {
			scheme = request.getScheme();
			serverName = request.getServerName();
			serverPort = request.getServerPort();
			contextPath = request.getContextPath();
			serverHost = serverPort == 80 ? serverName + contextPath : String.format("%s:%d%s", serverName, serverPort, contextPath);
			serverLink = String.format("%s://%s", scheme, serverHost);
		}
		inited = true;
	}

	public static String getScheme() {
		return scheme;
	}

	public static String getServerName() {
		return serverName;
	}

	public static Integer getServerPort() {
		return serverPort;
	}

	public static String getContextPath() {
		return contextPath;
	}

	public static String getServerHost() {
		return serverHost;
	}

	public static String getServerLink() {
		return serverLink;
	}

	public static String getAppRoot() {
		return appRoot;
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(Class<T> clazz, String beanName) {
		return applicationContext.getBean(clazz, beanName);
	}

}
