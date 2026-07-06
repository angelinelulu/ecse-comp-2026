package com.kirbken.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;

public class ArenaController {
    
    @FXML private Region p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7;

private Region[] p1Segments;

@FXML
public void initialize() {
    p1Segments = new Region[]{p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7};
}

public void updateP1Health(int litSegments) {
    for (int i = 0; i < p1Segments.length; i++) {
        p1Segments[i].setStyle(i < litSegments
            ? "-fx-background-color: #ff4444; -fx-background-radius: 2;"
            : "-fx-background-color: #1a1a1a; -fx-background-radius: 2;");
    }
}

}
