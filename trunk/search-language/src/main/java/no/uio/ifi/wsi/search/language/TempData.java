package no.uio.ifi.wsi.search.language;

import java.util.Set;

import lombok.Data;

import com.google.common.collect.Sets;

@Data
public class TempData {

	private int count;
	private Set<String> variables;

	public static TempData newInstance() {
		TempData t = new TempData();
		t.setCount(100);
		t.setVariables(Sets.<String> newHashSet());
		return t;
	}
}
