package no.uio.ifi.wsi.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.gui.html.HTMLGenerator;
import no.uio.ifi.wsi.gui.html.HTMLPaginator;
import no.uio.ifi.wsi.gui.html.PageResult;
import no.uio.ifi.wsi.search.language.ExpressionHandler;
import no.uio.ifi.wsi.sparql.SearchInfo;
import no.uio.ifi.wsi.sparql.SearchResults;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

@Log4j
public abstract class SearchInterface extends HttpServlet {

	private static final int MAX_RESULTS = 1000;

	public Logger log() {
		return log;
	}

	private static SearchInterface search;
	private Map<String, RepositoryInterface> interfaces;
	private Map<String, ExpressionHandler> expressionHandler;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("-----------------------------------");

		if (search == null) {
			search = this;
			try {
				search.initInstance(config);
			} catch (Exception ex) {
				ex.printStackTrace();
				log.fatal(ex.getMessage(), ex);
			}
		}
	}

	public abstract List<String> formats();

	public abstract List<FormatDescription> description();

	public abstract HTMLGenerator htmlGenerator();

	public static SearchInterface instance() {
		return search;
	}

	public HTMLPaginator getPaginator(String id) {
		HTMLPaginator paginator = map.get(id);
		if (paginator == null)
			return null;
		return paginator;
	}

	private String demoName;

	public String demoName() {
		return demoName;
	}

	public abstract Map<String, String> examples();

	@Getter
	private String dataPath;

	public void initDataPath(ServletConfig config) {
		dataPath = config.getInitParameter("DATA_PATH");
		System.out.println(dataPath);
	}

	public void initInstance(ServletConfig config) {
		String logpath = config.getInitParameter("LOG_PATH");
		interfaces = Maps.newHashMap();
		expressionHandler = Maps.newHashMap();
		interfaces = new HashMap<String, RepositoryInterface>();
		RepositoryInterface rif = null;
		for (FormatDescription format : description()) {
			expressionHandler.put(format.getName(),
					format.getExpressionHandler());
			rif = new RepositoryInterface(htmlGenerator(), dataPath
					+ format.getName() + "/", format.getStructure(), formats());
			format.getExpressionHandler().getGenerator()
					.setTermIndex(rif.getTermIndex());
			interfaces.put(format.getName(), rif);
		}
		internalLog = new Logging(logpath);
		queue = Queues.newArrayDeque();
		map = new HashMap<String, HTMLPaginator>();

		demoName = config.getInitParameter("DEMO_NAME");
	}

	public String stringFromStream(InputStream resourceContent) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				resourceContent));
		String line = null;
		StringBuilder vud = new StringBuilder();
		try {
			while ((line = in.readLine()) != null) {
				vud.append(line + "\n");
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vud.toString();
	}

	private Logging internalLog;

	private Queue<String> queue;

	private static final long serialVersionUID = 1L;

	private Map<String, HTMLPaginator> map;

	public String returnDiv(String id, String format) {
		try {
			RepositoryInterface rif = interfaces.get(format);
			SemanticStructure doc = rif.getDocuments().getDocument(id);
			if (doc == null)
				return "Could not find document!";
			return rif.getGenerator().toHtml(doc, format);
		} catch (Exception e) {
			e.printStackTrace();
			return "Could not load format!";
		}
	}

	public String searchId(String id, String format) {
		RepositoryInterface rif = interfaces.get(format);
		SemanticStructure doc = rif.getDocuments().getDocument(id);
		if (doc == null)
			return "Document with id " + id + " not found";
		return rif.getGenerator().toHtml(id, doc, formats().get(0), formats());
	}

	public String calculate(String calculate) {
		try {
			HTMLPaginator paginator = map.get(calculate);
			SearchInfo info = paginator.getResults().info();
			if (info != null)
				return info.toString(interfaces.get(paginator.format())
						.getDocuments().structureCount());
			else
				return "Fail to calculate results counts!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error calculating the results";
		}

	}

	public PageResult searchWithPaginator(String text, int totalLimit,
			String annotation, String ip) {
		String sparql = null;
		try {
			sparql = toSparqlStrucrture(text, annotation,
					internalLog.getLogger(ip));
			System.out.println(sparql);
		} catch (RuntimeException e) {
			e.printStackTrace();
			log.debug(e.getMessage(), e);
			String message = "" + e.getMessage();
			return new PageResult(message);

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage(), e);
			String message = "Internal service error!";
			return new PageResult(message);
		}
		return searchWithPaginatorSPARQL(sparql, totalLimit, true, annotation);
	}

	public PageResult searchWithPaginatorSPARQL(String sparql,
			int resultsPerPage, boolean showSparql) {
		return searchWithPaginatorSPARQL(sparql, resultsPerPage, showSparql,
				formats().get(0));
	}

	public synchronized int getId() {
		return query++;
	}

	private int query = 0;

	public PageResult searchWithPaginatorSPARQL(String sparql,
			int resultsPerPage, boolean showSparql, String annotation) {
		try {
			RepositoryInterface rif = interfaces.get(annotation);
			SearchResults ps = rif.getOntology().searchResults(sparql,
					MAX_RESULTS);
			String id = "" + getId();
			HTMLPaginator hmlp = new HTMLPaginator(id, showSparql, sparql, ps,
					resultsPerPage, rif, annotation);
			map.put(id, hmlp);
			queue.add(id);
			if (queue.size() > 10) {
				String rem = queue.poll();
				map.remove(rem);
			}
			return new PageResult(hmlp);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage(), e);
			String message = "" + e.getMessage();
			return new PageResult(message);
		}
	}

	public String query(HttpServletRequest request) {
		String query = "";
		if (request.getParameter("search") != null) {
			query = request.getParameter("search");
		} else {
			query = examples().get(formats().get(0));
		}
		return query;
	}

	public String httpHost(HttpServletRequest request) {
		String remoteHost = request.getHeader("x-forwarded-for");
		try {
			if (remoteHost == null)
				remoteHost = request.getRemoteHost();
			remoteHost = InetAddress.getByName(remoteHost).getHostName();
		} catch (Exception e) {

		}
		return remoteHost;
	}

	public int requestsPerPage(HttpServletRequest request) {
		int resultsPerPage = 20;
		if (request.getParameter("resultsPerPage") != null) {
			try {
				resultsPerPage = Integer.parseInt(request
						.getParameter("resultsPerPage"));
				if (resultsPerPage > 100) {
					resultsPerPage = 100;
				}
			} catch (Exception e) {

			}
		}
		return resultsPerPage;
	}

	public List<String> annotations(HttpServletRequest request) {
		List<String> out = Lists.newArrayList();
		out.add(formats().get(0));

		String[] values = request.getParameterValues("annotation");

		if (values != null) {
			out = Lists.newArrayList();
			for (String v : values)
				out.add(v);
		} else {
			if (request.getParameter("search") != null)
				out = Lists.newArrayList();
		}
		return out;
	}

	public String toSparqlStrucrture(String text, String annotation, Logger log) {
		log.debug("Query\t" + text.replace("\n", " ").replace("\r", " "));
		log.debug("Formats\t" + annotation);
		String sparql = expressionHandler.get(annotation).generateSparql(text,
				annotation);
		log.debug("SPARQL\t" + sparql.replace("\n", "\t"));
		return sparql;
	}
}
