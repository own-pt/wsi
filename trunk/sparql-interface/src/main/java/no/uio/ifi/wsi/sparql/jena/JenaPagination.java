package no.uio.ifi.wsi.sparql.jena;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import no.uio.ifi.wsi.sparql.Result;
import no.uio.ifi.wsi.sparql.ResultInstance;
import no.uio.ifi.wsi.sparql.SearchInfo;
import no.uio.ifi.wsi.sparql.SearchResults;
import lombok.extern.log4j.Log4j;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@Log4j
public class JenaPagination implements SearchResults {

	public static final String DEFAULT_GRAPH_ATTRIBUTE = "graph";
	private SearchInfo info;
	private boolean finnished = false;
	private int resultsRetrieved;
	private final Queue<Result> results;

	public void getResults(final String query_,
			final List<JENAConnect> connect, final int limit) {
		Thread t = new Thread() {
			public void run() {
				long start = System.currentTimeMillis();
				int graphs = 0;
				int matches = 0;
				try {
					for (JENAConnect jc : connect) {
						ResultSet current = jc.search(query_);
						while (current.hasNext()) {
							QuerySolution s = current.next();
							boolean isGraph = isGraph(s);
							if (!isGraph)
								continue;
							graphs++;
							Result r = new Result(s
									.get(DEFAULT_GRAPH_ATTRIBUTE).toString());
							Iterator<String> iter = s.varNames();
							while (iter.hasNext()) {
								String name = iter.next();
								if (name.equals(DEFAULT_GRAPH_ATTRIBUTE))
									continue;
								if (!name.endsWith("Values"))
									continue;
								String vname = name.substring(0,
										name.length() - 6);
								String[] values = s.getLiteral(name)
										.getString().split("\t");

								if (r.getInstances().size() == 0)
									initResultInstances(r.getInstances(),
											values);
								for (int i = 0; i < values.length; i++)
									r.getInstances().get(i)
											.addValue(vname, values[i]);
							}
							matches += r.getInstances().size();
							if (results.size() + resultsRetrieved <= limit)
								results.add(r);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
					finnished = true;
					throw new RuntimeException(e.getMessage());
				}

				long time = System.currentTimeMillis() - start;
				info = new SearchInfo(time, graphs, matches);
				finnished = true;
			}
		};
		t.start();
	}

	private long maxFinnishTime = 60000;

	public void waitForFinnish() {
		long start = System.currentTimeMillis();
		while (!finnished) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (System.currentTimeMillis() - start > maxFinnishTime)
				break;
		}
	}

	public JenaPagination(List<JENAConnect> connect, String query_, int limit_) {
		results = new ArrayDeque<Result>();
		resultsRetrieved = 0;
		getResults(query_, connect, limit_);
	}

	@Override
	public boolean hasNext() {
		if (!results.isEmpty())
			return true;
		waitForResults();
		return !results.isEmpty();
	}

	private void waitForResults() {
		long start = System.currentTimeMillis();
		while (!finnished && results.isEmpty()) {
			try {
				Thread.sleep(10);
				if (System.currentTimeMillis() - start > maxFinnishTime)
					break;
			} catch (InterruptedException e) {
			}
		}
	}

	private void initResultInstances(List<ResultInstance> rils, String[] values) {
		for (int i = 0; i < values.length; i++)
			rils.add(new ResultInstance());
	}

	public static boolean isGraph(QuerySolution s) {
		Iterator<String> vnames = s.varNames();
		while (vnames.hasNext()) {
			if (vnames.next().equals(DEFAULT_GRAPH_ATTRIBUTE))
				return true;
		}
		return false;
	}

	@Override
	public List<Result> next(int number) {
		List<Result> out = Lists.newArrayList();
		for (int i = 0; i < number; i++) {
			if (!hasNext())
				break;
			Result result = results.poll();
			out.add(result);
			resultsRetrieved++;
		}
		return out;
	}

	@Override
	public SearchInfo info() {
		if (info != null)
			return info;
		waitForFinnish();
		return info;
	}

	@Override
	public boolean finnished() {
		return finnished;
	}
}
