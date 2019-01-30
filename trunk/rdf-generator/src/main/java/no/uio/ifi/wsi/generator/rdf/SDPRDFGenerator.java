package no.uio.ifi.wsi.generator.rdf;

import java.util.List;

import no.uio.ifi.wsi.Defaults;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.sdp.SDPGraph;
import se.liu.ida.nlp.sdp.graph.Graph;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.sparql.core.Quad;

public class SDPRDFGenerator extends RDFGenerator {

	private String format;

	public SDPRDFGenerator(String format) {
		super();
		this.format = format;
	}

	@Override
	public List<Quad> convert(SemanticStructure structure) {

		SDPGraph sgg = (SDPGraph) structure;

		String baseNamespace = Defaults.NAMESPACE + "/sdp/" + format;

		Graph s = sgg.getGraph();
		List<Quad> c = Lists.newArrayList();
		String sid = s.id.substring(1);
		int i = 0;

		for (se.liu.ida.nlp.sdp.graph.Node n : s.getNodes()) {
			if (i == 0) {
				i++;
				continue;
			}
			String nid = "" + n.id;
			c.add(createDatatypeStringPropety(nid, baseNamespace + "#form",
					n.form.toLowerCase(), sid));

			if (n.lemma != null)
				c.add(createDatatypeStringPropety(nid,
						baseNamespace + "#lemma", n.lemma.toLowerCase(), sid));

			if (n.sense != null)
				c.add(createDatatypeStringPropety(nid,
						baseNamespace + "#sense", n.sense.toLowerCase(), sid));

			if (n.pos != null)
				c.add(createDatatypeStringPropety(nid, baseNamespace + "#pos",
						n.pos.toLowerCase(), sid));
			if (n.isTop) {
				c.add(createDatatypeBooleanPropety(nid, baseNamespace + "#top",
						n.isTop, sid));
			}
			if (n.isPred) {
				c.add(createDatatypeBooleanPropety(nid,
						baseNamespace + "#pred", n.isPred, sid));
			}
		}
		for (se.liu.ida.nlp.sdp.graph.Edge e : s.getEdges()) {
			c.add(createObjectProperty("" + e.source, baseNamespace + "#"
					+ e.label.toLowerCase(), "" + e.target, sid));
			c.add(createObjectProperty("" + e.source, baseNamespace + "#role",
					"" + e.target, sid));
		}
		return c;
	}

}
