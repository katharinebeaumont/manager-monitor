package agent.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PidResponse {

	private static final Logger log = LoggerFactory.getLogger(PidResponse.class);
	
	public String getResponse() {
		FileInputStream fis = null;
		try {
			File f = new File("application.pid");
			fis = new FileInputStream(f);
			
			BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
	        String pid = buffer.lines().collect(Collectors.joining(""));
	        log.info("Reading in PID: " + pid);
	        fis.close();
			return pid;
		} catch (IOException io) {
			log.error("Could not read PID");
			return "error";
		} finally {
			try {
				fis.close();
			} catch (IOException | NullPointerException ex) {
				log.error("Could not close stream");
			}
		}
		
	}
	
}
