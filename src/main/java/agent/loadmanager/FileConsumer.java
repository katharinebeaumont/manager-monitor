package agent.loadmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO keeping for reference but not required
public class FileConsumer implements Consumer<String> {

	private BufferedWriter writer;
	private static final Logger log = LoggerFactory.getLogger(FileConsumer.class);
	private int lines = 0;
	private File outputFile;
	
	public FileConsumer(File outputFile) throws IOException {
		this.outputFile = outputFile;
		this.writer = new BufferedWriter(new FileWriter(outputFile, true));
	}
	
	@Override
	public void accept(String t) {
		try {
			writer.write(t);
			writer.newLine();
			lines++;
			// Every 50 lines, flush the buffered writer
			if (lines > 50) {
				writer.close();
				writer = new BufferedWriter(new FileWriter(outputFile, true));
				lines = 0;
			}
		} catch (IOException e) {
			log.error("Error writing output to file:");
			e.printStackTrace();
			try {
				writer.close();
			} catch (IOException e1) {
				log.error("Could not close buffered writer.");
				e1.printStackTrace();
			}
		} 
	}
	
	@PreDestroy
	public void shutdown() {
		try {
			writer.close();
		} catch (IOException e) {
			log.error("Could not close buffered writer.");
			e.printStackTrace();
		}
	}

}
