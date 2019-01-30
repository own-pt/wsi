package no.uio.ifi.wsi.search.language.impl.mrs;

import java.util.List;

import no.uio.ifi.wsi.mrs.Property;
import no.uio.ifi.wsi.search.language.Parse;
import no.uio.ifi.wsi.search.language.RoleParse;
import no.uio.ifi.wsi.search.language.SparqlGenerator;
import no.uio.ifi.wsi.search.language.TempData;
import no.uio.ifi.wsi.search.language.WeightedString;

import com.google.common.collect.Lists;

public class MRSSparqlGenerator extends SparqlGenerator {

	private final int QEQ_LEVEL = 5;
	private final int CARG_LEVEL = 1;
	private final int ID_LEVEL = 4;
	private final int PREDICATE_LEVEL = 2;
	private final int ROLE_LEVEL = 4;
	private final int TOP_LEVEL = 3;
	private final int TYPE_LEVEL = 6;

	public MRSSparqlGenerator() {
		setNOT_LEVEL(7);
	}

	@Override
	public WeightedString addRole(String id, String text, long weight,
			RoleParse r) {
		String rel = text;
		if (!text.contains("+")) {
			rel = "mrs" + ":" + rel;
		} else {
			rel = "<" + namespace() + "#" + rel + ">";

		}
		rel = "?" + id + " " + rel + " ?" + r.getTo();

		List<WeightedString> out = Lists.newArrayList();

		WeightedString ws = new WeightedString(ROLE_LEVEL, rel, weight);

		out.add(ws);

		for (Property px : r.getParams()) {

			if (px.getValue() == null) {
				String propertyName = px.getName().toLowerCase();
				String propertyClass = px.getName().toLowerCase();
				if (propertyClass.equals("gen"))
					propertyClass = "gender";
				String line = "?" + r.getTo() + " " + prefix() + ":"
						+ propertyName + " ?" + r.getTo() + "_" + propertyName;
				String line2 = "?" + r.getTo() + "_" + propertyName + " "
						+ "rdfs:type" + " " + prefix() + ":" + propertyClass;
				out.add(new WeightedString(ROLE_LEVEL, line, 0));
				out.add(new WeightedString(ROLE_LEVEL, line2, 0));
			} else {
				String propertyName = px.getName().toLowerCase();
				String propertyValueClass = px.getValue();
				if (propertyValueClass.equals("+"))
					propertyValueClass = "True";

				if (propertyValueClass.equals("-"))
					propertyValueClass = "False";

				String line = "?" + r.getTo() + " " + prefix() + ":"
						+ propertyName + " ?" + r.getTo() + "_" + propertyName;

				String line2 = "?" + r.getTo() + "_" + propertyName + " "
						+ "rdfs:type" + " " + prefix() + ":"
						+ propertyValueClass;
				out.add(new WeightedString(ROLE_LEVEL, line, 0));
				out.add(new WeightedString(ROLE_LEVEL, line2, 0));

			}
		}

		if (out.size() == 1)
			return out.get(0);
		return new WeightedString(out);
	}

	public WeightedString addType(String s, String annotation) {
		char ch = s.charAt(0);
		if (ch <= '9' && ch >= '0')
			return null;
		if (ch == 'X' || ch == 'I' || ch == 'E' || ch == 'P' || ch == 'H')
			return null;
		String type = "" + ch;
		return new WeightedString(TYPE_LEVEL, "?" + s + " rdfs:type" + " "
				+ annotation + ":" + type.toUpperCase(), 0);
	}

	private String toSparql(String[] a) {
		return "?" + a[0] + " mrs:qeq ?" + a[1];
	}

	@Override
	public WeightedString toSparqlNode(Parse pp, String annotation,
			TempData temp) {

		if (pp instanceof QEQParse)
			return toSparqlQEQ((QEQParse) pp);

		MRSParse p = (MRSParse) pp;
		String id = getId(temp);

		List<WeightedString> out = Lists.newArrayList();

		temp.getVariables().add(id);

		if (p.getPredicate() != null) {
			out.addAll(addType(id, PREDICATE_LEVEL, p.getPredicate(),
					"predicate"));
		}

		if (p.getCarg() != null) {
			out.addAll(addType(id, CARG_LEVEL, p.getCarg(), "carg"));
		}

		for (RoleParse r : p.getRoles()) {
			if (r.getTo() == null)
				r.setTo(getId(temp));
			temp.getVariables().add(r.getTo());
			out.add(addRole(id, r, "role"));

			WeightedString type = addType(r.getTo(), annotation);
			if (type != null)
				out.add(type);
		}

		if (p.isTop()) {
			String s = "?" + id + " " + annotation
					+ ":top  \"true\"^^xsd:boolean";
			out.add(new WeightedString(TOP_LEVEL, s, 0));
		}

		if (p.getId() != null) {
			String s = "?" + id + " " + annotation + ":lbl" + " ?" + p.getId();
			out.add(new WeightedString(ID_LEVEL, s, 0));
			WeightedString ws = addType(p.getId(), annotation);
			if (ws != null)
				out.add(ws);
		}
		return new WeightedString(out);
	}

	private WeightedString toSparqlQEQ(QEQParse pp) {
		StringBuilder b = new StringBuilder();
		for (String[] a : pp.getEqs()) {
			b.append(toSparql(a) + " .\n");
		}
		b.deleteCharAt(b.length() - 1);
		b.deleteCharAt(b.length() - 1);
		return new WeightedString(QEQ_LEVEL, b.toString(), 0);
	}

	@Override
	public String prefix() {
		return "mrs";
	}

	@Override
	public String name() {
		return "mrs";
	}
}
