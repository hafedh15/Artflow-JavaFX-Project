package tn.artflow.controllors;

import com.google.protobuf.BoolValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;
import tn.artflow.services.WorkshopService;

import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateWorkshop {

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private DatePicker date;

    @FXML
    private TextField txtdescription;

    @FXML
    private TextField txtimage;

    @FXML
    private TextField txtlocation;

    @FXML
    private TextField txttitle;

    @FXML
    void choosefile(ActionEvent event) {

    }

    private Workshop currentWorkshop;

    public void setWorkshop(Workshop workshop) {
        this.currentWorkshop = workshop;

        // Pre-fill form
        txttitle.setText(workshop.getTitle());
        txtdescription.setText(workshop.getDescription());
        txtlocation.setText(workshop.getLocation());
        txtimage.setText(workshop.getImage());
        comboType.getItems().addAll("Online", "In Person");
        comboType.setValue(workshop.getType());


    }

    @FXML
    void updadeWorkshop(ActionEvent event) {
        currentWorkshop.setTitle(txttitle.getText());
        currentWorkshop.setDescription(txtdescription.getText());
        currentWorkshop.setLocation(txtlocation.getText());
        currentWorkshop.setImage(txtimage.getText());
        currentWorkshop.setType(comboType.getValue());
        currentWorkshop.setDate(date.getValue().toString());

        try {
            WorkshopService ws = new WorkshopService();
            ws.modifier(currentWorkshop);
            ((Stage) txttitle.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
