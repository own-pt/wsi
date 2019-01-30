package no.uio.ifi.wsi.search.language.impl.generic;

import java.util.List;

import lombok.Value;

@Value
public class GenericConfig {

	private List<GenericPattern> patterns;
	private String namespace;
	private String prefix;
	private String roleName;
	private int roleLevel;
	private String textName;
	private int textLevel;

}
