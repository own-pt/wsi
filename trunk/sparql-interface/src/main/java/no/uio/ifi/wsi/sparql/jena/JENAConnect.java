package no.uio.ifi.wsi.sparql.jena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

public class JENAConnect {

	public static void main(String[] args) throws Exception {
		JENAConnect jc = new JENAConnect("/home/milen/export/deepbank-test/tdb/1");
		String sparql = "PREFIX mrs:<http://www.delph-in.net/rdf/mrs#>"
				+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>" + "PREFIX owl:<http://www.w3.org/2002/07/owl#> "
				+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "select ?graph where {"
				+ "GRAPH ?graph { " + "?100<http://www.delph-in.net/rdf/mrs#TENSE> \"PAST\"^^xsd:string ." + "}" + "}";
		ResultSet rs = jc.search(sparql);

		while (rs.hasNext())
			System.out.println(rs.next());
	}

	private final Dataset dataset;

	public JENAConnect(String path) {
		System.out.println(path);
		dataset = TDBFactory.createDataset(path);
		TDB.getContext().set(TDB.symUnionDefaultGraph, true);
	}

	public void close() {
		dataset.close();
	}

	public ResultSet search(String sparql) throws Exception {
		return QueryExecutionFactory.create(QueryFactory.create(sparql), dataset).execSelect();
	}

}
