package pl.pollub.frontend.controller.group.transaction;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import pl.pollub.frontend.annotation.PostInitialize;
import pl.pollub.frontend.annotation.ViewParameter;
import pl.pollub.frontend.controller.group.transaction.dto.ImportExportDto;
import pl.pollub.frontend.event.EventEmitter;
import pl.pollub.frontend.event.EventType;
import pl.pollub.frontend.injector.Inject;
import pl.pollub.frontend.service.AuthService;
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.service.TransactionService;
import pl.pollub.frontend.util.JsonUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImportExportController {
    @ViewParameter("groupId")
    private Long groupId;
    @ViewParameter("type")
    private String type;

    @Inject
    private ModalService modalService;
    @Inject
    private TransactionService transactionService;
    @Inject
    private AuthService authService;
    @Inject
    private EventEmitter eventEmitter;

    @FXML
    private Label title;
    @FXML
    private Button acceptButton;
    @FXML
    public TextField content;
    @FXML
    public Label errorLabel;

    private File selectedFile;


    @PostInitialize
    public void postInitialize() {
        if (!type.equals("import") && !type.equals("export")) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        title.setText(isImport() ? "Import" : "Export");
        acceptButton.setText(isImport() ? "Import" : "Export");

        updateSelectedFileLabel();
    }

    private boolean isImport() {
        return type.equals("import");
    }

    private void updateSelectedFileLabel() {
        content.setText(selectedFile != null ? selectedFile.getAbsolutePath() : (isImport() ? "Wybierz plik" : "Wybierz folder"));
    }

    public void accept() {
        if(selectedFile == null) {
            errorLabel.setText(isImport() ? "Wybierz plik" : "Wybierz folder");
            return;
        }

        if (isImport()) {
            importData();
        } else {
            exportData();
        }

        errorLabel.setText(isImport() ? "Import zakończony" : "Export zakończony");
        selectedFile = null;
        updateSelectedFileLabel();
    }

    public void deny() {
        modalService.hideModal();
    }

    private void importData() {
        if (selectedFile == null)
            throw new IllegalArgumentException("Selected file is null");

        ImportExportDto dto;
        try {
            String content = Files.readString(selectedFile.toPath(), Charset.defaultCharset());

            dto = JsonUtil.GSON.fromJson(content, ImportExportDto.class);
        } catch (Exception e) {
            errorLabel.setText("Błąd podczas wczytywania pliku");
            throw new RuntimeException(e);
        }

        HttpResponse<String> response = transactionService.importTransactions(groupId, dto);
        if(response.statusCode() != 200) {
            errorLabel.setText("Błąd podczas importu danych");
            return;
        }

        eventEmitter.emit(EventType.TRANSACTION_UPDATE);
    }

    private void exportData() {
        if (!selectedFile.isDirectory())
            throw new IllegalArgumentException("Selected file is not a directory");

        ImportExportDto dto = new ImportExportDto();
        dto.setExportedBy(authService.getUser().getUsername());
        dto.setIncomes(transactionService.fetchIncomes(groupId));
        dto.setExpenses(transactionService.fetchExpenses(groupId));

        String json = JsonUtil.GSON.toJson(dto);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String filename = "transactions-" + LocalDateTime.now().format(formatter) + ".json";

        File targetFile = new File(this.selectedFile, filename);

        try (FileWriter fileWriter = new FileWriter(targetFile.getAbsolutePath())) {
            fileWriter.write(json);
        } catch (IOException e) {
            errorLabel.setText("Błąd podczas zapisywania pliku");
            e.printStackTrace();
        }
    }

    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik");

        selectedFile = isImport() ? selectImportFile() : selectExportDirectory();
        updateSelectedFileLabel();
    }

    private File selectImportFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));

        return fileChooser.showOpenDialog(null);
    }

    private File selectExportDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Wybierz folder");

        return directoryChooser.showDialog(null);
    }
}
