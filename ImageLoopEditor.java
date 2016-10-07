import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.print.attribute.standard.PrinterMessageFromOperator;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            ImageLoopEditor.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
// Lab Section:      LAB ###
///////////////////////////////////////////////////////////////////////////////

public class ImageLoopEditor {
	
	public static void main(String[] args) {
		// Stores a flag used to terminate the program once finished
		boolean done = false;
		// Used to read in user input
		Scanner stdin = new Scanner(System.in);
		// Stores the images in a loop structure 
		LinkedLoop<Image> imageLoop = new LinkedLoop<Image>();
		// Main loop for user input
		while (!done) { 
			System.out.print("enter command (? for help)>");
			done = userInput(stdin.nextLine());
		}
		stdin.close();
	}
	
	private static boolean userInput(String input) {
		boolean shouldQuit = false;
		
		char command = input.charAt(0);
		String argument = "";
		if (input.length() > 1) { argument = input.substring(1).trim(); }
		
		try {
			switch (command) {
				case '?': // Display prompt options
					optionPrintCommands();
					break;
				case 's': // Save current loop to file
					optionSaveLoop(argument);
					break;
				case 'l': // Load loop file
					optionLoadLoop(argument);
					break;
				case 'd': // Display image list
					optionPrintImageList();
					break;
				case 'p': // Display single image
					optionDisplayImage();
					break;
				case 't': // Test image loop
					optionTestImageLoop();
					break;
				case 'f': // Go forward in loop
					optionMoveForward();
					break;
				case 'b': // Go backwards in loop
					optionMoveBackward();
					break;
				case 'j': // Jump through loop
					optionJumpForward(Integer.valueOf(argument));
					break;
				case 'r': // Remove current image
					optionRemoveImage();
					break;
				case 'a': // Add new image (after current)
					optionAddImageAfter(argument);
					break;
				case 'i': // Add new image (before current)
					optionAddImageBefore(argument);
					break;
				case 'c': // Search image title
					optionSearchImageTitle(argument);
					break;
				case 'u': // Update image display time
					optionUpdateDuration(Integer.valueOf(argument));
					break;
				case 'e': // Edit image title
					optionEditTitle(argument);
					break;
				case 'x': // Exit
					shouldQuit = true;
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		catch (IllegalArgumentException e) { 
			System.out.println("invalid command");
		}
		return shouldQuit;
	}
	
	private static void optionPrintCommands() {
		System.out.println(
			"s (save)      l (load)       d (display)        p (picture)\n" +
			"f (forward)   b (backward)   j (jump)           t (test)\n" +
			"r (remove)    a (add after)  i (insert before)  e (retitle)\n" +
			"c (contains)  u (update)     x (exit)");
		return;
	}
	
	private static void optionSaveLoop(String fileName) {
		validateFilename(fileName);
		
		// Attempt to open the file for writing
		PrintStream fileWriter = null;
		try {
			File fileOut = new File(fileName);
			if (fileOut.exists()) {
				System.out.println("warning: file already exists, " +
						"will be overwritten");
			}
			fileWriter = new PrintStream(fileOut);
			//TODO Write the current loop to the file
		}
		catch (IOException e) {
			System.out.println("unable to save");
		}
		finally {
			if (fileWriter != null) { fileWriter.close(); }
		}
		return;
	}
	
	private static void optionLoadLoop(String fileName) {
		validateFilename(fileName);
		
		// Attempt to open the file for reading
		Scanner fileReader = null;
		try {
			File fileIn = new File(fileName);
			fileReader = new Scanner(fileIn);
			//TODO Read the contents of the file into a loop
		}
		catch (IOException e) {
			System.out.println("unable to load");
		}
		finally {
			if (fileReader != null) { fileReader.close(); }
		}
	}

	private static void optionPrintImageList() {}
	
	private static void optionDisplayImage() {}
	
	private static void optionTestImageLoop() {}
	
	private static void optionMoveForward() {}
	
	private static void optionMoveBackward() {}
	
	private static void optionJumpForward(int numToJump) {
		validatePositiveInt(numToJump);
		//TODO Jump through the list
	}
	
	private static void optionRemoveImage() {}
	
	private static void optionAddImageAfter(String fileName) {
		validateFilename(fileName);
	}
	
	private static void optionAddImageBefore(String fileName) {
		validateFilename(fileName);
	}
	
	//TODO Should likely have a more generic "AddImage" method that is called
	// from both the after and before code paths. This tag should call
	// validateImageFileExists and handle the exception.
	
	private static void optionSearchImageTitle(String searchStr) {
		searchStr = removeStringQuotes(searchStr);
	}
	
	private static void optionUpdateDuration(int time) {
		validatePositiveInt(time);
		//TODO Update image object duration
	}
	
	private static void optionEditTitle(String newTitle) {
		newTitle = removeStringQuotes(newTitle);
	}
	
	private static String removeStringQuotes(String argument) {
		String returnString = argument;
		if (argument.startsWith("\"") &&
			argument.endsWith("\"")) {
			returnString = argument.substring(1, argument.length() - 2);
		}
		return returnString;
	}
	
	/*
	 * Validate using a regex to ensure there are no illegal characters.
	 * String can only contain:
	 * 	a-z, A-Z, 0-9, _, ., /, -
	 * 	The entire string must only use those characters ( ^[regex]$ )
	 *  Need to have one or more of the characters ( +$ )
	 */
	private static void validateFilename(String fileName) {
		if (!fileName.matches("^[a-zA-Z0-9_./-]+$")) {
			throw new IllegalArgumentException();
		}
		return;
	}

	private static void validatePositiveInt(int numToCheck) {
		if (numToCheck < 0) { throw new IllegalArgumentException(); }
	}

	private static void validateImageFileExists(String fileName) 
			throws FileNotFoundException {
		File imageFile = new File(fileName);
		if (!imageFile.exists()) { throw new FileNotFoundException(); }
	}
}
