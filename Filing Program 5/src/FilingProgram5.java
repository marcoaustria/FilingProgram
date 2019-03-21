
//Marco Austria
//6.15.18

//fixed continues!!!
//6.15.18 removed debugs sysouts

import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.awt.Desktop;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class FilingProgram5 {

	public static void main(String[] args) {

		long firstStartTime = System.nanoTime();

		GUIFilingProgram gui = new GUIFilingProgram();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(800, 600);

		JPanel middlePanel = new JPanel();
		// middlePanel.setBorder ( );

		// create the middle panel components

		JTextArea display = new JTextArea(30, 75);
		display.setEditable(false); // set textArea non-editable
		JScrollPane scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Add TextArea in to middle panel
		middlePanel.add(scroll);

		gui.add(middlePanel);
		gui.pack();
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);

		// Scanner input = new Scanner(System.in);

		// Intro
		display.append(
				"\nINFO:\nFiling Program for automatic invoice filing.\nAt any time, click 'X' in top right corner to stop program.\nManually look at every 'FILE MOVE SUCCESSFUL' to make sure addresses match.\nIf program is not running, close and open it again. If running fails, make sure folder path inputs are exact and without extra spaces.\n");

		// to be filed folder
		String folderPath = JOptionPane.showInputDialog("Enter folder path for invoices to be filed");

		// folderPath = "C:\\Users\\Marco\\Desktop\\INVOICES FAKE";

		File[] fileListInvoices = new File(folderPath).listFiles(File::isFile); // possible
		// debug
		// is:File

		// A+ PM folder path
		folderPath = JOptionPane.showInputDialog("Enter folder path for A+ PM");

		//folderPath = "C:\\Users\\Marco\\Google Drive (aplusrentals690@gmail.com)\\A+ PM - PROPERTIES"; // TODO debug

		File[] fileListPM = new File(folderPath).listFiles(File::isDirectory);

		// A+ LO folder path
		folderPath = JOptionPane.showInputDialog("Enter folder path for A+ LO");

		//folderPath = "C:\\Users\\Marco\\Google Drive (aplusrentals690@gmail.com)\\A+ LO - PROPERTIES";// TODO debug

		File[] fileListLO = new File(folderPath).listFiles(File::isDirectory);

		// A+ HC folder path
		folderPath = JOptionPane.showInputDialog("Enter folder path for A+ HC");

		//folderPath = "C:\\Users\\Marco\\Google Drive (aplusrentals690@gmail.com)\\A+ HC - HOME CHECK Properties";// TODO
																													// debug

		File[] fileListHC = new File(folderPath).listFiles(File::isDirectory);

		// A+ INACTIVE folder path
		folderPath = JOptionPane.showInputDialog("Enter folder path for A+ INACTIVE");

		//folderPath = "C:\\Users\\Marco\\Google Drive (aplusrentals690@gmail.com)\\A+ INACTIVE - PROPERTIES";// TODO
																											// debug

		File[] fileListIN = new File(folderPath).listFiles(File::isDirectory);

		// waits for round 2 to address problem files that do not have 1 search result.
		boolean round2Search = false;

		// return after program finishes to show how many moves
		int moveCount = 0;

		long startTime = System.nanoTime();

		File[] round2SearchInvoices = new File[fileListInvoices.length];

		// -------------------------------------------------------------SEARCH

		// searching for address number in files
		// iterate through list of scanned pdfs
		for (int i = 0; i < fileListInvoices.length; i++) {

			// System.out.println(i); TODO debug

			if (round2Search && i == 0) {

				double timeTaken = (double) (System.nanoTime() - startTime) / 1000000000.0;

				JOptionPane.showMessageDialog(null,
						"Immediately movable files transferred.\n" + "Files moved: " + moveCount + "\nTime taken: "
								+ timeTaken + "s" + "\nReady to analyze files that found multiple results?",
						"Ready?", JOptionPane.PLAIN_MESSAGE);
				
				display.append("Immediately movable files transferred.\n" + "Files moved: " + moveCount + "\nTime taken: "
								+ timeTaken + "s" + "\nReady to analyze files that found multiple results?");

				display.append("\nNow searching skipped files.\n");

				fileListInvoices = round2SearchInvoices;

			}

			// TODO may be the problem
			// skip empty file slots during round 2
			if (fileListInvoices[i] == null) {
				// TODO if end of loop, do not continue!
				// if end of invoices list
				if (i == fileListInvoices.length - 1) {

					// end of all searching after round 2
					if (round2Search == true) {

						break;
					}

					boolean arrayEmpty = true;
					for (int j = 0; j < round2SearchInvoices.length; j++) {
						if (round2SearchInvoices[j] != null) {
							arrayEmpty = false;
						}
					}

					// last iteration of round 1
					if (!arrayEmpty) {
						round2Search = true;

						i = -1;
						continue; // TODO should it be this?
					}
				}

				else
					continue;
			}

			boolean askUserDashResult = false;

			File currentInvoice = fileListInvoices[i];

			String currentFile = fileListInvoices[i].getName();

			String currentFilePath = fileListInvoices[i].getAbsolutePath();

			int lastSlash = currentFilePath.lastIndexOf("\\");
			String fullFileName = currentFilePath.substring(lastSlash); // includes
																		// '\'
			String addressNum = getAddressNum(currentFile);

			System.out.println("addressNum: " + addressNum + "\n"); // TODO delete

			// incorrect formatting
			if (addressNum.equals("0")) {

				// TODO uncomment
				display.append("Error: " + currentFile + " has an incorrect formatting.");

				// TODO if end of loop, do not continue!
				// if end of invoices list
				if (i == fileListInvoices.length - 1) {

					// end of all searching after round 2
					if (round2Search == true) {

						break;
					}

					boolean arrayEmpty = true;
					for (int j = 0; j < round2SearchInvoices.length; j++) {
						if (round2SearchInvoices[j] != null) {
							arrayEmpty = false;
						}
					}

					// last iteration of round 1
					if (!arrayEmpty) {
						round2Search = true;

						i = -1;
						continue; // TODO should it be this?
					}
				}

				else
					continue;
			}

			// find new folder
			SearchResults results = new SearchResults();

			results.checkFolder("PM", fileListPM, addressNum);
			results.checkFolder("LO", fileListLO, addressNum);
			results.checkFolder("HC", fileListHC, addressNum);
			results.checkFolder("IN", fileListIN, addressNum);

			// if no results
			if (results.isEmpty()) {

				if (!round2Search) {
					round2SearchInvoices[i] = fileListInvoices[i];

					// TODO if end of loop, do not continue!
					// if end of invoices list
					if (i == fileListInvoices.length - 1) {

						// end of all searching after round 2
						if (round2Search == true) {

							break;
						}

						boolean arrayEmpty = true;
						for (int j = 0; j < round2SearchInvoices.length; j++) {
							if (round2SearchInvoices[j] != null) {
								arrayEmpty = false;
							}
						}

						// last iteration of round 1
						if (!arrayEmpty) {
							round2Search = true;
							i = -1;
							continue; // TODO should it be this?
						}
					}

					else
						continue;
				}

				// find results with just first address numbers, Ex. 652, instead of 652-1403
				if (addressNum.contains("-")) {

					String trialAddressNum = addressNum.substring(0, addressNum.lastIndexOf("-") + 1);// trying to
																										// include '-'

					display.append("\nNo folder match found for address: " + addressNum
							+ "\nSearching for address number: " + trialAddressNum + "\n");

					results.checkFolder("PM", fileListPM, trialAddressNum);
					results.checkFolder("LO", fileListLO, trialAddressNum);
					results.checkFolder("HC", fileListHC, trialAddressNum);
					results.checkFolder("IN", fileListIN, trialAddressNum);

					// after search for 135- and find only one result, still ask user
					if (results.getSize() == 1)
						askUserDashResult = true;

				}

			} // if empty

			// if multiple results
			if (results.getSize() > 1 || askUserDashResult) {

				if (!round2Search) {
					round2SearchInvoices[i] = fileListInvoices[i];
					// TODO if end of loop, do not continue!
					// if end of invoices list
					if (i == fileListInvoices.length - 1) {

						// end of all searching after round 2
						if (round2Search == true) {

							break;
						}

						boolean arrayEmpty = true;
						for (int j = 0; j < round2SearchInvoices.length; j++) {
							if (round2SearchInvoices[j] != null) {
								arrayEmpty = false;
							}
						}

						// last iteration of round 1
						if (!arrayEmpty) {
							round2Search = true;
							i = -1;
							continue; // TODO should it be this?
						}
					}

					else
						continue;

				}

				// repeats to ask user question
				while (true) {
					display.append("\nCurrent invoice file being worked on:\n" + currentFilePath + "\nAddress Number: "
							+ addressNum + "\n");

					// shows user the pdf
					openDoc(currentFilePath);

					String choice = JOptionPane.showInputDialog(
							"\nMultiple matches were found.\nPlease see file to confirm address.\nPlease choose a folder number by entering a number from the selection, or type 'skip' to skip the invoice file.\n\n"
									+ results.toString());

					int intChoice = 0;

					// convert choice to int
					try {
						Integer.getInteger(choice);
						// is a valid integer
						if (true) {
							intChoice = Integer.parseInt(choice);
						}
					} catch (NumberFormatException ex) {
						// s is not an integer

						if (choice.contains("skip")) {
							display.append("\n");
							break;
						}

						else if (choice.contains("redo")) {
							continue;
						} else

							JOptionPane.showMessageDialog(null, "Please enter a valid integer choice.\n", "ERROR",
									JOptionPane.PLAIN_MESSAGE);
						// display.append();
						continue;
					}

					if (intChoice > results.getSize() || intChoice == 0) {

						JOptionPane.showMessageDialog(null, "Please enter a valid choice.\n", "ERROR",
								JOptionPane.PLAIN_MESSAGE);

						// display.append("Please enter a valid choice.\n");
						continue;

						// TODO may need to move openDoc outside of while loop
					}

					// chosen one file
					// prepare for next if statement to move
					// use arraylist clear and add choice to the list to be moved
					else {
						String choiceString = results.get(intChoice - 1);
						results.clear();
						results.set(choiceString);
						break;
					}

				} // while

			} // if multiple results

			// found one solution, move file
			if (results.getSize() == 1) {

				String newPath = results.get(0);

				// edit newPath, cut off PM for ex.
				newPath = newPath.substring(newPath.indexOf(" ") + 1);
				
				// successful move
				if (fileListInvoices[i].renameTo(new File(newPath + "\\Repairs\\Invoices" + fullFileName))) {

					display.append("\n------------FILE MOVE SUCCESSFUL!------------\n");

					// parse newPath

					String fullFolderName = newPath.substring(newPath.lastIndexOf("\\") + 1);

					display.append(currentFile + "\n" + addressNum + "\n" + fullFolderName + "\n");

					moveCount++;

					// give time to see that move is correct
					// try {
					// TimeUnit.SECONDS.sleep(1);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }

				}

				// failed to move
				else {
					while (true) {

						String fullFolderName = newPath.substring(newPath.lastIndexOf("\\") + 1);

						display.append("\nFile " + addressNum
								+ " failed to move!\nTry adding a Repairs and Invoices folder.\nOr filename may already exist in folder.\n");

						// choose to redo or skip
						String failResponse = JOptionPane.showInputDialog("\nFile: " + addressNum
								+ " failed to move to: " + fullFolderName
								+ "\nTry adding a Repairs and Invoices folder.\nOr filename may already exist in folder.\nCLOSE PDF!!!!!\n"
								+ "If you would like to redo the current file please enter 'redo', if you would like to skip please enter 'skip'.\n");

						// redo
						if (failResponse.contains("redo")) {
							// redo for loop iteration
							i = i - 1;
							break;
						}

						// skip
						else if (failResponse.contains("skip")) {
							break;
						} else {
							display.append("Please enter a valid response.");
						}
					} // while
				} // else

			} // if one solution

			// no solution found
			if (results.isEmpty() && round2Search) {

				display.append("No results found for: " + currentFile
						+ "\nTry finding EXACT unit number and changing filename according to folder.\n");
			}

			// if end of invoices list
			if (i == fileListInvoices.length - 1) {

				// end of all searching after round 2
				if (round2Search == true) {

					break;
				}

				boolean arrayEmpty = true;
				for (int j = 0; j < round2SearchInvoices.length; j++) {
					if (round2SearchInvoices[j] != null) {
						arrayEmpty = false;
					}
				}

				// last iteration of round 1
				if (!arrayEmpty) {
					round2Search = true;

					i = -1;
				}
			}

		} // for loop for each invoice

		display.append(
				"\nFinished moving invoices, go back and move invoices that weren't found in either PM or LO.\n");
		display.append("Total number of files moved: " + moveCount);
		double totalTime = (double) (System.nanoTime() - firstStartTime) / 1000000000.0;
		display.append("\nTotal running time of program: " + totalTime + "s");

		// make text file
		Writer writer = null;

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(timeStamp + " Invoices Move.txt"), "utf-8"));

			for (String line : display.getText().split("\n"))

				// use \r\n instead of \n because notepad
				writer.write(line + "\r\n");

		} catch (IOException ex) {
			// Report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */}
		}

	}// main

	// ==================================================================================================================================METHODS

	// get address num of file
	// Example:
	// VendorName InvoiceNum AddressNum
	// get addressnum based on last space in filename
	public static String getAddressNum(String currentFile) {
		String addressNum = "0";

		try {

			// one space in between
			// TODO unusual
//			String cutOff1 = currentFile.substring(0, currentFile.lastIndexOf(" "));
//			String cutOff2 = cutOff1.substring(0, cutOff1.lastIndexOf(" "));
//			addressNum = cutOff2.substring(cutOff2.lastIndexOf(" ") + 1);

			// TODO original, uncomment
			 addressNum = currentFile.substring(currentFile.lastIndexOf(" ") + 1,
			 currentFile.lastIndexOf('.'));

			// should be inclusive, make sure
			// correct
		} catch (IndexOutOfBoundsException e) {

			// TODO uncomment?
			JOptionPane.showMessageDialog(null, "Error addressNum: " + currentFile, "ERROR", JOptionPane.PLAIN_MESSAGE);

			// TODO delete?
			addressNum = "0";
		}

		return addressNum;
	}

	public static void openDoc(String currentFilePath) {

		// if want to add time
		// try {
		// TimeUnit.SECONDS.sleep(1);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		// open doc
		File pdfFile = new File(currentFilePath);
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(pdfFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "io exception error1", "ERROR", JOptionPane.PLAIN_MESSAGE);

				e.printStackTrace();
			}
		}
	}// openDoc

}// class
