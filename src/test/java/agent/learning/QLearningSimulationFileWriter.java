package agent.learning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class QLearningSimulationFileWriter {

	static String fileName;
	
	public QLearningSimulationFileWriter(String fileName) {
		this.fileName = fileName;
		createNewFile();
	}
	
	private void createNewFile() {
		File file = new File(fileName);
		try {
			boolean result = Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void writeToFile(String resultsStr) throws IOException {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	    writer.write(resultsStr);
	    writer.close();
	}
}
