package no.uio.ifi.wsi.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.conll.ConllLine;
import no.uio.ifi.wsi.conll.ConllText;
import no.uio.ifi.wsi.generator.rdf.SDPRDFGenerator;
import no.uio.ifi.wsi.sdp.SDPGraph;
import se.liu.ida.nlp.sdp.graph.Edge;
import se.liu.ida.nlp.sdp.graph.Graph;
import se.liu.ida.nlp.sdp.graph.Node;

import com.google.common.collect.Sets;

public class SVGGenerator implements Comparator<Edge> {

public static void runSVG(CommandLineReader cmlReader) {
		InputReader reader = new InputReader(cmlReader);

		boolean sdp = cmlReader.getRdfGenerator() instanceof SDPRDFGenerator;
		while (reader.hasNext()) {
			SemanticStructure semanticStructure = reader.next();
			String dot = null;
			if (sdp)
				dot = generate((SDPGraph) semanticStructure, 9, "top");
			else
				dot = generate((ConllText) semanticStructure, 9, "root");
			saveString(cmlReader.getDotDirectory() + semanticStructure.getId(),
					dot, true, "UTF-8");
		}
	}

	public static String generate(ConllText g, int fontsize, String toplabel) {
		StringBuilder b = new StringBuilder();
		b.append("digraph ER {\n");
		b.append("\tgraph [rankdir=LR ranksep=0.001 splines=line]\n\n");
		for (ConllLine n : g.getLine()) {
			b.append("\tnode [ group=sentence  shape=plaintext fontsize="
					+ fontsize
					+ " width=0.0001  height=0.0001  color=none label=\""
					+ n.getForm() + "\\n" + n.getPos() + "\"]  N_" + n.getId()
					+ ";\n");

		}
		b.append("\n");

		for (int k = 1; k < g.getLine().size() - 2; k++) {
			b.append("\tN_" + k + " -> N_" + (k + 1)
					+ " [arrowhead=none constraint=true style=invis]\n");
		}

		b.append("\n");

		List<Edge> es = new ArrayList<Edge>();

		for (ConllLine n : g.getLine()) {
			if (n.getParent().equals("0"))
				continue;
			if (n.getParent().equals("_"))
				continue;
			Edge e = new Edge(Integer.parseInt(n.getId()), Integer.parseInt(n
					.getParent()), Integer.parseInt(n.getId()), n.getDeprel());
			es.add(e);
		}

		Collections.sort(es, new SVGGenerator());

		for (Edge e : es) {

			String eid = "N_" + e.source + "_" + e.target;
			String s = "N_" + e.source;
			String t = "N_" + e.target;

			b.append("\t"
					+ eid
					+ " [group=edges style=solid color=grey shape=box fontcolor=grey fontsize=7 width=0.0001 height=0.0001 label=\""
					+ e.label + "\"]\n");

			if (e.source < e.target) {
				b.append("\t" + s + " -> " + eid);
				b.append(" [weight=5 constraint=true arrowhead=none color=blue] \n");
				b.append("\t" + eid + " -> " + t);
				b.append(" [weight=10 arrowhead=open constraint=false color=blue] \n");
			} else {
				b.append("\t" + t + " -> " + eid);
				b.append(" [weight=10 constraint=true dir=back arrowtail=open color=blue] \n");
				b.append("\t" + eid + " -> " + s);
				b.append(" [weight=5 constraint=false dir=back arrowtail=none color=blue] \n");
			}
			b.append("\n");
		}

		for (ConllLine n : g.getLine()) {
			if (n.getParent().equals("0")) {
				b.append("\tNT_"
						+ n.getId()
						+ " [group=top style=solid color=grey shape=ellipse fontcolor=grey fontsize=7 width=0.0001 height=0.0001 label=\""
						+ toplabel + "\"]\n");
				b.append("\tN_" + n.getId() + " -> NT_" + n.getId()
						+ " [arrowhead=none constraint=true]\n");
			}
		}
		b.append("}");
		return b.toString();
	}

