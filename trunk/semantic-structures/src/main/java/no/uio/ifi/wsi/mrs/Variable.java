package no.uio.ifi.wsi.mrs;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class Variable {

	private final String name;

	private final List<Property> properties;

	private final String type;

	public Variable(String name_) {
		this(name_, name_.substring(0, 1));
	}

	public Variable(String name_, String type_) {
		name = name_;
		type = type_;
		properties = Lists.newArrayList();
	}

}
