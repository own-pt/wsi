package no.uio.ifi.wsi.generator;

import java.io.File;
import java.util.Arrays;

import no.uio.ifi.wsi.generator.index.CountIndexGenerator;

public class CreateIndex {

	public static void main(String[] args) {

		try {
			CommandLineReader cmlReader = new CommandLineReader(args);

			if (!cmlReader.isNoSVG()) {
				SVGGenerator.runSVG(cmlReader);
				runProcess(new String[] { "./run-graphviz",
						cmlReader.getDotDirectory(),
						cmlReader.getSvgDirectory() });
			}
			InputReader reader = new InputReader(cmlReader);
			StructureIndexGenerator.generateIndex(reader, cmlReader);

			CountIndexGenerator generator = new CountIndexGenerator(
					cmlReader.getCountDirectory());
			generator.index(cmlReader.getRdfDirectory());
			generator.writeCache();
			runProcess(new String[] { "apache-jena/bin/tdbloader2", "--loc",
					cmlReader.getTdbDirectory() + "/1",
					cmlReader.getRdfDirectory() + "/*" });

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void runProcess(String[] command) throws Exception {
		System.out.println(Arrays.toString(command));
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command, null, new File("../.."));
		ProcessStreamReader errorGobbler = new ProcessStreamReader(
				proc.getErrorStream());
		ProcessStreamReader outputGobbler = new ProcessStreamReader(
				proc.getInputStream());
		errorGobbler.start();
		outputGobbler.start();
		proc.waitFor();
	}

}
