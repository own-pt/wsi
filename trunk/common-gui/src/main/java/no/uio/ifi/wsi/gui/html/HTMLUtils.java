package no.uio.ifi.wsi.gui.html;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringEscapeUtils;

public class HTMLUtils {

	private static String append(String k) {
		k = k.replace(" ", "&nbsp;");
		if (k.length() == 1) {
			return k + "&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		if (k.length() == 2) {
			return k + "&nbsp;&nbsp;&nbsp";
		}
		if (k.length() == 3) {
			return k + "&nbsp;&nbsp;";
		}
		if (k.length() == 4) {
			return k + "&nbsp;";
		}

		return k;
	}

	public static String toHTMLTable(String s) {

		StringTokenizer toker = new StringTokenizer(s, "\n", false);

		StringBuilder b = new StringBuilder();
		b.append("<table  cellspacing=\"2\">\n");
		b.append("<tbody>\n");

		while (toker.hasMoreTokens()) {
			b.append("<tr>");
			b.append("<td>");

			String xs = StringEscapeUtils.escapeHtml4(toker.nextToken());

			String[] xss = xs.split("\t");

			for (int i = 0; i < xss.length - 1; i++) {
				String k = xss[i];
				k = append(k);
				b.append(k);
				b.append("</td><td>");
			}
			b.append(append(xss[xss.length - 1]));
			b.append("</td>");
			b.append("</tr>\n");
		}

		b.append("</tbody>\n");
		b.append("</table>\n");

		return b.toString();
	}
}
