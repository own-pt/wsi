package no.uio.ifi.wsi.gui.html;

import java.util.List;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.conll.ConllLine;
import no.uio.ifi.wsi.conll.ConllText;
import no.uio.ifi.wsi.eds.EDS;
import no.uio.ifi.wsi.eds.EDSPredication;
import no.uio.ifi.wsi.eds.EDSRole;
import no.uio.ifi.wsi.mrs.Constraint;
import no.uio.ifi.wsi.mrs.MRS;
import no.uio.ifi.wsi.mrs.MRSPredication;
import no.uio.ifi.wsi.mrs.MRSRole;
import no.uio.ifi.wsi.mrs.Property;
import no.uio.ifi.wsi.mrs.QEQConstraint;
import no.uio.ifi.wsi.mrs.Variable;
import no.uio.ifi.wsi.sdp.SDPGraph;

import org.apache.commons.lang3.StringEscapeUtils;

import se.liu.ida.nlp.sdp.graph.Edge;
import se.liu.ida.nlp.sdp.graph.Graph;
import se.liu.ida.nlp.sdp.graph.Node;

public class StructureToHTML {

	public static String toHTML(SemanticStructure ss) {
		if (ss instanceof EDS)
			return toHTML((EDS) ss);
		if (ss instanceof MRS)
			return toHTML((MRS) ss);
		if (ss.getSvg() != null)
			return ss.getSvg();
		if (ss instanceof ConllText)
			return toHTML((ConllText) ss);
		if (ss instanceof SDPGraph)
			return toHTML((SDPGraph) ss);
		return HTMLUtils.toHTMLTable(ss.getStructure());
	}

	private static String toHTML(SDPGraph s) {
		StringBuilder b = new StringBuilder();
		b.append("<table class=\"mrsEds\">\n");
		Graph g = s.getGraph();

		for (int i = 1; i < g.getNodes().size(); i++) {
			Node n = g.getNodes().get(i);
			b.append("<tr><td>");
			b.append(toHtmlID("" + n.id, s.getId()));
			b.append("</td>");
			b.append("<td>");
			b.append("<span id=\"" + s.getId() + "-" + i + "\">");
			b.append(StringEscapeUtils.escapeHtml4(n.form));
			b.append("</span>");
			b.append("</td>");
			b.append("<td>");
			b.append(StringEscapeUtils.escapeHtml4(n.lemma));
			b.append("</td>");
			b.append("<td>");
			b.append(StringEscapeUtils.escapeHtml4(n.pos));
			b.append("</td>");
			if (n.sense != null) {
				b.append("<td>");
				b.append(StringEscapeUtils.escapeHtml4(n.sense));
				b.append("</td>");
			}
			b.append("<td>");
			b.append("[");
			for (Edge e : n.getOutgoingEdges()) {
				b.append("&nbsp;");
				b.append(StringEscapeUtils.escapeHtml4(e.label));
				b.append("&nbsp;");
				b.append(toHtmlID("" + e.target, s.getId()));
				b.append(",");
			}
			if (n.getOutgoingEdges().size() > 0)
				b.deleteCharAt(b.length() - 1);
			b.append("]");

			b.append("</td></tr>").append("\n");
		}
		b.append("</table>\n");
		return b.toString();
	}

	private static String toHTML(ConllText s) {
		StringBuilder b = new StringBuilder();
		b.append("<table class=\"mrsEds\">\n");
		int i = 1;
		for (ConllLine n : s.getLine()) {
			b.append("<tr><td>");
			b.append(toHtmlID(n.getId(), s.getId()));
			b.append("</td>");
			b.append("<td>");
			b.append("<span id=\"" + s.getId() + "-" + i + "\">");
			i++;
			b.append(StringEscapeUtils.escapeHtml4(n.getForm()));
			b.append("</span>");
			b.append("</td>");
			b.append("<td>");
			b.append(StringEscapeUtils.escapeHtml4(n.getLemma()));
			b.append("</td>");
			b.append("<td>");
			b.append(StringEscapeUtils.escapeHtml4(n.getCpos()));
			b.append("</td>");
			b.append("<td>");
			b.append(StringEscapeUtils.escapeHtml4(n.getPos()));
			b.append("</td>");
			b.append("<td>");
			b.append(StringEscapeUtils.escapeHtml4(n.getDeprel()));
			b.append("</td>");
			b.append("<td>");
			if (n.getParent() != null)
				b.append(toHtmlID(n.getParent(), s.getId()));
			b.append("</td></tr>").append("\n");
		}
		b.append("</table>\n");
		return b.toString();
	}

