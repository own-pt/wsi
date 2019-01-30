package no.uio.ifi.wsi.search.language;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import no.uio.ifi.wsi.mrs.Property;

@Data
public class RoleParse {

	private final String name;

	private String to;

	private List<Property> params;

	public RoleParse(String s) {
		s = s.trim();
		params = new ArrayList<Property>();
		if (s.isEmpty()) {
			throw new RuntimeException("Role is empty");
		}
		if (s.endsWith("}")) {
			String substring = s.substring(s.lastIndexOf("{") + 1,
					s.lastIndexOf("}")).trim();
			s = s.substring(0, s.lastIndexOf("{")).trim();

			for (String xx : substring.split(",")) {
				xx = xx.trim();
				int pos = xx.indexOf(" ");
				Property p = new Property();
				if (pos == -1) {
					p.setName(xx);

				} else {
					p.setName(xx.substring(0, pos));
					p.setValue(xx.substring(pos + 1));
				}
				params.add(p);
			}
		}

		int i = 1;
		for (; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == ' ' && s.charAt(i) != '\\') {
				break;
			}
		}
		if (i == s.length()) {
			name = s;
			to = null;
		} else {
			name = s.substring(0, i);
			to = s.substring(i + 1);
		}
	}
}
