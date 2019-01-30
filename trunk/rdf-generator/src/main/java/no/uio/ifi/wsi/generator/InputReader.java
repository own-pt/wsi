package no.uio.ifi.wsi.generator;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;
import no.uio.ifi.wsi.SemanticStructure;

public class InputReader implements Iterator<SemanticStructure> {

	@Getter
	@Setter
	private boolean printQuads = false;

	private Queue<SemanticStructure> structures;

	private Queue<LocalFile> files;

	private CommandLineReader reader;

	public InputReader(CommandLineReader reader_) {
		reader = reader_;
		files = new ArrayDeque<LocalFile>(reader.getFiles());
		structures = new ArrayDeque<SemanticStructure>();
	}

	@Override
	public boolean hasNext() {
		if (structures.size() != 0)
			return true;
		readFile();
		return structures.size() != 0;
	}

	private void readFile() {
		printQuads = true;
		if (files.size() == 0)
			return;
		LocalFile f = files.poll();
		System.out.print("Loading file: " + f.getFile().getAbsolutePath());
		System.out.println(" ( " + (reader.getAllFiles() - files.size())
				+ " of " + reader.getAllFiles() + ")");
		structures.addAll(reader.getStructureReader().read(f));
		if (structures.size() == 0)
			readFile();

		System.out.println("Structures read: " + structures.size());

		if (structures.size() == 0)
			readFile();
	}

	@Override
	public SemanticStructure next() {
		return structures.poll();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
