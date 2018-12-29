package agent.manager;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplicationNamingService {

	private static final Logger log = LoggerFactory.getLogger(ApplicationNamingService.class);
	
	/*
	 * Strip the current name of any digits. Increment the version number.
	 */
	public String generateName(String name) {
		
		LinkedList<String> numbers = new LinkedList<String>();

		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(name); 
		while (m.find()) {
		   numbers.add(m.group());
		}
		
		String newApplicationName = name.replaceAll("\\d+", "");
		
		if (numbers.isEmpty()) {
			newApplicationName += "2";
		} else {
			String versionNumberStr = "";
			for (String i : numbers) {
				versionNumberStr += i;
			}
			try {
				int versionNumber = Integer.parseInt(versionNumberStr);
				int newVersionNumber = versionNumber + 1;
				newApplicationName += newVersionNumber;
			} catch (NumberFormatException nfe) {
				log.error("Could not parse version number from " + newApplicationName);
			}
		}
		return newApplicationName;
	}

}
