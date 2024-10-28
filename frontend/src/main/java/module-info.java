module pl.pollub.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires static lombok;
    requires java.desktop;
    requires org.reflections;
    requires net.bytebuddy;
    requires java.prefs;
    requires jdk.jfr;

    opens pl.pollub.frontend to javafx.fxml;
    exports pl.pollub.frontend;
    exports pl.pollub.frontend.controller;
    opens pl.pollub.frontend.controller to javafx.fxml;
    exports pl.pollub.frontend.service;
    opens pl.pollub.frontend.service to javafx.fxml;
    exports pl.pollub.frontend.controller.home;
    opens pl.pollub.frontend.controller.home to javafx.fxml;
    exports pl.pollub.frontend.model.transaction;
    opens pl.pollub.frontend.model.transaction to com.google.gson;
    exports pl.pollub.frontend.controller.home.transaction;
    opens pl.pollub.frontend.controller.home.transaction to javafx.fxml;
    exports pl.pollub.frontend.controller.home.add;
    opens pl.pollub.frontend.controller.home.add to javafx.fxml;
    exports pl.pollub.frontend.user;
    exports pl.pollub.frontend.controller.home.add.list;
    opens pl.pollub.frontend.controller.home.add.list to javafx.fxml;
}