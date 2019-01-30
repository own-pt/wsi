package no.uio.ifi.wsi;

import java.util.List;

import lombok.Data;

@Data
public abstract class SemanticStructure {
	private String id;
	private String structure;
	private String text;
	private String svg;

	public abstract int getNodesNumber();

	public abstract void load();

	public abstract void load(List<String> lines);

	public SemanticStructure() {

	}

	public SemanticStructure(SemanticStructure sx) {
		id = sx.getId();
		structure = sx.getStructure();
		text = sx.getText();
		svg = sx.getSvg();
		load();
	}

	public SemanticStructure newInstance() throws InstantiationException,
			IllegalAccessException {
		return this.getClass().newInstance();
	}
}
