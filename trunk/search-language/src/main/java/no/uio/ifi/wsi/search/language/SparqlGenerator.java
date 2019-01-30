package no.uio.ifi.wsi.search.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import no.uio.ifi.wsi.Defaults;
import no.uio.ifi.wsi.index.TermCount;
import no.uio.ifi.wsi.index.TermIndex;

import com.google.common.collect.Lists;

public abstract class SparqlGenerator {

	private static void addUniqFilter(Set<String> variables,
			StringBuilder output) {
		List<String> vars = Lists.newArrayList(variables);
		output.append("FILTER (");
		for (int i = 0; i < vars.size() - 1; i++) {
			String v1 = "?" + vars.get(i);
			for (int k = i + 1; k < vars.size(); k++) {
				String v2 = "?" + vars.get(k);
				output.append(" ( !bound(").append(v1).append(") || !bound(")
						.append(v2).append(") || ").append(v1).append("!=")
						.append(v2).append(" ) &&");
			}
		}
		output.deleteCharAt(output.length() - 1);
		output.deleteCharAt(output.length() - 1);
		output.append(")\n");
	}

	public void addWordsToWeightedString(StringBuilder b, String id,
			List<TermCount> words, int pos, String relationName) {
		TermCount doc = words.get(pos);
		String value = doc.getTerm();
		String s = "?" + id + " " + prefix() + ":" + relationName + " \""
				+ value + "\"^^xsd:string";
		if (pos == words.size() - 1) {
			b.append("{ " + s + " }");
			return;
		}
		addWordsToWeightedString(b, id, words.subList(1, words.size()), pos++,
				relationName);
		b.append(" UNION { " + s + " }");
	}