	public static String generate(SDPGraph fg, int fontsize, String toplabel) {
		Graph g = fg.getGraph();
		StringBuilder b = new StringBuilder();
		b.append("digraph ER {\n");
		b.append("\tgraph [rankdir=LR ranksep=0.001 splines=line]\n\n");
		int i = 0;
		for (Node n : g.getNodes()) {
			if (i == 0) {
				i++;
				continue;
			}
			b.append("\tnode [ group=sentence  shape=plaintext fontsize="
					+ fontsize
					+ " width=0.0001  height=0.0001  color=none label=\""
					+ n.form + "\\n" + n.pos
					+ (n.sense == null ? "" : "\\n" + n.sense) + "\"]  N_"
					+ n.id + ";\n");

		}
		b.append("\n");

		for (int k = 1; k < g.getNodes().size() - 1; k++) {
			b.append("\tN_" + k + " -> N_" + (k + 1)
					+ " [arrowhead=none constraint=true style=invis]\n");
		}

		b.append("\n");

		List<Edge> es = new ArrayList<Edge>(g.getEdges());
		Collections.sort(es, new SVGGenerator());

		for (Edge e : es) {

			String eid = "N_" + e.source + "_" + e.target;
			String s = "N_" + e.source;
			String t = "N_" + e.target;

			b.append("\t"
					+ eid
					+ " [group=edges style=solid color=grey shape=box fontcolor=grey fontsize=7 width=0.0001 height=0.0001 label=\""
					+ e.label + "\"]\n");

			if (e.source < e.target) {
				b.append("\t" + s + " -> " + eid);
				b.append(" [weight=5 constraint=true arrowhead=none color=blue] \n");
				b.append("\t" + eid + " -> " + t);
				b.append(" [weight=10 arrowhead=open constraint=false color=blue] \n");
			} else {
				b.append("\t" + t + " -> " + eid);
				b.append(" [weight=10 constraint=true dir=back arrowtail=open color=blue] \n");
				b.append("\t" + eid + " -> " + s);
				b.append(" [weight=5 constraint=false dir=back arrowtail=none color=blue] \n");
			}
			b.append("\n");
		}

		for (Node n : g.getNodes()) {
			if (i == 0) {
				i++;
				continue;
			}
			if (n.isTop) {
				b.append("\tNT_"
						+ n.id
						+ " [group=top style=solid color=grey shape=ellipse fontcolor=grey fontsize=7 width=0.0001 height=0.0001 label=\""
						+ toplabel + "\"]\n");
				b.append("\tN_" + n.id + " -> NT_" + n.id
						+ " [arrowhead=none constraint=true]\n");
			}
		}
		b.append("}");
		return b.toString();
	}

	public static String generateCompact(ConllText g) {
		StringBuilder b = new StringBuilder();
		b.append("digraph {\n");
		for (ConllLine n : g.getLine()) {
			b.append("\tnode [color=none shape=plaintext label=\""
					+ n.getForm().replace("\"", "\\\"") + "\\n"
					+ n.getLemma().replace("\"", "\\\"") + "\\n"
					+ n.getPos().replace("\"", "\\\"") + "\"]");
			b.append(" N_" + n.getId() + "; ");
			b.append("\n");
		}
		for (ConllLine n : g.getLine()) {
			if (!n.getParent().equals("0")) {
				b.append("\tN_" + n.getParent() + " -> N_" + n.getId()
						+ " [arrowhead=open label=\"" + n.getDeprel()
						+ "\" ] \n");
			}
		}
		b.append("}");
		return b.toString();
	}

	public static String generateCompact(Graph g) {
		int i = 0;
		StringBuilder b = new StringBuilder();
		b.append("digraph ER {\n");
		b.append("\trankdir=\"LR\"\n\n");

		Set<Integer> ok = Sets.newHashSet();
		for (Edge e : g.getEdges()) {
			ok.add(e.source);
			ok.add(e.target);
		}

		for (Node n : g.getNodes()) {
			if (i == 0) {
				i++;
				continue;
			}

			if (!ok.contains(n.id)) {
				continue;
			}

			b.append("\tnode [color=none shape=plaintext label=\"" + n.form
					+ "+" + n.lemma + "/" + n.pos + "\"]");

			b.append(" N_" + n.id + "; ");
			b.append("\n");

		}

		for (Edge e : g.getEdges()) {

			b.append("\tN_" + e.source + " -> N_" + e.target
					+ " [arrowhead=open label=\"" + e.label + "\" ] \n");

		}
		b.append("}");
		return b.toString();
	}

	public static String runDot(String dot) throws Exception {

		saveString("/tmp/temp.dot", dot, true, "UTF-8");

		Process p = Runtime.getRuntime().exec("dot -Tsvg /tmp/temp.dot");
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		String s = null;
		StringBuilder b = new StringBuilder();
		while ((s = stdInput.readLine()) != null) {
			b.append(s + "\n");
		}

		String sx = b.toString();
		sx = sx.substring(sx.indexOf("<svg "));

		return sx;
	}

	public static void saveString(String filename, String s, boolean overwrite,
			String encoding) {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), encoding));
			out.write(s);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("The system can not write in "
					+ filename + " because:\n" + e.getMessage());
		}
	}

	public static String toSVG(SDPGraph g, int fontsize, String toplabel) {
		try {
			String s = runDot(generate(g, fontsize, toplabel));
			return s;
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(SVGGenerator.class.getName())
					.log(Level.SEVERE, null, e);
			return "";
		}
	}

	@Override
	public int compare(Edge o1, Edge o2) {
		double v1 = Math.abs(o1.source - o1.target);
		double v2 = Math.abs(o2.source - o2.target);
		return Double.compare(v1, v2);
	}
}
