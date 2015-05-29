package sk.insomnia.rowingRace.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.so.DisciplineCategory;
import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DisciplineCategoryEditDialogController {

	@FXML
	private TextField tfDisciplineCategoryName;
	@FXML
	private TextField tfDisciplineCategoryAcronym;
	
	private DisciplineCategoryDto disciplineCategory;
    private Stage dialogStage;
    private boolean okClicked = false;

    private ResourceBundle rb;
    
	@FXML
	private void initialize() {
		
		Locale locale = Locale.getDefault(); 
		this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.ui", locale);

	}
	
	
	 @FXML
	  private void handleOk() {
	      if (isInputValid()) {	    	  
//	    	  disciplineCategory.setName(tfDisciplineCategoryName.getText());
	          okClicked = true;
	          dialogStage.close();
	      }	      
	  }
	 @FXML
	  private void handleCancel() {
	      dialogStage.close();
	  }
	 
	 private boolean isInputValid(){
		 String errorMessage = "";
		 if (tfDisciplineCategoryName.getText()==null){
			 errorMessage += rb.getString("ERR_NAME_EMTPY");
		 } 
		 if (tfDisciplineCategoryAcronym.getText()==null){
			 errorMessage += rb.getString("ERR_ACRONYM_EMTPY");
		 } 
		 if (errorMessage.length()==0){
			return true; 
		 }	 else {
	          Dialogs.showErrorDialog(dialogStage, errorMessage,
	        		  rb.getString("INFO_CORRECT_FIELDS"),
	        		  rb.getString("INFO_CORRECT_FIELDS_TITLE"));
			 return false;
		 }
	 }
	public DisciplineCategoryDto getDisciplineCategory() {
		return disciplineCategory;
	}
	public void setDisciplineCategory(DisciplineCategoryDto disciplineCategory) {
		this.disciplineCategory = disciplineCategory;
		if (disciplineCategory!=null){
//			tfDisciplineCategoryName.setText(disciplineCategory.getName());
			tfDisciplineCategoryAcronym.setText(disciplineCategory.getAcronym());
		}
	}


	public Stage getDialogStage() {
		return dialogStage;
	}


	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}


	public boolean isOkClicked() {
		return okClicked;
	}


	public void setOkClicked(boolean okClicked) {
		this.okClicked = okClicked;
	}
	 

}
