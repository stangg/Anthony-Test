package org.gosh.validation.general.bank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * This is a helper class for splitting up a bank list XML file. 
 * This is useful because Xindice can't cope with a file this large
 * but it can cope with many smaller files. 
 * 
 * @author Kevin.Savage
 */
public class SplitBankList {
	public static void main(String[] args) throws IOException {
		File directory = new File("C:\\temp\\bankFiles");
		directory.mkdirs();
		FileUtils.cleanDirectory(directory);
		
		int numberOfCurrentFile = 0;
		File currentFile = null;
		List<String> content = new ArrayList<String>();
		LineIterator lineIterator = FileUtils.lineIterator(new File("c:\\temp\\report.xml"));
		while(lineIterator.hasNext()){
			String line = (String) lineIterator.next();
			if (line.contains("<Bank ")){
				numberOfCurrentFile++;
				currentFile = new File(directory, numberOfCurrentFile + ".xml");
			} else if (line.contains("</ISCDDocument>")){
				break;
			}
			if (numberOfCurrentFile>0){
				content.add(line);
			}
			if (line.contains("</Bank>")){
				FileUtils.writeLines(currentFile, content);
				content = new ArrayList<String>();
			}
		}
	}
}
