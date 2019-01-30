/*
 * See the file "LICENSE" for the full license governing this code.
 */
package se.liu.ida.nlp.sdp.tools;

import se.liu.ida.nlp.sdp.graph.Graph;
import se.liu.ida.nlp.sdp.graph.GraphInspector;
import se.liu.ida.nlp.sdp.graph.Node;
import se.liu.ida.nlp.sdp.io.GraphReader;

/**
 * Print statistics about a collection of graphs.
 * 
 * @author Marco Kuhlmann <marco.kuhlmann@liu.se>
 */
public class Analyzer {

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new Analyzer();
		for (String arg : args) {
			GraphReader reader = new GraphReader(arg);
			Graph graph;
			while ((graph = reader.readGraph()) != null) {
				analyzer.update(graph);
			}
			reader.close();
			analyzer.finish();
			System.out.format("%% semi-connected: %f%n", analyzer.pcSemiConnected);
			// System.out.format("%% cyclic: %f%n", analyzer.pcCyclic);
		}
	}

	private int maxIndegreeGlobal;
	private int nGraphs;

	private double pcSemiConnected;

	public void finish() {
		//
		pcSemiConnected /= nGraphs;
	}

	public void update(Graph graph) {
		GraphInspector analyzer = new GraphInspector(graph);

		boolean isSemiConnected = true;
		int maxIndegree = 0;
		int maxOutdegree = 0;
		int nSingletons = 0;

		for (Node node : graph.getNodes()) {

			maxIndegree = Math.max(maxIndegree, node.getNIncomingEdges());
			maxOutdegree = Math.max(maxOutdegree, node.getNOutgoingEdges());
			if (!node.hasIncomingEdges() && !node.hasOutgoingEdges()) {
				nSingletons++;
			}
		}
		if (analyzer.getNComponents() - nSingletons > 1) {
			System.err.format("%s%n", graph.id);
			isSemiConnected = false;
		}

		nGraphs++;

		pcSemiConnected += isSemiConnected ? 1.0 : 0.0;
		maxIndegreeGlobal = Math.max(maxIndegreeGlobal, maxIndegree);
	}
}
