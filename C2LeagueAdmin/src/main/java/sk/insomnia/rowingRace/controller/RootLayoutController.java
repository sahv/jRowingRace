package sk.insomnia.rowingRace.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import sk.insomnia.rowingRace.application.AdminGui;

public class RootLayoutController {

  private AdminGui adminGui;
  private ResourceBundle rb;

  public void setMainApp(AdminGui adminGui) {
      this.adminGui = adminGui;

      Locale locale = Locale.getDefault(); 
      this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.ui", locale);
  }  
  
  
  @FXML
  private void handleClose() {
      System.exit(0);
  }
  @FXML
  private void handleLanguage() {
	  adminGui.showLanguageSelection();
  }
  
}