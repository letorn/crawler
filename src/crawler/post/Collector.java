package crawler.post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Collector {

	private static Logger logger = Logger.getLogger(Collector.class);
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String id;
	private String norm;
	private String region;
	private String area;
	private Date date;

	private Explorer explorer;
	private Processor processor;

	public Collector(String norm, String region, String area) {
		if (StringUtils.isBlank(area)) {
			id = String.format("%s.%s", norm, region);
		} else {
			id = String.format("%s.%s.%s", norm, region, area);
		}
		this.norm = norm;
		this.region = region;
		this.area = area;
		date = new Date();

		explorer = new Explorer(this);
		processor = new Processor(this);
	}

	public Boolean start() {
		logger.info("start collector[id=" + id + ", norm=" + norm + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		return explorer.start() && processor.start();
	}

	public Boolean pause() {
		logger.info("pause collector[id=" + id + ", norm=" + norm + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		return explorer.pause() && processor.pause();
	}

	public Boolean stop() {
		logger.info("stop collector[id=" + id + ", norm=" + norm + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		date = new Date();
		return explorer.stop() && processor.stop();
	}

	public Integer getStatus() {
		if (explorer.getStatus() == 0 && processor.getStatus() == 0) {
			return 0;
		} else if (explorer.getStatus() == 1 || processor.getStatus() == 1) {
			return 1;
		} else if (explorer.getStatus() == 2 && processor.getStatus() == 2) {
			return 2;
		} else if (explorer.getStatus() == 3 && processor.getStatus() == 3) {
			return 3;
		} else {
			return -1;
		}
	}

	public String getId() {
		return id;
	}

	public String getNorm() {
		return norm;
	}

	public String getRegion() {
		return region;
	}

	public String getArea() {
		return area;
	}

	public Date getDate() {
		return date;
	}

	public Explorer getExplorer() {
		return explorer;
	}

	public Processor getProcessor() {
		return processor;
	}
}
