package init;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import util.WebContext;

public class WebInterceptor implements HandlerInterceptor {

	private static Logger logger = Logger.getLogger(WebInterceptor.class);

	private ThreadLocal<Long> localStartMillis = new ThreadLocal<Long>();

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		WebContext.init(request);

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String remoteHost = request.getRemoteHost();

		String url = request.getRequestURI();
		String params = request.getQueryString();
		if (params != null)
			url = String.format("%s?%s", url, params);
		logger.info(String.format("%s %s", remoteHost, url));

		localStartMillis.set(System.currentTimeMillis());

		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {

	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception e) throws Exception {
		Long startMillis = localStartMillis.get();
		Long endMillis = System.currentTimeMillis();
		Long duration = endMillis - startMillis;
		logger.info(String.format("spent %d millis", duration));
	}

}
