package no.uio.ifi.wsi.gui.html;

import java.util.List;

import lombok.Getter;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.gui.RepositoryInterface;
import no.uio.ifi.wsi.sparql.Result;
import no.uio.ifi.wsi.sparql.SearchInfo;
import no.uio.ifi.wsi.sparql.SearchResults;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;

public class HTMLPaginator {

	private final String annotation;

	private final String id;
	private final List<String> pages;
	@Getter
	private final SearchResults results;
	private int position;
	private final String query;
	private int resultNumber;
	private final int resultsPerPage;
	private final RepositoryInterface rif;
	private final boolean showSparql;

	private boolean showCalculateResults;

	public HTMLPaginator(String id_, boolean showSparql_, String query,
			SearchResults pagination, int resultsPerPage_,
			RepositoryInterface rif_, String annotation_) {

		rif = rif_;
		annotation = annotation_;
		showSparql = showSparql_;
		id = id_;
		resultNumber = 0;
		this.query = query;
		resultsPerPage = resultsPerPage_;
		this.results = pagination;
		pages = Lists.newArrayList();
		position = -1;
		showCalculateResults = true;

		hasNext();
	}

	public String buildPage(boolean hasPrev, boolean hasNext, String page) {
		StringBuilder b = new StringBuilder();
		if (showSparql) {
			b.append("<a href=\"javascript:toggle('sparql' , 'SPARQL' );\" id=\"show-sparql\">Show SPARQL</a><br/>");
			b.append("<div id=\"div-sparql\" style=\"display: none\">");
			b.append(StringEscapeUtils.escapeHtml4(query).replace("\n",
					"\n<br/>"));
			b.append("</div>");
		}

		b.append("<h3>Results Page ").append(position + 1).append("</h3>");
		b.append(page);
		b.append("<p>");
		if (hasPrev) {
			b.append("<input name=\"previous\" type=\"submit\" value=\"previous\"/>&nbsp;");
		}
		if (hasNext) {
			b.append("<input name=\"next\" type=\"submit\" value=\"next\"/>");
		}
		b.append("</p>");

		if (showCalculateResults) {
			if (!results.finnished()) {
				b.append("<h3 id=\"showResults\">");
				b.append("<a href=\"javascript:showResults(" + id
						+ ");\">Count Matches</a>");
				b.append("</h3>");
			} else {
				SearchInfo info = results.info();
				b.append("<h3 id=\"showResults\">");
				if (info != null)
					b.append(results.info().toString(
							rif.getDocuments().structureCount()));
				else
					b.append("Fail to calculate results counts!");
				b.append("</h3>");
			}
		}
		return b.toString();
	}

	public String getId() {
		return id;
	}

	public String getQuery() {
		return query;
	}

	public boolean hasNext() {
		if (position != pages.size() - 1) {
			return true;
		}

		if (!results.hasNext()) {
			if (pages.isEmpty()) {
				pages.add("<p>No results found that match the query!</p>");
				showCalculateResults = false;
				return true;
			}
			return false;
		}

		if (pages.size() * resultNumber > 1000) {
			return false;
		}

		List<Result> rs = results.next(resultsPerPage);
		StringBuilder b = new StringBuilder();
		for (Result r : rs) {
			if (r.getGraph() != null) {
				SemanticStructure document = rif.getDocuments().getDocument(
						r.getGraph());
				rif.getGenerator().toHtml(document, r, b, resultNumber++,
						annotation, rif.getFormats());
			} else {
				b.append("<hr/>");
				b.append("<table>");
				b.append("<tbody>");
				for (String key : r.getValues().keySet()) {
					b.append("<tr>");
					b.append("<td>");
					b.append(StringEscapeUtils.escapeHtml4(key));
					b.append("</td>");
					b.append("<td>");
					b.append(StringEscapeUtils.escapeHtml4(r.getValues().get(
							key)));
					b.append("</td>");
					b.append("</tr>");
				}
				b.append("</tbody>");
				b.append("</table>");
			}
		}
		pages.add(b.toString());
		return true;
	}

	public boolean hasPrevious() {
		return position != 0;
	}

	public String nextPage() throws Exception {
		position++;
		String page = pages.get(position);
		boolean hasPrev = hasPrevious();
		boolean hasNext = hasNext();
		return buildPage(hasPrev, hasNext, page);
	}

	public String previousPage() {
		position--;
		String page = pages.get(position);
		boolean hasPrev = hasPrevious();
		boolean hasNext = true;
		return buildPage(hasPrev, hasNext, page);
	}

	public String format() {
		return annotation;
	}

}
