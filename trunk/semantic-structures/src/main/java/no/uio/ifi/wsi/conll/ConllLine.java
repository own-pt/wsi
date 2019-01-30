package no.uio.ifi.wsi.conll;

import lombok.Value;

@Value
public class ConllLine {

	private String id;
	private String form;
	private String lemma;
	private String pos;
	private String cpos;
	private String morpho;
	private String parent;
	private String deprel;

}
