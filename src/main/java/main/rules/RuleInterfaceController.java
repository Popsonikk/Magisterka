package main.rules;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.InterfaceTemplate;

import java.net.URL;
import java.util.ResourceBundle;

public class RuleInterfaceController extends InterfaceTemplate implements Initializable {
    private RuleManager ruleManager;

    public void setRuleManager(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    @Override
    //funkcja inicjująca interfejs
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
        //tworzenie elementów interfejsu
        createSelectSizeBox();
        createSwitchPageBox();
        createFiltrButton();
        createHeader();
        //edycja przycisku przewijającego strony do przodu
        Button nextButton=(Button) switchPageBox.lookup("#nButt");
        nextButton.setOnAction(e -> {
            if((startId+boxSize>= ruleManager.getRuleListSize())||(filtered&&startId+boxSize>= ruleManager.getFilteredRuleListSize()))
                return;
            startId+=boxSize;
            createView();
        });
    }

    @Override
    protected void deleteRows() {

    }

    @Override
    protected void clearFilter() {

    }

    @Override
    protected void createView() {

    }

    @Override
    protected void createFiltrButton() {

    }

    @Override
    protected void createHeader() {

    }

    @Override
    public void clearBase() {

    }
}
