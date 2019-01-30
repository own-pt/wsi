package no.uio.ifi.wsi.search.language.impl.generic;

import java.util.List;

import lombok.Value;

@Value
public class GenericDescription {

	private String textName;
	private String textWeight;
	private List<GenericPattern> patterns;

}
