import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class ScriptFormatWatcher implements Runnable{
	private String inputPath;
	
	private WatchService watchService;
	
	public ScriptFormatWatcher(String inputPath) {
		this.inputPath = inputPath;
	}
	
	@Override
	public void run() {
		try {
			this.watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(inputPath);
			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			WatchKey key;
			while ((key = watchService.take()) != null) {
				List<WatchEvent<?>> watchEvents = key.pollEvents();
				for (WatchEvent<?> watchEvent : watchEvents) {
					WatchEvent.Kind<?> kind = watchEvent.kind();
					if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
						Thread.sleep(100);
						File script = ScriptFormat.readScript(inputPath);	
						ScriptFormat.formatScript(script);
					}
				}
				key.reset();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}
