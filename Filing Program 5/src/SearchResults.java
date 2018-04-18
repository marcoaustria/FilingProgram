import java.io.File;
import java.util.ArrayList;
//import java.util.Scanner;

import javax.swing.JOptionPane;

public class SearchResults {
	static ArrayList<String> arrayList;

	SearchResults() {
		arrayList = new ArrayList<String>();

	}

	public static void set(String filePath) {
		arrayList.add(filePath);
	}

	public static String get(int choice) {
		return arrayList.get(choice);

	}

	public static boolean isEmpty() {
		return arrayList.isEmpty();
	}

	public String toString() {
		String returnString = "";
		for (int i = 0; i < arrayList.size(); i++) {

			// trying to return folder addressnum and not filepath
			// TODO use getfolderaddressnum instead?
			// Probably correct

			// adds the "PM "
			returnString += (i + 1) + ". " + arrayList.get(i).substring(0, arrayList.get(i).indexOf(" ")) + " ";
			returnString += arrayList.get(i).substring(arrayList.get(i).lastIndexOf('\\') + 1) + "\n";
		}

		return returnString;
	}

	public static int getSize() {
		return arrayList.size();
	}

	public static void clear() {
		arrayList.clear();
	}

	// TODO what does this do and what does getname return for a file list?
	// String Ex. 140 Rubino Drive
	public static String getFolderAddressNum(String currentFileFolder) {

		String folderAddressNum = "0";

		try {
			folderAddressNum = currentFileFolder.substring(0, currentFileFolder.indexOf(" "));
		} catch (IndexOutOfBoundsException e) {
			//System.out.println("Error folderAddressNum: " + currentFileFolder);
			JOptionPane.showMessageDialog(null, "Error folderAddressNum: " + currentFileFolder, "ERROR", JOptionPane.PLAIN_MESSAGE);

		}

		System.out.println(folderAddressNum);//TODO debug
		
		return folderAddressNum;
	}

	// adds the filepath of found folders to arrayList
	public static void checkFolder(String type, File[] fileList, String addressNum) {

		//Scanner input = new Scanner(System.in);
		//System.out.println(fileList.toString());//TODO debug

		//System.out.println(fileList.length);// TODO debug
		
		// going through folders, just called files because thats the syntax
		for (int j = 0; j < fileList.length; j++) {
			String currentFile = fileList[j].getName();
			// TODO print this out to see what getName gives us

			String folderAddressNum = getFolderAddressNum(currentFile);

			// getFolderAddressNum returned a bad formatting for the folder name
			if (folderAddressNum.equals("0")) {
//				System.out.println("Error: " + currentFile + " has an incorrect formatting.\n");
//
//				System.out.println("Please edit the folder name and then enter 'redo' to redo the search or 'skip'.\n");
//				
				String failResponse = JOptionPane.showInputDialog("Error: " + currentFile + " has an incorrect formatting.\n"+"Please edit the folder name and then enter 'redo' to redo the search or 'skip'.\n");

			//	String failResponse = input.nextLine();
				
				int count = 0;
				while (!(failResponse.contains("redo") || failResponse.contains("skip"))) {

				//	if (count != 0)
						//failResponse = input.nextLine();
					failResponse = JOptionPane.showInputDialog("Please enter a valid response");
					//System.out.println("Please enter a valid response.");
					
					//JOptionPane.showMessageDialog(null, "Please enter a valid response.", "ERROR", JOptionPane.PLAIN_MESSAGE);

					count++;
				} // while

				// redo
				if (failResponse.contains("redo")) {
					// redo for loop iteration
					j = j - 1;
					continue;
				}
				// skip
				else if (failResponse.contains("skip")) {
					continue;
				}

			} // if wrong formatting

			//included dash search
			if (folderAddressNum.equals(addressNum)||(addressNum.charAt(addressNum.length()-1)=='-' && folderAddressNum.contains(addressNum))) {
				// switch to add PM, LO etc to String

				switch (type) {
				case "PM":
					arrayList.add("PM "+ fileList[j].getAbsolutePath());
					break;
				case "LO":
					arrayList.add("LO "+ fileList[j].getAbsolutePath());
					break;
				case "HC":
					arrayList.add("HC "+ fileList[j].getAbsolutePath());
					break;
				case "IN":
					arrayList.add("IN "+ fileList[j].getAbsolutePath());
					break;
				}
			}

			// if address like 615-1452
			// TODO is this correct? should it be used?
			// else if (addressNum.contains("-") && folderAddressNum.contains(addressNum)) {
			// arrayList.add(fileList[j].getAbsolutePath());
			//
			// }

		} // for loop folder search

	}// checkFolder

}
