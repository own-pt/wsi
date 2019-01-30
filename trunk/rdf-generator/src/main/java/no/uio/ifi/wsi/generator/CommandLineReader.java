package no.uio.ifi.wsi.generator;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import lombok.Getter;
import no.uio.ifi.wsi.generator.rdf.CONLLRDFGenerator;
import no.uio.ifi.wsi.generator.rdf.EDSRDFGenerator;
import no.uio.ifi.wsi.generator.rdf.MRSRDFGenerator;
import no.uio.ifi.wsi.generator.rdf.RDFGenerator;
import no.uio.ifi.wsi.generator.rdf.SDPRDFGenerator;
import no.uio.ifi.wsi.generator.structures.CONLLStructureReader;
import no.uio.ifi.wsi.generator.structures.DeepBankStructureReader;
import no.uio.ifi.wsi.generator.structures.SDPStructureReader;
import no.uio.ifi.wsi.generator.structures.StructureReader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.google.common.collect.Lists;

public class CommandLineReader {

	public static String FORMAT = "format";
	public static String OUTPUT = "output";
	public static String FILTER = "filter";
	public static String HAS_SENSE = "has-sense";
	public static String NO_SVG = "no-svg";
	public static String MRS = "mrs";
	public static String HELP = "help";
	public static String EDS = "eds";
	public static String PAS = "pas";
	public static String DM = "dm";
	public static String PSD = "psd";
	public static String CONLL = "conll";

	@Getter
	private StructureReader structureReader;
	@Getter
	private RDFGenerator rdfGenerator;

	private String outputDirectory;
	@Getter
	private String countDirectory;
	@Getter
	private String rdfDirectory;
	@Getter
	private String indexDirectory;
	@Getter
	private String svgDirectory;
	@Getter
	private String dotDirectory;
	@Getter
	private String tdbDirectory;
	@Getter
	private String format;
	@Getter
	private List<LocalFile> files;
	@Getter
	private int allFiles;

	@Getter
	private boolean noSVG;

	public static void print(Options opts) {
		StringWriter result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		HelpFormatter usageFormatter = new HelpFormatter();
		usageFormatter.printHelp(printWriter, 80, "create-index",
				"Create index directory for WeSearch infrastructure", opts, 2,
				4, " ", true);
		printWriter.println("");
		printWriter.close();
		System.out.println(result.toString());
		System.exit(0);
	}

	public CommandLineReader(String args[]) {

		if (args.length == 0) {
			print(options(true));
			System.exit(0);
		}

		CommandLine commandLine = null;
		try {
			commandLine = new BasicParser().parse(options(false), args);
		} catch (Exception e) {
			e.printStackTrace();
			print(options(true));
			System.exit(1);
			return;
		}
		if (commandLine.hasOption(HELP)) {
			print(options(true));
			System.exit(1);
			return;
		}
		try {
			commandLine = new BasicParser().parse(options(true), args);
		} catch (Exception e) {
			e.printStackTrace();
			print(options(true));
			System.exit(0);
			return;
		}

		outputDirectory = commandLine.getOptionValue(OUTPUT);
		format = commandLine.getOptionValue(FORMAT);
		noSVG = commandLine.hasOption(NO_SVG);

		init();
		files = Lists.newArrayList();
		for (String file : commandLine.getArgs())
			allFiles(file);

		allFiles = files.size();

		String filter = commandLine.getOptionValue(FILTER);

		if (format.equals(EDS)) {
			noSVG = true;
			structureReader = new DeepBankStructureReader(true, filter);
			rdfGenerator = new EDSRDFGenerator();
			return;
		}
		if (format.equals(MRS)) {
			noSVG = true;
			structureReader = new DeepBankStructureReader(false, filter);
			rdfGenerator = new MRSRDFGenerator();
			return;
		}
		if (format.equals(CONLL)) {
			structureReader = new CONLLStructureReader(filter, svgDirectory);
			rdfGenerator = new CONLLRDFGenerator();
			return;
		}

		boolean hasSense = commandLine.hasOption(HAS_SENSE);
		structureReader = new SDPStructureReader(filter, noSVG ? null
				: svgDirectory, hasSense, outputDirectory);
		rdfGenerator = new SDPRDFGenerator(format);

	}

	public void init() {
		new File(outputDirectory).mkdir();
		outputDirectory = outputDirectory + "/" + format;
		new File(outputDirectory).mkdir();
		indexDirectory = outputDirectory + "/index/";
		new File(indexDirectory).mkdir();
		countDirectory = outputDirectory + "/count/";
		new File(countDirectory).mkdir();
		tdbDirectory = outputDirectory + "/tdb/";
		new File(tdbDirectory).mkdir();
		rdfDirectory = outputDirectory + "/rdf/";
		new File(rdfDirectory).mkdir();
		svgDirectory = outputDirectory + "/svg/";
		new File(svgDirectory).mkdir();
		dotDirectory = outputDirectory + "/dot/";
		new File(dotDirectory).mkdir();
	}

	private void allFiles(String file) {
		allFiles(file, new File(file).getAbsolutePath().length());
	}

	private void allFiles(String file, int root) {
		File f = new File(file);
		if (!valid(f))
			return;
		if (!f.isDirectory()) {
			files.add(new LocalFile(f, f.getAbsolutePath().substring(root)
					.replace("/", "-").replace("\\", "-")));
			return;
		}
		for (File ff : f.listFiles())
			allFiles(ff.getAbsolutePath(), root);
	}

	private static boolean valid(File f) {
		if (f.getName().startsWith(".") || f.getName().startsWith("~")
				|| f.getName().startsWith("#") || f.getName().endsWith("#")) {
			System.out.println(f + " Skipped");
			return false;
		}
		return true;
	}

	public static Options options(boolean required) {
		Options ops = new Options();
		Option o = null;
		o = new Option("h", HELP, false, "Display help");
		o.setRequired(false);
		ops.addOption(o);
		o = new Option("f", FORMAT, true,
				"Format of the input file. Possible values: conll, eds, mrs, dm, pas & psd");
		o.setRequired(required);
		ops.addOption(o);
		o = new Option("o", OUTPUT, true, "The location of the generated index");
		o.setRequired(required);
		ops.addOption(o);
		o = new Option("i", FILTER, true,
				"Filter graphs with id higher than stated");
		o.setRequired(false);
		ops.addOption(o);
		o = new Option("s", HAS_SENSE, false, "SDP input file has sense column");
		o.setRequired(false);
		ops.addOption(o);
		o = new Option("n", NO_SVG, false, "Do not generate svg");
		o.setRequired(false);
		ops.addOption(o);
		return ops;
	}
}
