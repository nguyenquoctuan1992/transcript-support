import java.io.IOException;

public class MainProgram {

	public static void main(String[] args) throws IOException {
		String inputPath = ScriptFormat.getInputPath();
		new Thread(new ScriptFormatWatcher(inputPath)).start();
	}

}
