import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ScriptFormat {
	private static final String[] IGNORE_PATTERNS = {"^[0-9].*-->.*[0-9]$", "^[0-9]*$"};
	private static final String UNDERSCORE = "_";
	private static final String DOT = ".";
	private static final String EMPTY = "";
	private static final String USER_DIRECTORY = "user.dir";
	private static final String DEFAULT_EXTENSION = "srt";
	private static final String INPUT_FOLDER = "\\Input";
	private static final String OUTPUT_FOLDER = "\\Output\\";

	public static void formatScript(File script){
		if (script == null) {
			return;
		}

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(script), StandardCharsets.UTF_8))) {
			StringBuilder outputScript = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (!isIgnoreLine(line)) {
					outputScript.append(line);
				}
			}

			if (outputScript.length() > 0) {
				String outputPath = getOuputPath() + System.currentTimeMillis() + UNDERSCORE + script.getName();
				try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputPath),
						StandardCharsets.UTF_8)) {
					writer.write(outputScript.toString());
				}
			}
			
			copyToClipboard(outputScript.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
		
		script.delete();
		
	}

	public static File readScript(String path) {
		File inputFolder = new File(path);
		File[] files = inputFolder.listFiles();
		File inputScript = getFirstFileByExtension(files, DEFAULT_EXTENSION);
		return inputScript;
	}

	public static String getInputPath() {
		return getProjectPath() + INPUT_FOLDER;
	}

	private static String getOuputPath() {
		return getProjectPath() + OUTPUT_FOLDER;
	}

	private static String getProjectPath() {
		return System.getProperty(USER_DIRECTORY);
	}

	private static File getFirstFileByExtension(File[] sources, String extension) {
		for (File file : sources) {
			if (getExtension(file).equals(extension)) {
				return file;
			}
		}

		return null;
	}

	private static String getExtension(File file) {
		String fileName = file.getName();
		int extensionIndex = fileName.lastIndexOf(DOT) + 1;
		if (extensionIndex > 0) {
			return fileName.substring(extensionIndex);
		}
		return EMPTY;
	}

	private static boolean isIgnoreLine(String line) {
		if (line.isBlank() || line.isEmpty()) {
			return true;
		}
		
		for (String ignorePattern : IGNORE_PATTERNS) {
			if (line.matches(ignorePattern)) {
				return true;
			}
		} 
		return false;
	}

	private static void copyToClipboard(String text) {
		Clipboard clipboard = getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	private static Clipboard getSystemClipboard() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		return defaultToolkit.getSystemClipboard();
	}
}
