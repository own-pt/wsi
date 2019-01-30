package no.uio.ifi.wsi.sparql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import no.uio.ifi.wsi.sparql.jena.JenaPagination;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

public class Test {

	public static long countMatchesTest(Dataset dataset, String query)
			throws Exception {
		long startTime = System.currentTimeMillis();
		query = clean(query);
		String prefix = query.substring(0, query.indexOf("select"));
		query = query.substring(query.indexOf("select"));
		query = query.replace("ORDER BY ?graph\n", "");
		query = "select (count(?graph) as ?gCount) WHERE { " + query + " }";
		ResultSet rs = search(prefix + query, dataset);
		rs.next();
		return System.currentTimeMillis() - startTime;
	}

	public static long countGraphsTest(Dataset dataset, String query)
			throws Exception {
		long startTime = System.currentTimeMillis();
		query = clean(query);
		String prefix = query.substring(0, query.indexOf("select"));
		query = query.substring(query.indexOf("select"));
		query = query.replace("ORDER BY ?graph\n", "");
		query = "select (count(?graph) as ?gCount) WHERE { " + query + " }";
		query = query.replace("GROUP BY ?graph", "");
		ResultSet rs = search(prefix + query, dataset);
		rs.next();
		return System.currentTimeMillis() - startTime;
	}

	public static long first20Graphs(Dataset dataset, String query)
			throws Exception {
		long startTime = System.currentTimeMillis();
		ResultSet rs = search(query, dataset);
		int i = 0;
		while (rs.hasNext()) {
			rs.next();
			i++;
			if (i == 20)
				break;
		}
		return System.currentTimeMillis() - startTime;
	}

	public static long allGraphs(Dataset dataset, String query)
			throws Exception {
		long startTime = System.currentTimeMillis();
		ResultSet rs = search(query, dataset);
		while (rs.hasNext())
			rs.next();
		return System.currentTimeMillis() - startTime;
	}

	public static void main(String[] args) throws Exception {
		initLog4j();

		String datasetName = "dm";

		String path = "/usit/sh/ssd/milen/deepbank-new/" + datasetName + "/";
		Dataset dataset = TDBFactory.createDataset(path + "tdb/1/");
		TDB.getContext().set(TDB.symUnionDefaultGraph, true);

		String q = loadString("test/" + datasetName + ".txt");

		List<String> querys = queries(q);

		System.out.println("Dataset " + datasetName);

		int k = 1;
		for (String query : querys) {
			System.out.println("Query " + k);
			int[] solutions = solutions(query, dataset);
			System.out.println(solutions[0] + " graphs " + solutions[1]
					+ " matches");
			double estimatedTime = 0;
			for (int i = 0; i < 3; i++)
				estimatedTime += countGraphsTest(dataset, query);
			estimatedTime = estimatedTime / 3.0;
			estimatedTime = estimatedTime / 1000.0;
			estimatedTime = Math.round(estimatedTime * 10.0) / 10.0;
			System.out.println("Count graphs " + estimatedTime + " seconds");

			estimatedTime = 0;
			for (int i = 0; i < 3; i++)
				estimatedTime += countMatchesTest(dataset, query);
			estimatedTime = estimatedTime / 3.0;
			estimatedTime = estimatedTime / 1000.0;
			estimatedTime = Math.round(estimatedTime * 10.0) / 10.0;
			System.out.println("Count matches " + estimatedTime + " seconds");

			estimatedTime = 0;
			for (int i = 0; i < 3; i++)
				estimatedTime += first20Graphs(dataset, query);
			estimatedTime = estimatedTime / 3.0;
			estimatedTime = estimatedTime / 1000.0;
			estimatedTime = Math.round(estimatedTime * 10.0) / 10.0;
			System.out.println("First 20 " + estimatedTime + " seconds");

			estimatedTime = 0;
			for (int i = 0; i < 3; i++)
				estimatedTime += allGraphs(dataset, query);
			estimatedTime = estimatedTime / 3.0;
			estimatedTime = estimatedTime / 1000.0;
			estimatedTime = Math.round(estimatedTime * 10.0) / 10.0;
			System.out.println("All grpahs " + estimatedTime + " seconds");
			k++;
		}
	}

	public static List<String> queries(String query) {
		StringTokenizer toker = new StringTokenizer(query, "\n", false);
		List<String> out = Lists.newArrayList();
		StringBuilder b = new StringBuilder();
		while (toker.hasMoreTokens()) {
			String token = toker.nextToken().trim();
			if (token.length() == 0) {
				out.add(b.toString());
				b = new StringBuilder();
				continue;
			}
			b.append(token + "\n");

		}
		out.add(b.toString());
		return out;
	}

	public static int[] solutions(String query, Dataset dataset) {
		int count = 0;
		int matches = 0;
		try {
			ResultSet ss = search(query, dataset);
			while (ss.hasNext()) {
				QuerySolution s = ss.next();
				if (!JenaPagination.isGraph(s))
					continue;
				count++;
				Iterator<String> iter = s.varNames();
				while (iter.hasNext()) {
					String name = iter.next();
					if (name.equals(JenaPagination.DEFAULT_GRAPH_ATTRIBUTE))
						continue;
					if (!name.endsWith("Values"))
						continue;
					String[] values = s.getLiteral(name).getString()
							.split("\t");
					matches += values.length;
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new int[] { count, matches };
	}

	public static void initLog4j() {
		System.setProperty("log4j.defaultInitOverride", "true");
		LogManager.resetConfiguration();
		ConsoleAppender ca = new ConsoleAppender(new PatternLayout(
				"%-5p - %m%n"));
		ca.setName("log");
		LogManager.getRootLogger().addAppender(ca);
		ca.setThreshold(Level.INFO);
	}

	public static ResultSet search(String sparql, Dataset dataset)
			throws Exception {
		return QueryExecutionFactory.create(QueryFactory.create(sparql),
				dataset).execSelect();
	}

	public static String clean(String query) {
		int pos = query.indexOf(" ?graph") + 7;
		int pos2 = query.indexOf("where", pos);
		return query.substring(0, pos) + " " + query.substring(pos2);
	}

	public static String loadString(String file) throws Exception {

		StringBuilder b = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			b.append(line + "\n");
		}
		br.close();
		return b.toString();
	}

}
