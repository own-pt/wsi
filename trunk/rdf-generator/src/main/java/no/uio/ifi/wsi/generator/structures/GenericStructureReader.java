package no.uio.ifi.wsi.generator.structures;

import java.io.File;
import java.util.List;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.generator.LocalFile;
import no.uio.ifi.wsi.generator.StructureIndexGenerator;

import com.google.common.collect.Lists;

public abstract class GenericStructureReader extends StructureReader {

	private String svgDirectory;
	private int filterUp;

	public abstract SemanticStructure instance();

	public GenericStructureReader(String filter, String svgDirectory_) {
		super();
		filterUp = -1;
		if (filter != null)
			filterUp = Integer.parseInt(filter);
		svgDirectory = svgDirectory_;
		if (svgDirectory == null)
			return;
	}

	@Override
	public List<SemanticStructure> read(List<String> lines, LocalFile file) {
		int id = 0;
		List<SemanticStructure> structures = Lists.newArrayList();
		List<String> currentLine = Lists.newArrayList();
		for (String line : lines) {
			line = line.trim();
			if (line.length() == 0) {
				if (currentLine.size() == 0)
					continue;
				SemanticStructure s = instance();
				try {
					s.load(currentLine);
					addSVG(s);
					if (s.getId() == null)
						s.setId(file.getLocalName() + id++);
					if (!filter(s))
						structures.add(s);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				currentLine.clear();
				continue;
			}
			currentLine.add(line);
		}
		if (currentLine.size() > 0) {
			SemanticStructure s = instance();
			try {
				s.load(currentLine);
				addSVG(s);
				if (s.getId() == null)
					s.setId(file.getLocalName() + id++);
				if (!filter(s))
					structures.add(s);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
		return structures;
	}

	private void addSVG(SemanticStructure s) {
		if (svgDirectory == null)
			return;
		String fileName = svgDirectory + s.getId();

		if (new File(fileName).exists()) {
			String data = StructureIndexGenerator.loadString(fileName, "UTF-8");
			s.setSvg(data);
		}
	}

	private boolean filter(SemanticStructure sx) {
		if (filterUp == -1)
			return false;
		try {
			return filterUp < Integer.parseInt(sx.getId());
		} catch (Exception e) {
		}
		return false;
	}
}
