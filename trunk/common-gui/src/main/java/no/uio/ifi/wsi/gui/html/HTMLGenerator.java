package no.uio.ifi.wsi.gui.html;

import java.util.List;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.sparql.Match;
import no.uio.ifi.wsi.sparql.Result;

public abstract class HTMLGenerator {

	public String addFromat(SemanticStructure doc, String format) {
		StringBuilder b = new StringBuilder();
		b.append(
				"<div style=\"border:1px solid; border-color: lightgray\" id=\"")
				.append(format).append(doc.getId()).append("\">");
		b.append(toHTML(doc));

		String key = format + "_" + doc.getId() + "_text";
		String field = "Text";
		b.append("<p>");
		b.append(
				"<a style=\"text-decoration: none\" href=\"javascript:toggle('")
				.append(key).append("' , '").append(field)
				.append("' );\" id=\"show-").append(key).append("\">Show ")
				.append(field).append("</a>");
		b.append("</p>");
		key = format + "_" + doc.getId() + "_text";
		b.append("<div id=\"div-").append(key)
				.append("\" style=\"display: none;\">");
		b.append(HTMLUtils.toHTMLTable(doc.getStructure()));
		b.append("</div>");

		b.append("</div>");

		return b.toString();
	}

	private String toString(List<Integer> ans) {
		StringBuilder b = new StringBuilder();
		for (Integer s : ans) {
			b.append(s).append(" ");
		}
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	public void addStructure(Result result, StringBuilder b, int pos,
			SemanticStructure doc, String format, List<String> formats) {

		b.append("<div id=\"").append(doc.getId()).append("\">");

		b.append("<p>");
		b.append("<strong>").append(doc.getId()).append("</strong>&nbsp;");
		b.append(doc.getText());
		b.append("</p>");

		b.append("<span class=\"color\" style=\"border:1px solid grey;font-weight:bold;\"");
		b.append(" id=\"").append(doc.getId()).append("main").append("link\">");
		b.append("<a style=\"text-decoration: none\" href=\"javascript:showMain(");
		b.append("'").append(doc.getId()).append("'");
		b.append(");\">").append(format.toUpperCase()).append("</a>");
		b.append("</span>&nbsp;");
		for (String a : formats) {
			if (a.equals(format)) {
				continue;
			}
			b.append("<span class=\"color\" style=\"border:1px solid grey;background:darkgrey;\"");
			b.append(" id=\"");
			b.append(doc.getId()).append(a).append("link\">");
			b.append("<a style=\"text-decoration: none\" href=\"javascript:showFormat(");
			b.append("'").append(doc.getId()).append("'").append(",");
			b.append("'").append(a).append("'");
			b.append(");\">").append(a.toUpperCase()).append("</a>");
			b.append("</span>&nbsp;");
		}

		b.append(
				"<div style=\"border:1px solid; border-color: lightgray\" id=\"")
				.append("main").append(doc.getId()).append("\">");

		List<no.uio.ifi.wsi.sparql.Match> matches = null;
		String strString = toHTML(doc);

		if (result != null) {

			matches = result.getMatches(doc);
			if (matches.size() > 1) {
				b.append("<br/>");
				int i = 1;
				for (Match m : matches) {
					String toString = toString(m.getIds());
					if (i == 1) {
						b.append("<span class=\"color\" style=\"border:1px solid grey;font-weight:bold;\"");
					} else {
						b.append("<span class=\"color\" style=\"border:1px solid grey;background:darkgrey;\"");
					}
					b.append(" id=\"").append(doc.getId()).append("m")
							.append(i).append("link\">");
					b.append("<a style=\"text-decoration: none;"
							+ (i == 1 ? "color:red" : "")
							+ "\" href=\"javascript:match"
							+ (doc.getSvg() != null ? "SVG" : "HTML") + "(");
					b.append("'").append(doc.getId()).append("'").append(",");
					b.append("'").append(toString).append("'").append(",");
					b.append(doc.getNodesNumber()).append(",");
					b.append("").append(i).append(",");
					b.append("").append(matches.size());
					b.append(");\">").append("Match #").append(i)
							.append("</a>");
					b.append("</span>&nbsp;");
					i++;
				}
			}

			strString = anotate(doc.getId(), strString, matches.get(0), format);

		}

		b.append(strString);

		String key = format + "_" + doc.getId() + "_text";
		String field = "Text";
		b.append("<p>");
		b.append(
				"<a style=\"text-decoration: none\" href=\"javascript:toggle('")
				.append(key).append("' , '").append(field)
				.append("' );\" id=\"show-").append(key).append("\">Show ")
				.append(field).append("</a>");
		b.append("</p>");
		key = format + "_" + doc.getId() + "_text";
		b.append("<div id=\"div-").append(key)
				.append("\" style=\"display: none;\">");
		b.append(HTMLUtils.toHTMLTable(doc.getStructure()));
		b.append("</div>");

		b.append("</div>");

		b.append("</div>");

	}

	public abstract String toHTML(SemanticStructure structure);

	public abstract String anotate(String docID, String strString, Match match,
			String format);

	public void toHtml(SemanticStructure document, Result r, StringBuilder b,
			int pos, String format, List<String> formats) {
		b.append("<hr>\n");
		addStructure(r, b, pos, document, format, formats);
	}

	public String toHtml(SemanticStructure doc, String format) {
		return addFromat(doc, format);

	}

	public String toHtml(String id, SemanticStructure doc, String format,
			List<String> formats) {
		StringBuilder b = new StringBuilder();
		if (doc == null) {
			return "Failed to retrieve document " + id;
		}
		addStructure(null, b, 1, doc, format, formats);
		return b.toString();
	}
}