	public static boolean isRgexp(String s) {
		char prev = ' ';
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if ((ch == '*' || ch == '?') && prev != '\\') {
				return true;
			}
			prev = ch;
		}
		return false;
	}

	public static String unquote(String s) {
		return s.replace("\\", "")
				.replace(ExpressionParser.QUOTE_REPLACE, '\\');
	}

	@Getter
	@Setter
	private int defaultRegexResults = 20000000;

	@Getter
	@Setter
	private TermIndex termIndex;

	@Getter
	@Setter
	private int NOT_LEVEL = 10;

	public abstract String prefix();

	public abstract String name();

	public WeightedString addRole(String id, RoleParse r, String topEdge) {

		String name = r.getName().toLowerCase();

		boolean isRegexp = isRgexp(name);

		if (isRegexp && name.length() == 1) {
			name = topEdge;
			return addRole(id, name, Integer.MAX_VALUE, r);
		}
		if (name.equals(topEdge)) {
			return addRole(id, name, Integer.MAX_VALUE, r);
		}
		List<TermCount> docs = termIndex.searchTerm(name, isRegexp, namespace()
				+ "#", defaultRegexResults);
		if (docs.isEmpty()) {
			throw new RuntimeException("Unrecognized relation " + name);
		}
		List<WeightedString> ws = addRoleVariants(id, docs, r);
		Collections.sort(ws, ws.get(0));

		return new WeightedString(WeightedString.level(ws),
				WeightedString.toUnionString(ws), WeightedString.weight(ws));
	}

	public abstract WeightedString addRole(String id, String name, long weight,
			RoleParse r);

	public List<WeightedString> addRoleVariants(String id,
			List<TermCount> docs, RoleParse r) {
		String doc = docs.get(0).getTerm();

		WeightedString first = addRole(id, doc, docs.get(0).getCount(), r);
		if (docs.size() == 1) {
			List<WeightedString> out = Lists.newArrayList();
			out.add(first);
			return out;
		}
		List<WeightedString> second = addRoleVariants(id,
				docs.subList(1, docs.size()), r);
		second.add(first);
		return second;
	}

	public String namespace() {
		return Defaults.NAMESPACE + "/" + name();
	}

	public List<WeightedString> addType(String id, int level, String value,
			String type) {

		List<WeightedString> out = new ArrayList<WeightedString>();

		boolean isRegexp = isRgexp(value);
		value = unquote(value);

		String namespace = namespace() + "#";
		if (!isRegexp) {
			String key = namespace + type;
			TermCount score = termIndex.weight(value, key);
			if (score == null)
				throw new RuntimeException("Unrecognized " + type + " " + value);
			value = score.getTerm();
			String s = "?" + id + " " + prefix() + ":" + type + " \"" + value
					+ "\"^^xsd:string .\n";
			out.add(new WeightedString(level, s, score.getCount()));
			return out;
		}
		if (value.length() == 1)
			return out;
		String key = namespace + type;
		List<TermCount> results = termIndex.searchTerm(value, isRegexp, key,
				defaultRegexResults);
		if (results == null || results.isEmpty())
			throw new RuntimeException("Unrecognized " + type + " " + value);

		if (results.size() > 25) {
			String s = "?" + id + " " + prefix() + ":" + type + " ?" + id
					+ "TEXT";
			out.add(new WeightedString(10000, s, 1000));

			String perlRegEx = toPerl(value);

			s = "FILTER regex(?" + id + "TEXT, \"^" + perlRegEx + "$\")";
			out.add(new WeightedString(10000, s, 1000));
			return out;
		}

		StringBuilder b = new StringBuilder();
		b.append("{ ");
		addWordsToWeightedString(b, id, results, 0, type);
		b.append(" } ");
		out.add(new WeightedString(level, b.toString(), 10000));
		return out;
	}

	private String toPerl(String value) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < value.length(); i++) {
			if (i == 0 || value.charAt(i - 1) == '\\') {
				builder.append(value.charAt(i));
				continue;
			}
			char ch = value.charAt(i);
			if (ch == '+' || ch == '*' || ch == '?')
				builder.append('.');
			builder.append(ch);
		}
		return builder.toString();
	}

	public String getId(TempData temp) {
		String id = "" + temp.getCount();
		temp.setCount(temp.getCount() + 1);
		return id;
	}

	public void initialize(StringBuilder output) {
		output.append("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n");
		output.append("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n");
		output.append("PREFIX owl:<http://www.w3.org/2002/07/owl#>\n");
		output.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
		output.append("PREFIX " + prefix() + ":<" + namespace() + "#>\n");
	}

	private WeightedString toSparql(Parse p, String annotation, TempData temp) {
		if (!p.isLeaf())
			return toSparqlOperator((OperatorParse) p, annotation, temp);
		return toSparqlNode(p, annotation, temp);
	}

	public abstract WeightedString toSparqlNode(Parse p, String annotation,
			TempData temp);

	private WeightedString toSparqlOperator(OperatorParse p, String annotation,
			TempData temp) {
		if (p.getOperator() == ExpressionParser.NOTOP) {
			WeightedString left = toSparql(p.getLeft(), annotation, temp);
			String s = "FILTER NOT EXISTS { " + left.toSparqlString() + "\n}\n";
			return new WeightedString(NOT_LEVEL, s, 0);
		}
		WeightedString l = toSparql(p.getLeft(), annotation, temp);
		WeightedString r = toSparql(p.getRight(), annotation, temp);
		List<WeightedString> xx = Lists.newArrayList();
		if (p.getOperator() == ExpressionParser.OROP) {

			if (l.getValue() == null) {
				Collections.sort(l.getValues(), l.getValues().get(0));
				l.setValue(l.toSparqlString());
				l.setLevel(l.getValues().get(l.getValues().size() - 1)
						.getLevel());
			}

			if (r.getValue() == null) {
				Collections.sort(r.getValues(), r.getValues().get(0));
				r.setValue(r.toSparqlString());
				r.setLevel(r.getValues().get(r.getValues().size() - 1)
						.getLevel());
			}

			xx.add(l);
			xx.add(r);
			Collections.sort(xx, l);
			return new WeightedString(WeightedString.level(xx),
					WeightedString.toUnionString(xx), xx.get(0).getWeight()
							+ xx.get(1).getWeight());
		}

		if (l.getValue() == null)
			xx.addAll(l.getValues());
		else
			xx.add(l);
		if (r.getValue() == null)
			xx.addAll(r.getValues());
		else
			xx.add(r);
		return new WeightedString(xx);
	}

	public String toSparqlStructure(Parse parse, String annotation) {
		TempData temp = TempData.newInstance();
		StringBuilder output = new StringBuilder();
		initialize(output);
		String sparql = toSparql(parse, annotation, temp).toSparqlString();
		output.append("select ?graph ");
		for (String s : temp.getVariables()) {
			String var = "?" + s;
			output.append("(group_concat(" + var + "; separator = \"\\t\") AS "
					+ var + "Values)");
		}
		output.append("\nwhere { \n ");
		output.append(" GRAPH ?graph { \n");
		output.append(sparql);
		if (temp.getVariables().size() > 1)
			addUniqFilter(temp.getVariables(), output);
		output.append("}\n }\n");
		output.append("GROUP BY ?graph\n");
		output.append("ORDER BY ?graph\n");
		sparql = output.toString();
		return sparql;

	}
}
