package no.uio.ifi.wsi.sparql;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class ResultInstance {

	private final List<String> names;
	private final List<String> values;

	public ResultInstance() {
		names = Lists.newArrayList();
		values = Lists.newArrayList();
	}

	public void addValue(String name, String value) {
		values.add(value);
		names.add(name);
	}

	public String value(String name) {
		return values.get(names.indexOf(name));
	}
}
