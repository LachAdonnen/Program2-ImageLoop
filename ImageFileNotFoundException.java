import java.io.FileNotFoundException;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            ImageFileNotFoundException.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
// Lab Section:      LAB ###
///////////////////////////////////////////////////////////////////////////////

public class ImageFileNotFoundException extends FileNotFoundException {

	public ImageFileNotFoundException(String errMsg) { super(errMsg); }
}