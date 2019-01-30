package no.uio.ifi.wsi.generator.structures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.sdp.SDPGraph;

public class SDPStructureReader extends GenericStructureReader {

	private boolean hasSense;

	public SDPStructureReader(String filter, String svgDirectory,
			boolean hasSense_, String outputDirectory) {
		super(filter, svgDirectory);
		hasSense = hasSense_;

		if (hasSense) {
			File f = new File(outputDirectory + "/sense.txt");
			try {
				PrintStream ps = new PrintStream(f);
				ps.print(true);
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public SemanticStructure instance() {
		return new SDPGraph(hasSense);
	}

}
