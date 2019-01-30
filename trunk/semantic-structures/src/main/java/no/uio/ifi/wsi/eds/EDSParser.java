package no.uio.ifi.wsi.eds;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EDSParser {

	private static List<String> extractLines(List<String> lines) {
		List<String> out = Lists.newArrayList();
		int start = -1;
		for (int i = 0; i < lines.size(); i++) {
			String s = lines.get(i);
			if (s.startsWith("{") && lines.get(i + 1).endsWith("]")) {
				start = i;
				break;
			}
		}
		if (start == -1) {
			return null;
		}
		for (int i = start; i < lines.size(); i++) {
			String s = lines.get(i);
			out.add(s);
			if (s.startsWith("}")) {
				break;
			}
		}

		return out;
	}

	private static void extractTextLine(List<String> lines, EDS eds) {
		String l = null;
		for (String s : lines) {
			if (s.startsWith(";;")) {
				continue;
			}
			if (s.trim().length() == 0) {
				continue;
			}
			if (!s.startsWith("[") || !s.endsWith("'") || !s.contains("`")) {
				continue;
			}
			l = s.trim();
			break;
		}
		if (l == null)
			l = lines.get(0);
		String text = l.substring(l.indexOf("`") + 1, l.length() - 1).trim();
		String id = l.substring(1, l.indexOf("]"));
		eds.setId(id);
		eds.setText(text);
	}

	private static EDSPredication get(String id, EDS s,
			Map<String, EDSPredication> predications) {
		EDSPredication n = null;
		if (predications.containsKey(id))
			n = predications.get(id);
		else {
			n = new EDSPredication();
			n.setId(id);
			predications.put(id, n);
			if (!id.startsWith("_")) {
				n.setType("" + id.charAt(0));
			}
			s.getPredications().add(n);
		}
		return n;
	}

	private static void processLine(String line, EDS s,
			Map<String, EDSPredication> predications) {
		line = line.trim();
		int idpos = line.indexOf(":");
		String id = null;
		if (idpos == -1) {
			id = "" + (100 + s.getPredications().size());
		} else {
			id = line.substring(0, idpos);
			if (id.startsWith("|")) {
				id = id.substring(1);
			}
			line = line.substring(line.indexOf(":") + 1);

		}
		EDSPredication n = get(id, s, predications);
		int braks = line.lastIndexOf("[");

		if (braks != -1) {
			String rels = line.substring(braks + 1, line.length() - 1).trim();

			if (rels.length() > 0) {
				String[] split = rels.split(",");
				for (String sx : split) {
					sx = sx.trim();

					String toID = sx.substring(sx.indexOf(" ") + 1);
					if (toID.startsWith("|")) {
						toID = toID.substring(1);
					}
					EDSRole e = new EDSRole();
					e.setName(sx.substring(0, sx.indexOf(" ")));
					EDSPredication nto = get(toID, s, predications);
					e.setTarget(nto);
					n.getRoles().add(e);
				}
			}

			line = line.substring(0, braks);
		}

		if (line.endsWith(">")) {
			String span = line.substring(line.lastIndexOf("<") + 1,
					line.length() - 1);
			int separtator = span.indexOf(":");
			if (separtator == -1) {
				separtator = span.indexOf(",");
			}
			if (separtator != -1) {
				try {
					int start = Integer.parseInt(span.substring(0, separtator));
					int end = Integer.parseInt(span.substring(separtator + 1,
							span.length()));
					n.setStart(start);
					n.setEnd(end);
				} catch (NumberFormatException e) {
					java.util.logging.Logger.getLogger(
							EDSParser.class.getName()).log(Level.SEVERE, null,
							e);
				}
			}
			line = line.substring(0, line.lastIndexOf("<"));
		}

		if (line.endsWith(")")) {
			String name = line.substring(line.indexOf("(") + 1,
					line.length() - 1);
			if (name.length() > 1 && name.startsWith("\"")
					&& name.endsWith("\"")) {
				name = name.substring(1, name.length() - 1);
			}
			n.setCarg(name);
			line = line.substring(0, line.indexOf("("));
		}

		if (line.endsWith(">")) {
			String span = line.substring(line.lastIndexOf("<") + 1,
					line.length() - 1);
			int separtator = span.indexOf(":");
			if (separtator == -1) {
				separtator = span.indexOf(",");
			}
			if (separtator != -1) {
				try {
					int start = Integer.parseInt(span.substring(0, separtator));
					int end = Integer.parseInt(span.substring(separtator + 1,
							span.length()));
					n.setStart(start);
					n.setEnd(end);
				} catch (NumberFormatException e) {
					java.util.logging.Logger.getLogger(
							EDSParser.class.getName()).log(Level.SEVERE, null,
							e);
				}
			}
			line = line.substring(0, line.lastIndexOf("<"));
		}

		n.setPredicate(line);

	}

	public static void readLines(List<String> lines, EDS s) {
		String top = lines.get(0).trim();
		top = top.substring(1, top.length() - 1);
		if (top.contains("("))
			top = top.substring(0, top.indexOf("(")).trim();
		s.setTop(top);
		lines = lines.subList(1, lines.size() - 1);
		Map<String, EDSPredication> predications = Maps.newHashMap();
		for (String line : lines)
			processLine(line, s, predications);

	}

	private static String stored(List<String> lines) {
		StringBuilder b = new StringBuilder();
		for (String l : lines) {
			b.append(l + "\n");
		}
		return b.toString();
	}

	public static EDS read(List<String> lines) {
		EDS s = new EDS();
		read(lines, s);
		return s;

	}

	public static void read(List<String> lines, EDS s) {
		extractTextLine(lines, s);
		lines = extractLines(lines);
		s.setStructure(stored(lines));
		readLines(lines, s);
	}

}
