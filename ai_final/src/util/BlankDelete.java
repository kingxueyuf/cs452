package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class BlankDelete {

	public void fixFile(String path, String newFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String sCurrentLine;
			ArrayList<String> content = new ArrayList<String>();
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains("Total Blocks")||sCurrentLine.equals(""))
					continue;
				String[] array = sCurrentLine.split(" ");
				System.out.println(array[1]);
				content.add(array[1]);
			}

			BufferedWriter fout;
			fout = new BufferedWriter(new FileWriter(newFile));
			for (int i = 0; i < content.size(); i++) {
				fout.write(content.get(i));
				fout.newLine();
			}
			fout.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		BlankDelete bD = new BlankDelete();
		bD.fixFile("testData_fixed.txt", "newFile_fixed.txt");
	}

}
