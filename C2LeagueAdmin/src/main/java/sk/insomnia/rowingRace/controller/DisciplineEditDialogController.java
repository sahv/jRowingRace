package sk.insomnia.rowingRace.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.DisciplineCategory;

public class DisciplineEditDialogController {

	
	@FXML
	private TextField tfDisciplineName;
	@FXML
	private ComboBox<DisciplineCategory> cbDisciplineCategory;


	
	private Discipline discipline;
	
	
	private ObservableList<DisciplineCategory> disciplinesCategoryData = FXCollections.observableArrayList();
    private Stage dialogStage;
    private boolean okClicked = false;
    
    private ResourceBundle rb;
    
	@FXML
	private void initialize() {
		
		Locale locale = Locale.getDefault(); 
		this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.ui", locale);

	}


	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
		if (discipline!=null){
			tfDisciplineName.setText(discipline.getName());
//			cbDisciplineCategory.setValue(discipline.getCategory());
		}
	}

	public ObservableList<DisciplineCategory> getDisciplinesCategoryData() {
		return disciplinesCategoryData;
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

	 @FXML
	  private void handleOk() {
	      if (isInputValid()) {	    	  
	    	  discipline.setName(tfDisciplineName.getText());
//	    	  discipline.setCategory(cbDisciplineCategory.getValue());	          
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
		 if (tfDisciplineName.getText()==null){
			 errorMessage +=  rb.getString("ERR_NAME_EMTPY");
		 } 
//		 if (cbDisciplineCategory.getValue() == null){
//			 errorMessage += rb.getString("ERR_CATEGORY_EMTPY");
//		 } 
		 if (errorMessage.length()==0){
			return true; 
		 }	 else {
	          Dialogs.showErrorDialog(dialogStage, errorMessage,
	        		  rb.getString("INFO_CORRECT_FIELDS"),
	        		  rb.getString("INFO_CORRECT_FIELDS_TITLE"));
			 return true;
		 }
	 }

	
}
