package utils.tools;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import utils.FileUtils;

public class SelectiveFileChooser extends JFileChooser {
	private static final long serialVersionUID = 4163511363777800754L;
	private static final int INVALID_EXTENSION = 0,
							 FILE_EXISTS = 1,
							 NO_WARNING = 2;
	
	private String allowed_extension;
	
	public SelectiveFileChooser(String allowed_extension) {
		super();
		this.allowed_extension = allowed_extension;
	}
	
	public SelectiveFileChooser() {
		super();
		allowed_extension = "";
	}
	
	@Override
    public void approveSelection(){
        File f = getSelectedFile();
        int safety_flag = applySafetyChecks(f);
        
    	if (safety_flag == INVALID_EXTENSION) {
    	     JOptionPane.showMessageDialog(this, "The specified file does not have the expected extension!", "Invalid File", JOptionPane.ERROR_MESSAGE);
    	     super.approveSelection();
    	     return;
    	}
    	else if (safety_flag == FILE_EXISTS) {
            int result = JOptionPane.showConfirmDialog(this, "The file already exists, "
            		+ "do you want to overwrite it?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
            switch(result){
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION: return;
                case JOptionPane.CLOSED_OPTION: return;
                case JOptionPane.CANCEL_OPTION: 
                	super.cancelSelection();
                	return;
            }
        }
        super.approveSelection();
    }   
	
	private int applySafetyChecks(File file) {
    	int dialog_type = getDialogType();
    	String canonical_path = "";
    	try {
    		canonical_path = file.getCanonicalPath();
		} catch (IOException e) { e.printStackTrace(); }
    	String file_extension = FileUtils.getExtension(canonical_path);
    	
    	if (dialog_type == OPEN_DIALOG && !file_extension.equals(allowed_extension) && !allowed_extension.isBlank()) 
    		return INVALID_EXTENSION;
    	
    	boolean file_exists = file.exists();
    	if (file_extension.isBlank())
    		file_exists = new File(canonical_path + ".png").exists();
    	
    	return (file_exists && dialog_type == SAVE_DIALOG)?FILE_EXISTS:NO_WARNING;
	}
	
}