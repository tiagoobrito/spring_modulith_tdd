package tooling;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SurefireTimeSum {

	private static final Pattern TIME_ELAPSED_LINE =
			// Matches: "Time elapsed: 1.234 s - in com.example.MyTest"
			// Handles normal dash '-' and en dash '–'
			Pattern.compile("\\bTime\\s+elapsed:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*s\\b");

	public static void main(String[] args) {
		if (args.length != 1 || args[0].isBlank()) {
			System.err.println("Usage: java -jar surefire-time-sum.jar <path-to-surefire-reports>");
			System.exit(2);
		}

		Path dir = Paths.get(args[0]).toAbsolutePath().normalize();
		if (!Files.isDirectory(dir)) {
			System.err.println("Not a directory: " + dir);
			System.exit(2);
		}

		try {
			DocumentBuilderFactory dbf = secureDocumentBuilderFactory();
			DocumentBuilder db = dbf.newDocumentBuilder();

			AtomicInteger xmlFilesParsed = new AtomicInteger(0);
			AtomicInteger txtFilesParsed = new AtomicInteger(0);

			BigDecimal xmlTotalSeconds = sumXmlTestcaseTimes(db, dir, xmlFilesParsed);
			BigDecimal txtTotalSeconds = sumTxtElapsedTimes(dir, txtFilesParsed);

			System.out.println("Scanned directory: " + dir);
			System.out.println("XML  files parsed : " + xmlFilesParsed.get());
			System.out.println("TXT  files parsed : " + txtFilesParsed.get());
			System.out.println();

			System.out.println("== Totals ==");
			System.out.println("XML testcase sum : " + formatSeconds(xmlTotalSeconds) + " s  ("
					+ formatHms(xmlTotalSeconds) + ")");
			System.out.println("TXT elapsed sum  : " + formatSeconds(txtTotalSeconds) + " s  ("
					+ formatHms(txtTotalSeconds) + ")");

			if (xmlTotalSeconds != null && txtTotalSeconds != null) {
				BigDecimal delta = txtTotalSeconds.subtract(xmlTotalSeconds);
				BigDecimal pct = safePercent(delta, xmlTotalSeconds);
				System.out.println();
				System.out
					.println("Difference (TXT - XML): " + formatSeconds(delta) + " s  (" + formatHms(delta) + ")");
				System.out.println("Overhead vs XML       : " + pct.stripTrailingZeros().toPlainString() + " %");
			}
		}
		catch (Exception e) {
			System.err.println("Error while processing: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/** Sum @time from all <testcase ...> elements across *.xml files. */
	private static BigDecimal sumXmlTestcaseTimes(DocumentBuilder db, Path dir, AtomicInteger filesParsed)
			throws IOException {
		BigDecimal total = BigDecimal.ZERO;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.xml")) {
			for (Path xml : stream) {
				BigDecimal fileSeconds = sumTestcaseTimesInSingleXml(db, xml);
				if (fileSeconds != null) {
					total = total.add(fileSeconds);
					filesParsed.incrementAndGet();
				}
			}
		}
		return total;
	}

	/** For one XML file, sum all testcase time="...". */
	private static BigDecimal sumTestcaseTimesInSingleXml(DocumentBuilder db, Path xml) {
		try {
			Document doc = db.parse(xml.toFile());
			doc.getDocumentElement().normalize();

			NodeList testcases = doc.getElementsByTagName("testcase");
			BigDecimal sum = BigDecimal.ZERO;

			for (int i = 0; i < testcases.getLength(); i++) {
				NamedNodeMap attrs = testcases.item(i).getAttributes();
				if (attrs == null)
					continue;

				String timeStr = attrs.getNamedItem("time") != null ? attrs.getNamedItem("time").getNodeValue() : null;
				if (timeStr == null || timeStr.isBlank())
					continue;

				try {
					BigDecimal t = new BigDecimal(timeStr.trim());
					sum = sum.add(t);
				}
				catch (NumberFormatException ignored) {
					// skip malformed time
				}
			}
			return sum;
		}
		catch (IOException ioe) {
			System.err.println("I/O error reading " + xml + ": " + ioe.getMessage());
			return null;
		}
		catch (Exception ex) {
			System.err.println("Failed to parse " + xml + ": " + ex.getMessage());
			return null;
		}
	}

	/** Sum "Time elapsed: X s" across *.txt files, skipping known summary files. */
	private static BigDecimal sumTxtElapsedTimes(Path dir, AtomicInteger filesParsed) throws IOException {
		BigDecimal total = BigDecimal.ZERO;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
			for (Path txt : stream) {
				String fn = txt.getFileName().toString();
				// Skip aggregate / summary files that don't represent a single test class
				// timing
				if (isSummaryTxt(fn))
					continue;

				BigDecimal fileSeconds = parseTimeElapsedFromTxt(txt);
				if (fileSeconds != null) {
					total = total.add(fileSeconds);
					filesParsed.incrementAndGet();
				}
			}
		}
		return total;
	}

	private static boolean isSummaryTxt(String filename) {
		String lower = filename.toLowerCase();
		return lower.contains("summary") || lower.equals("testng-results.txt"); // conservative
																				// skip
																				// list
	}

	/**
	 * Parse the first (or last) "Time elapsed: X s" occurrence; if multiple, sum them.
	 */
	private static BigDecimal parseTimeElapsedFromTxt(Path txt) {
		BigDecimal sum = BigDecimal.ZERO;
		boolean found = false;

		try (BufferedReader br = Files.newBufferedReader(txt, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = TIME_ELAPSED_LINE.matcher(line);
				while (m.find()) {
					String n = m.group(1);
					try {
						sum = sum.add(new BigDecimal(n));
						found = true;
					}
					catch (NumberFormatException ignored) {
					}
				}
			}
		}
		catch (IOException e) {
			System.err.println("I/O error reading " + txt + ": " + e.getMessage());
			return null;
		}
		return found ? sum : null;
	}

	/** Secure XML factory. */
	private static DocumentBuilderFactory secureDocumentBuilderFactory() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		// Some implementations throw on these attributes; guard with try/catch.
		try {
			dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		}
		catch (Throwable ignored) {
		}
		try {
			dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		}
		catch (Throwable ignored) {
		}
		return dbf;
	}

	private static String formatHms(BigDecimal seconds) {
		BigDecimal millisBD = seconds.multiply(BigDecimal.valueOf(1000));
		long millis = millisBD.setScale(0, RoundingMode.HALF_UP).longValue();

		long hours = millis / 3_600_000;
		millis %= 3_600_000;
		long minutes = millis / 60_000;
		millis %= 60_000;
		long secs = millis / 1000;
		long ms = millis % 1000;

		return String.format("%02d:%02d:%02d.%03d", hours, minutes, secs, ms);
	}

	private static String formatSeconds(BigDecimal seconds) {
		return seconds.stripTrailingZeros().toPlainString();
	}

	private static BigDecimal safePercent(BigDecimal part, BigDecimal base) {
		if (base == null)
			return BigDecimal.ZERO;
		if (base.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		return part.multiply(BigDecimal.valueOf(100)).divide(base, 4, RoundingMode.HALF_UP);
	}

}
