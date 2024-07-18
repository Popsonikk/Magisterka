package main;


import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindowController {
    private BasketManager basketManager;

    public void setBasketManager(BasketManager basketManager) {
        this.basketManager = basketManager;
    }

    public void loadBaskets() {

        basketManager.loadBaskets();


    }

    public void showBaskets() {
        basketManager.showBaskets();
    }
}