	private static String toHTML(EDS s) {
		StringBuilder b = new StringBuilder();
		b.append("<table class=\"mrsEds\">\n");
		int i = 1;
		for (EDSPredication n : s.getPredications()) {
			if (n.getEnd() != -1)
				b.append("<tr><td nowrap onMouseOver=\"highlight(" + s.getId()
						+ ", " + n.getStart() + ", " + n.getEnd()
						+ ")\" onMouseOut=\"highlight(" + s.getId() + ")\">");
			else
				b.append("<tr><td>");
			b.append("&nbsp;");
			b.append(toHtmlID(n.getId(), s.getId()));
			b.append(":");
			b.append("<span id=\"" + s.getId() + "-" + i + "\">");
			i++;
			b.append(StringEscapeUtils.escapeHtml4(n.getPredicate()));
			if (n.getCarg() != null)
				b.append("(")
						.append(StringEscapeUtils.escapeHtml4(n.getCarg()))
						.append(")");
			if (n.getStart() != -1)
				b.append("<").append(n.getStart()).append(",")
						.append(n.getEnd()).append(">");
			b.append("</span>");
			b.append("[");
			boolean in = false;
			for (EDSRole e : n.getRoles()) {
				b.append("&nbsp;").append(e.getName()).append("&nbsp;")
						.append(toHtmlID(e.getTarget().getId(), s.getId()))
						.append(",");
				in = true;
			}
			if (in)
				b.deleteCharAt(b.length() - 1).append("&nbsp;");
			b.append("]");
			b.append("</td></tr>").append("\n");
		}

		b.append("</table>\n");
		return b.toString();
	}

	private static String toHtmlID(String id, String edsID) {
		return "<span class=\"mrsVariable" + edsID + id
				+ "\" onMouseOver=\"mrsVariableSelect('" + edsID + id
				+ "', '')\" onMouseOut=\"mrsVariableUnselect('" + edsID + id
				+ "')\">" + id + "</span>";
	}

	private static void addConstraint(StringBuilder b, QEQConstraint c) {
		b.append("<span " + addVariable(c.getArg1())
				+ "</span>&thinsp;=q&thinsp;<span " + addVariable(c.getArg2())
				+ "</span>");
	}

	private static void addIndex(StringBuilder b, Variable top) {
		b.append("<tr><td class=\"mrsFeatureIndex\">INDEX</td>");
		b.append("<td class=mrsFeatureIndex>");
		b.append("  <div " + addVariable(top) + "</div>");
		b.append("</td>");
		b.append("</tr>");
	}

	private static void addLBL(StringBuilder b, String name, Variable lbl) {
		b.append("<tr><td class=mrsLabel>" + name
				+ "</td><td class=mrsValue><div ");
		b.append(addVariable(lbl));
		b.append("</div>");
		b.append("</td>");
		b.append("</tr>");
	}

	private static String addParams(List<Property> properties) {
		if (properties.size() == 0)
			return "";
		StringBuilder b = new StringBuilder();
		b.append("<table class=mrsProperties>");
		for (Property p : properties) {
			b.append("<tr>");
			b.append("<td class=mrsPropertyFeature>").append(p.getName())
					.append("</td>");
			b.append("<td class=mrsPropertyValue>").append(p.getValue())
					.append("</td>");
			b.append("</tr>");
		}
		b.append("</table>");

		return b.toString();
	}

