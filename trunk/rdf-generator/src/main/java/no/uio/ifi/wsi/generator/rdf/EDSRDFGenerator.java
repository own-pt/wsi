package no.uio.ifi.wsi.generator.rdf;

import java.util.List;

import no.uio.ifi.wsi.Defaults;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.eds.EDS;
import no.uio.ifi.wsi.eds.EDSPredication;
import no.uio.ifi.wsi.eds.EDSRole;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.sparql.core.Quad;

public class EDSRDFGenerator extends RDFGenerator {

	@Override
	public List<Quad> convert(SemanticStructure structure) {

		EDS eds = (EDS) structure;

		String namespace = Defaults.NAMESPACE + "/eds#";

		List<Quad> c = Lists.newArrayList();
		for (EDSPredication n : eds.getPredications()) {

			String predicate = n.getPredicate();
			if (predicate.endsWith("_rel"))
				predicate = predicate.substring(0, predicate.length() - 4);

			c.add(createDatatypeStringPropety(n.getId(), namespace
					+ "predicate", predicate, eds.getId()));
			if (n.getCarg() != null)
				c.add(createDatatypeStringPropety(n.getId(),
						namespace + "carg", n.getCarg(), eds.getId()));
			for (EDSRole e : n.getRoles()) {
				if (e.getName().startsWith("("))
					continue;
				c.add(createObjectProperty(n.getId(), namespace
						+ e.getName().toLowerCase(), e.getTarget().getId(),
						eds.getId()));
				c.add(createObjectProperty(n.getId(), namespace + "role", e
						.getTarget().getId(), eds.getId()));
			}
		}

		String top = eds.getTop();
		if (top.contains("("))
			top = top.substring(0, top.indexOf("(")).trim();
		c.add(createDatatypeBooleanPropety(top, namespace + "top",
				Boolean.TRUE, eds.getId()));

		return c;
	}

}
