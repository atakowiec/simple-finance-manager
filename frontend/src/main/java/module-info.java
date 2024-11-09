module pl.pollub.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires javafx.base;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
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

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    requires org.kordamp.ikonli.fontawesome;

    opens pl.pollub.frontend to javafx.fxml;
    exports pl.pollub.frontend;
    exports pl.pollub.frontend.model.transaction;
    exports pl.pollub.frontend.model.group;
    exports pl.pollub.frontend.user;
    exports pl.pollub.frontend.enums;
    exports pl.pollub.frontend.controller.group.transaction.dto;

    // open controllers to fxml
    exports pl.pollub.frontend.service;
    opens pl.pollub.frontend.service to javafx.fxml;

    exports pl.pollub.frontend.controller;
    opens pl.pollub.frontend.controller to javafx.fxml;

    exports pl.pollub.frontend.controller.group;
    opens pl.pollub.frontend.controller.group to javafx.fxml;

    exports pl.pollub.frontend.controller.home;
    opens pl.pollub.frontend.controller.home to javafx.fxml;

    exports pl.pollub.frontend.controller.home.list;
    opens pl.pollub.frontend.controller.home.list to javafx.fxml;

    exports pl.pollub.frontend.controller.home.add;
    opens pl.pollub.frontend.controller.home.add to javafx.fxml;

    exports pl.pollub.frontend.controller.group.add;
    opens pl.pollub.frontend.controller.group.add to javafx.fxml;

    exports pl.pollub.frontend.controller.group.add.list;
    opens pl.pollub.frontend.controller.group.add.list to javafx.fxml;

    exports pl.pollub.frontend.controller.group.transaction;
    opens pl.pollub.frontend.controller.group.transaction to javafx.fxml;

    exports pl.pollub.frontend.controller.group.member;
    opens pl.pollub.frontend.controller.group.member to javafx.fxml;

    exports pl.pollub.frontend.controller.group.search;
    opens pl.pollub.frontend.controller.group.search to javafx.fxml, com.google.gson;

    exports pl.pollub.frontend.controller.component;
    opens pl.pollub.frontend.controller.component to javafx.fxml;

    exports pl.pollub.frontend.controller.group.edit;
    opens pl.pollub.frontend.controller.group.edit to javafx.fxml;

    // open to gson
    opens pl.pollub.frontend.model.transaction to com.google.gson;
    opens pl.pollub.frontend.model.group to com.google.gson;
    opens pl.pollub.frontend.enums to com.google.gson;
    opens pl.pollub.frontend.controller.group.transaction.dto to com.google.gson;
}