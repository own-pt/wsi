package no.uio.ifi.wsi.sdp;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import lombok.Getter;
import no.uio.ifi.wsi.SemanticStructure;
import se.liu.ida.nlp.sdp.graph.Graph;
import se.liu.ida.nlp.sdp.io.GraphReader;

public class SDPGraph extends SemanticStructure {

	@Getter
	private Graph graph;
	private boolean hasSense;

	public SDPGraph(String struct, String svg, String id, String text,
			boolean hasSense_) {
		setId(id);
		setText(text);
		setStructure(struct);
		hasSense = hasSense_;
	}

	public SDPGraph() {
	}

	public SDPGraph(boolean hasSense_) {
		hasSense = hasSense_;
	}

	@Override
	public int getNodesNumber() {
		return graph.getNodes().size();
	}

	public Graph readGraph(String struct, boolean hasSense) {
		try {
			GraphReader gr = new GraphReader(new StringReader(struct));
			Graph g = gr.readGraph(hasSense);
			gr.close();

			return g;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}

	@Override
	public void load() {
		graph = readGraph(getStructure(), hasSense);
		setId(graph.id.substring(1));
		StringBuilder b = new StringBuilder();
		b = new StringBuilder();
		for (int i = 1; i < graph.getNodes().size(); i++)
			b.append(graph.getNodes().get(i).form + " ");
		setText(b.toString().trim());
	}

	@Override
	public void load(List<String> lines) {
		StringBuilder b = new StringBuilder();
		for (String line : lines)
			b.append(line + "\n");
		setStructure(b.toString());
		load();
	}

	public SemanticStructure newInstance() throws InstantiationException,
			IllegalAccessException {
		return new SDPGraph(hasSense);
	}
}
