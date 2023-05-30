package utils;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

import utils.tools.SelectiveFileChooser;
import window.Window;

public class FileUtils {
	public static final int SAVE_SELECTION = 0,
							OPEN_SELECTION = 1;
	
	public static void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
	}
	
	/*
	 * Apre un box per specificare o aprire un dato file
	 *    - selection_type: tipo di selezione, se è FileUtils.SAVE_SELECTION allora il box sarà uno di salvataggio,
	 *    					se invece è OPEN_SELECTION sarà uno di apertura di un file
	 *    - window_text: il testo posto al vertice della finestra
	 *    - window: la finestra principale del programma, necessaria per riferimento
	 * */
	public static File getFileSelection(int selection_type, String window_text, Window window) {
		SelectiveFileChooser file_chooser = new SelectiveFileChooser("lme");
		file_chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		file_chooser.setDialogTitle(window_text);
		
		JFrame frame = window.getFrame();
		int result = (selection_type == SAVE_SELECTION)?file_chooser.showSaveDialog(frame):file_chooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
			return file_chooser.getSelectedFile();
		return null;
	}
	
	/*
	 * Apre un box per specificare o aprire un dato file
	 *    - selection_type: tipo di selezione, se è FileUtils.SAVE_SELECTION allora il box sarà uno di salvataggio,
	 *    					se invece è OPEN_SELECTION sarà uno di apertura di un file
	 *    - window_text: il testo posto al vertice della finestra
	 *    - window: la finestra principale del programma, necessaria per riferimento
	 *    - suggested_filename: il nome iniziale del file prima che venga specificato manualmente dall'utente
	 * */
	public static File getFileSelection(int selection_type, String window_text, String suggested_filename, Window window) {
		SelectiveFileChooser file_chooser = new SelectiveFileChooser();
		file_chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		file_chooser.setDialogTitle(window_text);
		file_chooser.setSelectedFile(new File(suggested_filename));
		
		JFrame frame = window.getFrame();
		int result = (selection_type == SAVE_SELECTION)?file_chooser.showSaveDialog(frame):file_chooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
			return file_chooser.getSelectedFile();
		return null;
	}
	
	
	/*
	 * Apre un box per specificare o aprire un dato file
	 *    - selection_type: tipo di selezione, se è FileUtils.SAVE_SELECTION allora il box sarà uno di salvataggio,
	 *    					se invece è OPEN_SELECTION sarà uno di apertura di un file
	 *    - window_text: il testo posto al vertice della finestra
	 *    - window: la finestra principale del programma, necessaria per riferimento
	 *    - suggested_filename: il nome iniziale del file prima che venga specificato manualmente dall'utente
	 *    - allowed_extension: estensione accettabile nel salvataggio del file, usare un'estensione diversa porterà ad un messaggio
	 *    					   di errore 
	 * */
	public static File getFileSelection(int selection_type, String window_text, String suggested_filename, String allowed_extension, 
				Window window) {
		SelectiveFileChooser file_chooser = new SelectiveFileChooser(allowed_extension);
		file_chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		file_chooser.setDialogTitle(window_text);
		file_chooser.setSelectedFile(new File(suggested_filename));
		
		JFrame frame = window.getFrame();
		int result = (selection_type == SAVE_SELECTION)?file_chooser.showSaveDialog(frame):file_chooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
			return file_chooser.getSelectedFile();
		return null;
	}
	
	// scrive il testo specificato in un determinato file salvandolo sul sistema
	public static void WriteToFile(File file, String text) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// scrive il testo specificato in un determinato file salvandolo sul sistema con la data estensione
	public static void WriteToFile(File file, String text, String extension) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file + extension));
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// ritorna l'estensione di un file dal suo nome o percorso
	public static String getExtension(String filename) {
		if (filename.indexOf('.') == -1)
			return "";
		
		return filename.substring(filename.lastIndexOf('.')+1);
	}
	
	// rimuove l'estensione di un file dal nome/percorso del file
	public static String removeFileExtension(String filepath) {
		if (filepath.indexOf('.') == -1)
			return filepath;
		
		return filepath.substring(0, filepath.lastIndexOf('.'));
	}
	
	// salva l'immagine specificata nella destinazione identificata dal file dest
	public static void saveImage(BufferedImage image, File dest) {
		try {
			String img_path = removeFileExtension(dest.getCanonicalPath());
			img_path += ".png";
			ImageIO.write(image, "png", new File(img_path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

