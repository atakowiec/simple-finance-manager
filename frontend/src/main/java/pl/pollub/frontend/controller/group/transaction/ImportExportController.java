package pl.pollub.frontend.controller.group.transaction;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import pl.pollub.frontend.service.ModalService;
import pl.pollub.frontend.service.TransactionService;
import pl.pollub.frontend.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private EventEmitter eventEmitter;

    @FXML
    private Label title;
    @FXML
    private Button acceptButton;
    @FXML
    private ComboBox<String> formatPicker;
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

        if (isImport()) {
            formatPicker.setManaged(false);
            formatPicker.setVisible(false);
        } else {
            formatPicker.getItems().setAll("json", "csv");
            formatPicker.getSelectionModel().select("json");
        }

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

        boolean operationSucceeded = isImport() ? importData() : exportData();
        if (!operationSucceeded) {
            return;
        }

        errorLabel.setText(isImport() ? "Import zakończony" : "Export zakończony");
        selectedFile = null;
        updateSelectedFileLabel();
    }

    public void deny() {
        modalService.hideModal();
    }

    private boolean importData() {
        if (selectedFile == null)
            throw new IllegalArgumentException("Selected file is null");

        ImportExportDto dto;
        try {
            String content = Files.readString(selectedFile.toPath(), Charset.defaultCharset());
            dto = JsonUtil.GSON.fromJson(content, ImportExportDto.class);
        } catch (Exception e) {
            errorLabel.setText("Błąd podczas wczytywania pliku");
            return false;
        }

        HttpResponse<String> response = transactionService.importTransactions(groupId, dto);
        if(response.statusCode() != 200) {
            errorLabel.setText("Błąd podczas importu danych");
            return false;
        }

        eventEmitter.emit(EventType.TRANSACTION_UPDATE);
        return true;
    }

    private boolean exportData() {
        if (!selectedFile.isDirectory())
            throw new IllegalArgumentException("Selected file is not a directory");

        String format = getSelectedExportFormat();
        HttpResponse<byte[]> response = transactionService.exportTransactions(groupId, format);

        if (response.statusCode() != 200) {
            errorLabel.setText("Błąd podczas eksportu danych");
            return false;
        }

        String filename = extractFilename(response).orElseGet(() -> defaultFilename(format));
        File targetFile = new File(this.selectedFile, filename);

        try {
            Files.write(targetFile.toPath(), response.body());
            return true;
        } catch (IOException e) {
            errorLabel.setText("Błąd podczas zapisywania pliku");
            return false;
        }
    }

    private String getSelectedExportFormat() {
        String selectedFormat = formatPicker.getValue();
        if (selectedFormat == null || selectedFormat.isBlank()) {
            return "json";
        }

        return selectedFormat.toLowerCase(Locale.ROOT);
    }

    private Optional<String> extractFilename(HttpResponse<byte[]> response) {
        String contentDisposition = response.headers().firstValue("Content-Disposition").orElse("");
        Matcher matcher = Pattern.compile("filename=\"?([^\";]+)\"?").matcher(contentDisposition);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }

    private String defaultFilename(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "transactions-" + LocalDateTime.now().format(formatter) + "." + format;
    }

    public void selectFile() {
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
