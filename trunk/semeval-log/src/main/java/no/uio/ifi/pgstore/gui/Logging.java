package no.uio.ifi.pgstore.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;

public class Logging extends HttpServlet {

	public static Logging instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Logging instance() {
		return instance;
	}

	private String PATH = null;

	public int count(String host) {
		int count = 0;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(PATH + host + ".log"),
					"UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("\tQuery\t"))
					count++;
			}
			in.close();
		} catch (Exception e) {

		}
		return count;
	}

	public List<CountedHost> hosts() {
		List<CountedHost> out = Lists.newArrayList();
		for (String f : new File(PATH).list()) {
			if (f.startsWith("SYSTEM"))
				continue;
			String ip = f.substring(0, f.length() - 4);
			out.add(new CountedHost(count(ip), ip));
		}
		Collections.sort(out, out.get(0));
		return out;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		PATH = config.getInitParameter("DATA_PATH");
		instance = this;
	}

	public String toHtml(String host) {

		StringBuilder b = new StringBuilder();
		int current = 0;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(PATH + host + ".log"),
					"UTF-8"));
			String line = null;

			StringBuilder bb = new StringBuilder();

			ArrayDeque<String> bs = new ArrayDeque<String>();

			MapInteger mi = new MapInteger();

			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0)
					continue;
				String[] s = line.split("\t");

				if (s.length < 3)
					continue;

				String type = s[1];
				String timestamp = s[0];

				StringBuilder rest = new StringBuilder();
				for (int i = 2; i < s.length; i++)
					rest.append(s[i] + "\t");

				String value = rest.toString().trim();

				if (type.equals("Query")) {
					if (bb.length() > 0) {
						bs.add(bb.toString());
						if (bs.size() > 25)
							bs.removeFirst();
						bb = new StringBuilder();
					}
					mi.increment(value);
					current++;
					bb.append("<hr/>\n");
					bb.append(timestamp + "<br/>\n");
					bb.append(StringEscapeUtils.escapeHtml4(value) + "<br/>\n");
				}
				if (type.equals("Formats")) {
					bb.append("<a href=\"javascript:toggle('formats-" + current
							+ "' , 'Formats' );\" id=\"show-formats-" + current + "\">Show Formats</a><br/>");
					bb.append("<div id=\"div-formats-" + current + "\" style=\"display: none\">");
					bb.append(StringEscapeUtils.escapeHtml4(value));
					bb.append("</div>");
				}
				if (type.equals("Analysis")) {
					bb.append("<a href=\"javascript:toggle('analysis-" + current
							+ "' , 'Analysis' );\" id=\"show-analysis-" + current + "\">Show Analysis</a><br/>");
					bb.append("<div id=\"div-analysis-" + current + "\" style=\"display: none\">");
					bb.append(StringEscapeUtils.escapeHtml4(value));
					bb.append("</div>");
				}
				if (type.equals("SPARQL")) {
					bb.append("<a href=\"javascript:toggle('sparql-" + current + "' , 'SPARQL' );\" id=\"show-sparql-"
							+ current + "\">Show SPARQL</a><br/>");
					bb.append("<div id=\"div-sparql-" + current + "\" style=\"display: none\">");
					bb.append(StringEscapeUtils.escapeHtml4(value.replace("\t", "\n")).replace("\n", "<br/>\n"));
					bb.append("</div>");
				}
			}
			if (bb.length() > 0) {
				bs.add(bb.toString());
				if (bs.size() > 25)
					bs.removeFirst();
				bb = new StringBuilder();
			}

			b.append("<h3><strong>Queries:</strong>&nbsp" + current + "</h3>");

			b.append("<h3>Top Queries</h3>\n");

			List<String> mis = mi.ordered();

			b.append("<table>");

			b.append("<tbody>");

			for (int i = mis.size() - 1; i > -1; i--) {
				b.append("<tr><td>" + mis.get(i) + "</td><td>" + mi.get(mis.get(i)) + "</td></tr>");
			}
			b.append("</tbody>");
			b.append("</table>");

			b.append("<h3>Last 25 Queries</h3>\n");

			while (bs.size() > 0)
				b.append(bs.removeLast());
			in.close();
		} catch (Exception e) {
			throw new RuntimeException("Could not load file " + host);
		}

		return b.toString();
	}

}