	private static void addPredicates(StringBuilder b, MRS e) {

		int size = e.getPredications().size();

		int rols = size / 5 + 1;

		System.out.println(size + " " + rols);

		b.append("<table class=mrsRelsContainer>");

		for (int r = 0; r < rols; r++) {

			if (5 * r >= e.getPredications().size())
				break;
			b.append("<tr>");
			b.append("<td>");
			b.append("<table class=mrsRelsContainer>");
			b.append("<tr>");

			for (int i = 0; i < 5; i++) {

				int current = 5 * r + i;

				if (current >= e.getPredications().size())
					break;

				MRSPredication p = e.getPredications().get(current);

				b.append("<td>");

				addPredication(e, p, b, current + 1);

				b.append("</td>");
			}
			b.append("</tr>");
			b.append("</table>");
			b.append("</td>");
			b.append("</tr>");
		}

		b.append("</table>");
	}

	private static void addPredicateString(StringBuilder b, MRS s,
			MRSPredication p, int i) {
		b.append("<tr>");
		b.append("<td class=mrsPredicate colspan=2>");
		b.append("<span id=\"" + s.getId() + "-" + i + "\" >");
		String predicate = p.getPredicate();
		if (predicate.endsWith("_rel"))
			predicate = predicate.substring(0, predicate.length() - 4);
		b.append(StringEscapeUtils.escapeHtml4(predicate));
		if (p.getStart() != -1) {
			b.append("&lang;");
			b.append(p.getStart() + ":" + p.getEnd());
			b.append("&rang;");
		}
		b.append("</span>");
		b.append("</td>");
		b.append("</tr>");
	}

	private static void addPredication(MRS s, MRSPredication p,
			StringBuilder b, int i) {
		b.append("<table class=\"mrsRelation\">");
		addPredicateString(b, s, p, i);
		if (p.getLbl() != null)
			addLBL(b, "LBL", p.getLbl());
		for (MRSRole x : p.getRoles())
			addLBL(b, x.getName(), x.getTarget());
		b.append("</tr>");
		b.append("</table>");

	}

	private static void addTop(StringBuilder b, Variable top) {
		b.append("<tr><td class=\"mrsFeatureTop\">TOP</td>");
		b.append("<td class=mrsValueTop>");
		b.append("  <div " + addVariable(top) + "</div>");
		b.append("</td>");
		b.append("</tr>");
	}

	private static String addVariable(Variable v) {
		String u = v.getName().toUpperCase();
		String l = v.getName();
		String params = addParams(v.getProperties());
		return "class=\"mrsVariable0" + u
				+ "\" onMouseOver=\"mrsVariableSelect('0" + u + "', '" + params
				+ "')\" onMouseOut=\"mrsVariableUnselect('0" + u
				+ "')\" style=\"color: rgb(26, 4, 165);\">" + l;
	}

	private static String toHTML(MRS e) {

		StringBuilder b = new StringBuilder();
		b.append("<table  class=\"mrsMrs\">");

		addTop(b, e.getTop());
		addIndex(b, e.getIndex());

		b.append("<tr>");
		b.append("<td class=mrsFeatureRels>RELS</td>");
		b.append("<td class=mrsValueRels>");
		b.append("<table class=mrsRelsContainer>");
		b.append("<tr>");
		b.append("<td valign=middle><span class=mrsBracket>{&nbsp;</span></td>");

		b.append("<td>");

		addPredicates(b, e);

		b.append("</td>");

		b.append("<td valign=middle><span class=mrsBracket>&nbsp;}</span></td>");
		b.append("</tr>");
		b.append("</table>");
		b.append("</td>");
		b.append("</tr>");
		b.append("<tr>");
		b.append("<td class=\"mrsFeatureHcons\">HCONS</td><td class=\"mrsValueHcons\">{&nbsp;");

		int cx = 0;
		for (Constraint c : e.getConstraints()) {
			cx++;
			addConstraint(b, (QEQConstraint) c);
			if (cx != e.getConstraints().size())
				b.append(",&nbsp");
		}

		b.append("&nbsp;}</td>");
		b.append("</tr>");
		b.append("</table>");
		b.append("<script>\n");
		for (Constraint c : e.getConstraints()) {
			QEQConstraint q = (QEQConstraint) c;
			b.append("  mrsHCONSsBackward['0"
					+ q.getArg2().getName().toUpperCase() + "'] = '0"
					+ q.getArg1().getName().toUpperCase() + "';\n");
		}
		b.append("</script>\n");
		return b.toString();
	}

}
