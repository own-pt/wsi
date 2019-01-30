package no.uio.ifi.wsi.generator.structures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import lombok.Getter;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.generator.LocalFile;

import com.google.common.collect.Lists;

public abstract class StructureReader {
	@Getter
	private int failedToRead;

	@Getter
	private List<String> failedFiles;

	public StructureReader() {
		failedToRead = 0;
		failedFiles = Lists.newArrayList();
	}

	public List<SemanticStructure> read(LocalFile file) {
		List<String> lines = null;
		try {
			lines = readFile(file.getFile());
			if (lines.size() == 0)
				throw new RuntimeException("File is empty");
		} catch (Exception e) {
			e.printStackTrace();
			failedFiles.add(file.getFile().getAbsolutePath());
			return Lists.newArrayList();
		}
		return read(lines, file);
	}

	public abstract List<SemanticStructure> read(List<String> lines,
			LocalFile file);

	public void fail() {
		failedToRead++;
	}

	private static List<String> readFile(File f) throws Exception {
		List<String> out = Lists.newArrayList();
		InputStream input = null;
		if (f.getName().endsWith(".gz"))
			input = new GZIPInputStream(new FileInputStream(f));
		else
			input = new FileInputStream(f);
		String content;
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		while ((content = in.readLine()) != null)
			out.add(content);
		in.close();
		input.close();
		return out;
	}
}
