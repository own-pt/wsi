package no.uio.ifi.wsi.search.language.impl.generic;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.uio.ifi.wsi.search.language.Parse;
import no.uio.ifi.wsi.search.language.RoleParse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Data
@EqualsAndHashCode(callSuper = false)
public class GenericNodeParse extends Parse {

	private String id;

	private String nonPatternText;

	private Map<String, String> values;

	private List<RoleParse> roles;

	public GenericNodeParse() {
		super();
		values = Maps.newHashMap();
		roles = Lists.newArrayList();
	}

	public static void main(String[] args) {
		List<GenericPattern> ps = GenericDefaults.sdp();
		Map<Character, GenericPattern> patterns = Maps.newHashMap();
		for (GenericPattern gp : ps)
			patterns.put(gp.getPatternChar(), gp);

		System.out.println(new GenericNodeParse("e:/v*[ARG1 x]", patterns));

	}

	public GenericNodeParse(String text, Map<Character, GenericPattern> patterns) {
		this();
		consumeText(text, patterns);
	}

	public void parseRoles(String substring) {
		if (substring.length() == 0) {
			return;
		}
		int start = 0;
		int brak = 0;
		for (int i = 1; i < substring.length(); i++) {
			char ch = substring.charAt(i);
			if (ch == '{')
				brak++;
			if (ch == '}')
				brak--;
			if (brak < 0)
				throw new RuntimeException("Wrong brakets in " + substring);
			if (ch == ',' && brak == 0 && substring.charAt(i) != '\\') {
				String role = substring.substring(start, i).toLowerCase();
				roles.add(new RoleParse(role));
				start = i + 1;
			}
		}
		if (brak != 0)
			throw new RuntimeException("Wrong brakets in " + substring);
		String role = substring.substring(start);
		roles.add(new RoleParse(role));
	}

	public void consumeText(String text, Map<Character, GenericPattern> patterns) {

		if (text.length() > 3 && text.endsWith("]"))
			text = trimRoles(text);

		if (text.length() == 0)
			return;

		StringBuilder b = new StringBuilder();

		GenericPattern found = null;
		for (int i = 0; i < text.length();) {
			char ch = text.charAt(i);
			if (ch == GenericDefaults.ID_CHAR && notQuoted(text, i)) {
				if (values.size() > 0 || found != null)
					throw new RuntimeException("Wrong position of ID char '"
							+ GenericDefaults.ID_CHAR + "' in string\"" + text
							+ "\"");
				id = b.toString();
				b = new StringBuilder();
				i++;
				continue;
			}
			if (patterns.containsKey(ch) && notQuoted(text, i)) {
				GenericPattern z = patterns.get(ch);
				if (values.containsKey(z.getName())
						|| (found != null && found.getName()
								.equals(z.getName())))
					throw new RuntimeException("Duplicate char '" + ch
							+ "' in string\"" + text + "\"");
				if (z.isBoolean()
						|| z.getEndChar() != GenericDefaults.NULL_CHAR) {
					if (b.toString().trim().length() > 0) {
						if (found == null)
							nonPatternText = b.toString().trim();
						else
							values.put(found.getName(), b.toString().trim());
					}
					found = null;

					if (z.isBoolean()) {
						values.put(z.getName(), "true");
						i++;
					} else {
						int k = i + 1;
						for (; k < text.length(); k++) {
							if (text.charAt(k) == z.getEndChar()
									&& notQuoted(text, k))
								break;
						}
						if (k == text.length())
							throw new RuntimeException(z.getName()
									+ " is not closed in string\"" + text
									+ "\"");
						if (i + 1 - k == 0)
							throw new RuntimeException(z.getName()
									+ " is empty in string\"" + text + "\"");

						values.put(z.getName(), text.substring(i + 1, k));
						i = k + 1;
					}
					continue;
				}
				if (found == null) {
					if (b.toString().trim().length() != 0)
						nonPatternText = b.toString().trim();
				} else {
					if (b.toString().trim().length() == 0)
						throw new RuntimeException(z.getName()
								+ " is empty in string\"" + text + "\"");
					values.put(found.getName(), b.toString());
				}
				found = z;
				b = new StringBuilder();
				i++;
				continue;
			}
			b.append(ch);
			i++;
		}
		if (b.toString().trim().length() > 0) {
			if (found == null)
				nonPatternText = b.toString().trim();
			else
				values.put(found.getName(), b.toString().trim());
		}
	}

	private boolean notQuoted(String text, int i) {
		return i == 0 || text.charAt(i - 1) != '\\';
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	private String trimRoles(String text) {
		int end = text.length() - 2;
		for (; end >= 0; end--) {
			if (text.charAt(end) == '['
					&& (end == 0 || (text.charAt(end - 1) != '\\'))) {
				break;
			}
		}
		if (end == -1) {
			throw new RuntimeException("Invalid expression " + text);
		}
		parseRoles(text.substring(end + 1, text.length() - 1).trim());
		return text.substring(0, end);
	}
}
