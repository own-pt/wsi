package no.uio.ifi.wsi.generator.rdf;

import java.util.List;

import no.uio.ifi.wsi.Defaults;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.conll.ConllLine;
import no.uio.ifi.wsi.conll.ConllText;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.sparql.core.Quad;

public class CONLLRDFGenerator extends RDFGenerator {

	@Override
	public List<Quad> convert(SemanticStructure structure) {

		ConllText g = (ConllText) structure;

		String baseNameSpace = Defaults.NAMESPACE + "/conll";

		List<Quad> c = Lists.newArrayList();
		String sid = g.getId();
		for (ConllLine n : g.getLine()) {
			String nid = "" + n.getId();
			c.add(createDatatypeStringPropety(nid, baseNameSpace + "#form",
					n.getForm(), sid));
			if (n.getLemma() != null)
				c.add(createDatatypeStringPropety(nid,
						baseNameSpace + "#lemma", n.getLemma().toLowerCase(),
						sid));
			c.add(createDatatypeStringPropety(nid, baseNameSpace + "#pos", n
					.getPos().toLowerCase(), sid));
			if (n.getParent().equals("0")) {
				c.add(createDatatypeBooleanPropety(nid, baseNameSpace + "#top",
						true, sid));
			} else {
				c.add(createObjectProperty("" + n.getParent(), baseNameSpace
						+ "#" + n.getDeprel().toLowerCase(), "" + n.getId(),
						sid));
				c.add(createObjectProperty("" + n.getParent(), baseNameSpace
						+ "#deps", "" + n.getId(), sid));
			}
		}
		return c;
	}
}
