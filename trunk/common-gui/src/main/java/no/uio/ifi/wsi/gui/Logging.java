package no.uio.ifi.wsi.gui;

import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.google.common.collect.Maps;

public class Logging {

	private final Map<String, Logger> loggers;

	public final String PATH;

	public Logging(String path_) {
		PATH = path_;
		loggers = Maps.newHashMap();
	}

	public Logger getLogger(String ip) {
		if (loggers.containsKey(ip))
			return loggers.get(ip);
		FileAppender fa = new FileAppender();
		fa.setAppend(true);
		fa.setFile(PATH + ip + ".log");
		fa.activateOptions();
		fa.setName("system");
		fa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss}	%m%n"));
		Logger systemLogger = Logger.getLogger(ip);
		systemLogger.addAppender(fa);
		loggers.put(ip, systemLogger);
		return systemLogger;
	}
}
