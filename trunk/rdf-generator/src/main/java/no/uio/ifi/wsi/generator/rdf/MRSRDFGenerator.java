package no.uio.ifi.wsi.generator.rdf;

import java.util.List;
import java.util.Set;

import no.uio.ifi.wsi.Defaults;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.mrs.Constraint;
import no.uio.ifi.wsi.mrs.MRS;
import no.uio.ifi.wsi.mrs.MRSPredication;
import no.uio.ifi.wsi.mrs.MRSRole;
import no.uio.ifi.wsi.mrs.QEQConstraint;
import no.uio.ifi.wsi.mrs.Variable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.vocabulary.RDFS;

public class MRSRDFGenerator extends RDFGenerator {

	private void addConstraint(String sid, Constraint s, List<Quad> c,
			Set<String> in, String namespace) {
		if (!(s instanceof QEQConstraint))
			return;
		QEQConstraint cs = (QEQConstraint) s;
		addVariable(sid, in, cs.getArg1(), c, namespace);
		addVariable(sid, in, cs.getArg2(), c, namespace);
		c.add(createObjectProperty(cs.getArg1().getName(), namespace + "qeq",
				cs.getArg2().getName(), sid));
		c.add(createObjectProperty(cs.getArg2().getName(), namespace + "qeq",
				cs.getArg1().getName(), sid));
	}

	private void addPredication(String sid, MRSPredication s, List<Quad> c,
			Set<String> in, String namespace) {

		String predicate = s.getPredicate();
		if (predicate.endsWith("_rel"))
			predicate = predicate.substring(0, predicate.length() - 4);

		c.add(createDatatypeStringPropety(s.getId(), namespace + "predicate",
				predicate, sid));

		if (s.getCarg() != null)
			c.add(createDatatypeStringPropety(s.getId(), namespace + "carg",
					s.getCarg(), sid));

		if (s.getLbl() != null) {
			c.add(createObjectProperty(s.getId(), namespace + "lbl", s.getLbl()
					.getName(), sid));
			addVariable(sid, in, s.getLbl(), c, namespace);
		}

		for (MRSRole r : s.getRoles()) {
			addVariable(sid, in, r.getTarget(), c, namespace);
			c.add(createObjectProperty(s.getId(), namespace
					+ r.getName().toLowerCase(), r.getTarget().getName(), sid));
			c.add(createObjectProperty(s.getId(), namespace + "role", r
					.getTarget().getName(), sid));
		}
	}

	private void addVariable(String sid, Set<String> in, Variable l,
			List<Quad> c, String namespace) {
		if (in.contains(l.getName()))
			return;
		in.add(l.getName());

		String value = l.getType().toUpperCase();

		c.add(createObjectProperty(l.getName(), RDFS.getURI() + "type",
				namespace + value, sid));

		if (value.equals("H") || value.equals("X")) {
			c.add(createObjectProperty(l.getName(), RDFS.getURI() + "type",
					namespace + "P", sid));
		}

		if (value.equals("E") || value.equals("X")) {
			c.add(createObjectProperty(l.getName(), RDFS.getURI() + "type",
					namespace + "I", sid));
		}

		for (no.uio.ifi.wsi.mrs.Property p : l.getProperties()) {

			String propertyName = p.getName().toLowerCase();
			String propertyClass = p.getName().toLowerCase();
			if (propertyClass.equals("gen"))
				propertyClass = "gender";
			String propertyValueClass = p.getValue().toLowerCase();

			if (propertyValueClass.equals("+"))
				propertyValueClass = "True";

			if (propertyValueClass.equals("-"))
				propertyValueClass = "False";

			c.add(createObjectProperty(l.getName(), namespace + propertyName,
					l.getName() + "_" + propertyName, sid));

			c.add(createObjectProperty(l.getName() + "_" + propertyName,
					RDFS.getURI() + "type", namespace + propertyClass, sid));

			c.add(createObjectProperty(l.getName() + "_" + propertyName,
					RDFS.getURI() + "type", namespace + propertyValueClass, sid));

			if (propertyValueClass.equals("past")
					|| propertyValueClass.equals("non-past")) {
				c.add(createObjectProperty(l.getName() + "_" + propertyName,
						RDFS.getURI() + "type", namespace + "tensed", sid));
			}
			if (propertyValueClass.equals("pres")
					|| propertyValueClass.equals("fut")) {
				c.add(createObjectProperty(l.getName() + "_" + propertyName,
						RDFS.getURI() + "type", namespace + "non-past", sid));
				c.add(createObjectProperty(l.getName() + "_" + propertyName,
						RDFS.getURI() + "type", namespace + "tensed", sid));
			}

		}

	}

	@Override
	public List<Quad> convert(SemanticStructure structure) {
		MRS m = (MRS) structure;
		String namespace = Defaults.NAMESPACE + "/mrs#";

		Set<String> in = Sets.newHashSet();

		List<Quad> out = Lists.newArrayList();

		for (MRSPredication s : m.getPredications())
			addPredication(m.getId(), s, out, in, namespace);

		for (Constraint s : m.getConstraints())
			addConstraint(m.getId(), s, out, in, namespace);

		if (!in.contains(m.getTop().getName()))
			addVariable(m.getId(), in, m.getTop(), out, namespace);

		out.add(createDatatypeBooleanPropety(m.getTop().getName(), namespace
				+ "top", Boolean.TRUE, m.getId()));

		if (!in.contains(m.getIndex().getName()))
			addVariable(m.getId(), in, m.getIndex(), out, namespace);

		out.add(createDatatypeBooleanPropety(m.getIndex().getName(), namespace
				+ "index", Boolean.TRUE, m.getId()));

		return out;
	}

}
