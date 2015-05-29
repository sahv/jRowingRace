package sk.insomnia.rowingRace.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.insomnia.rowingRace.so.Team;

public class TeamEditDialogController {

	@FXML
	private TextField tfTeamName;

	
	
	
	private Team team;
    private Stage dialogStage;
    private boolean okClicked = false;
    private ResourceBundle rb;
	
	public void setTeam(Team team){
		this.team = team;
		if (team!=null){
			if (team.getName()!=null) tfTeamName.setText(team.getName());
		} else {
			tfTeamName.setText("");
		}
	}

	public void setDialogStage(Stage dialogStage) {
	      this.dialogStage = dialogStage;
	       
	}
	
	@FXML
	private void initialize() {

		Locale locale = Locale.getDefault(); 
		this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.ui", locale);

	}

	 public boolean isOkClicked() {
	      return okClicked;
	  }
	 @FXML
	  private void handleOk() {
	      if (isInputValid()) {
	          team.setName(tfTeamName.getText());
	          okClicked = true;
	          dialogStage.close();
	      }	      
	  }
	 @FXML
	  private void handleCancel() {
	      dialogStage.close();
	  }
	 private boolean isInputValid() {
	      String errorMessage = "";

	      if (tfTeamName.getText() == null || tfTeamName.getText().length() == 0) {
	          errorMessage += rb.getString("ERR_FIRST_NAME_EMTPY");
	      }
	      
	      if (errorMessage.length() == 0) {
	          return true;
	      } else {
	          Dialogs.showErrorDialog(dialogStage, errorMessage,
	        		  rb.getString("INFO_CORRECT_FIELDS"),
	        		  rb.getString("INFO_CORRECT_FIELDS_TITLE"));
	          return false;
	      }
	  }	 

}
