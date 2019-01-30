package no.uio.ifi.wsi.mrs;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MRSParser {

	private static void extractCons(MRS mrs, List<String> lines,
			Map<String, Variable> map) {
		String line = lines.get(lines.size() - 1);

		line = line.substring(line.indexOf("<") + 1, line.lastIndexOf(">"))
				.trim();

		String[] s = line.split(" ");

		for (int i = 0; i < s.length;) {
			Variable v1 = variable(s[i], map);
			i++;
			i++; // skip rel;
			Variable v2 = variable(s[i], map);
			i++;
			Constraint c = new QEQConstraint(v1, v2);
			mrs.getConstraints().add(c);
		}

	}

	private static void extractIndex(MRS mrs, List<String> lines,
			Map<String, Variable> map) {
		String line = lines.get(1).substring(lines.get(1).indexOf(":") + 1)
				.trim();

		List<Property> props = null;
		if (line.contains("[")) {
			String propsString = line.substring(line.indexOf("[") + 1,
					line.indexOf("]", line.indexOf("["))).trim();
			line = line.substring(0, line.indexOf("[")).trim();
			props = extractProps(propsString);

		}

		line = line.trim();

		String var = line;

		Variable v = variable(var, map);
		mrs.setIndex(v);
		if (props != null)
			v.getProperties().addAll(props);
	}

	private static List<String> extractLines(List<String> lines) {
		List<String> out = Lists.newArrayList();
		int start = -1;
		for (int i = 0; i < lines.size(); i++) {
			String s = lines.get(i);
			if (s.startsWith(" [ LTOP:")) {
				start = i;
				break;
			}
		}
		if (start == -1) {
			System.out.println(lines);
			System.exit(0);
			return null;
		}
		for (int i = start; i < lines.size(); i++) {
			String s = lines.get(i);
			out.add(s);
			if (s.trim().startsWith("HCONS:")) {
				break;
			}
		}
		return out;
	}

	private static List<Property> extractProps(String props) {
		List<Property> out = Lists.newArrayList();
		String[] ps = props.split(" ");
		for (int i = 1; i < ps.length;) {
			Property p = new Property();
			String name = ps[i];
			name = name.substring(0, name.length() - 1);
			p.setName(name);
			i++;
			String vc = ps[i].trim();
			if (vc.startsWith("\"") && vc.endsWith("\"") && vc.length() > 2)
				vc = vc.substring(1, vc.length() - 1);
			p.setValue(vc);
			i++;
			out.add(p);
		}
		return out;
	}

	private static void extractTextLine(List<String> lines, MRS mrs) {
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
		mrs.setId(id);
		mrs.setText(text);
	}

	private static void extractTop(MRS mrs, List<String> lines,
			Map<String, Variable> map) {
		String top = lines.get(0).trim();
		top = top.substring(top.lastIndexOf(" ") + 1);
		mrs.setTop(variable(top, map));

	}

	public static void readLines(List<String> lines, MRS mrs) {
		Map<String, Variable> map = Maps.newHashMap();
		extractTop(mrs, lines, map);
		extractIndex(mrs, lines, map);
		extractCons(mrs, lines, map);
		MRSPredication p = null;

		int pid = 1;

		for (int i = 3; i < lines.size() - 1; i++) {

			String line = lines.get(i).trim();

			String next = lines.get(i + 1).trim();

			if (i == lines.size() - 2)
				line = line.substring(0, line.length() - 3).trim();
			else {
				if (next.startsWith("["))
					line = line.substring(0, line.length() - 1).trim();
			}

			if (line.startsWith("[")) {
				if (p != null)
					mrs.getPredications().add(p);

				line = line.substring(1).trim();

				p = new MRSPredication();
				p.setId("pdc" + pid);
				pid++;

				String label = line;
				if (line.contains("<") && line.contains(":")
						&& line.contains(">")) {
					int start = Integer.parseInt(line.substring(
							line.lastIndexOf("<") + 1, line.lastIndexOf(":")));
					int end = Integer.parseInt(line.substring(
							line.lastIndexOf(":") + 1, line.lastIndexOf(">")));
					p.setStart(start);
					p.setEnd(end);
					label = line.substring(0, line.lastIndexOf("<"));
				}
				if (label.startsWith("\"") && label.endsWith("\"")
						&& label.length() > 2)
					label = label.substring(1, label.length() - 1);
				p.setPredicate(label);
				continue;
			}

			if (line.length() == 0)
				continue;

			String roleName = line.substring(0, line.indexOf(":"));

			if (roleName.equals("CARG")) {
				String value = line.substring(line.indexOf(":") + 1).trim();

				if (value.startsWith("(") && value.endsWith(")"))
					value = value.substring(1, value.length() - 1);

				if (value.startsWith("\"") && value.endsWith("\"")
						&& value.length() > 2)
					value = value.substring(1, value.length() - 1);
				p.setCarg(value);
				continue;
			}

			List<Property> props = null;
			if (line.contains("[")) {
				String propsString = line.substring(line.indexOf("[") + 1,
						line.indexOf("]", line.indexOf("["))).trim();
				line = line.substring(0, line.indexOf("[")).trim();
				props = extractProps(propsString);

			}
			String var = line.substring(line.indexOf(":") + 1).trim();
			Variable v = variable(var, map);
			if (props != null)
				v.getProperties().addAll(props);

			if (roleName.equals("LBL")) {
				p.setLbl(v);
				continue;
			}
			MRSRole r = new MRSRole();
			r.setName(roleName);
			r.setTarget(v);
			p.getRoles().add(r);

		}
		if (p != null)
			mrs.getPredications().add(p);
	}

	private static String stored(List<String> lines) {
		StringBuilder b = new StringBuilder();
		for (String l : lines) {
			b.append(l + "\n");
		}
		return b.toString();
	}

	public static MRS read(List<String> lines) {
		MRS s = new MRS();
		read(lines, s);
		return s;
	}

	public static void read(List<String> lines, MRS s) {
		extractTextLine(lines, s);
		lines = extractLines(lines);
		s.setStructure(stored(lines));
		readLines(lines, s);
	}

	private static Variable variable(String var, Map<String, Variable> map) {
		Variable v = null;
		if (map.containsKey(var))
			v = map.get(var);
		else {
			v = new Variable(var);
			map.put(var, v);
		}
		return v;
	}

}
