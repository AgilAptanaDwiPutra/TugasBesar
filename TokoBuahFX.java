package com.example.tugasproglan;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class TokoBuahFX extends Application {

    private TextField inputTextField;
    private TextArea resultTextArea;
    private TreeMap<String, Integer> TokoBuah;
    private boolean pencarianSelesai = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Pengecekkan Stok Buah");
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);

        BorderPane borderPane = new BorderPane();

        // pencarian
        VBox searchBox = new VBox(10);
        searchBox.setPadding(new Insets(10, 10, 10, 10));

        Label searchLabel = new Label("Cari buah: ");
        inputTextField = new TextField();
        inputTextField.setOnAction(e -> cariStok());

        Button searchButton = new Button("Cari");
        searchButton.setOnAction(e -> cariStok());

        searchBox.getChildren().addAll(searchLabel, inputTextField, searchButton);

        borderPane.setLeft(searchBox);

        FlowPane topPane = new FlowPane();
        topPane.setPadding(new Insets(10, 10, 10, 10));
        topPane.setHgap(10);

        topPane.getChildren().add(new Label("Masukkan nama buah: "));

        TextField inputTextFieldTop = new TextField();
        inputTextFieldTop.setOnAction(e -> pencarianSelesai = false);

        topPane.getChildren().add(inputTextFieldTop);

        Button searchButtonTop = new Button("Cari");
        searchButtonTop.setOnAction(e -> cariStok());
        topPane.getChildren().add(searchButtonTop);

        borderPane.setTop(topPane);

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);
        borderPane.setCenter(resultTextArea);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.setHgap(10);

        Button addStockButton = new Button("Tambah Stok");
        addStockButton.setOnAction(e -> tambahStok());

        Button deleteStockButton = new Button("Hapus Stok");
        deleteStockButton.setOnAction(e -> hapusStok());

        Button addFruitButton = new Button("Tambah Buah");
        addFruitButton.setOnAction(e -> tambahBuah());

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addStockButton, deleteStockButton, addFruitButton);

        Button exitButton = new Button("Keluar");
        exitButton.setOnAction(e -> exitProgram());

        bottomPane.getChildren().addAll(buttonBox, exitButton);

        borderPane.setBottom(bottomPane);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Toko Buah Aneka Jaya");
        menuBar.getMenus().add(fileMenu);

        borderPane.setTop(menuBar);

        loadData();

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void cariStok() {
        if (!pencarianSelesai) {
            String pilihanBuah = inputTextField.getText();
            if (TokoBuah.containsKey(pilihanBuah)) {
                resultTextArea.setText("Stok " + pilihanBuah + " ada " + TokoBuah.get(pilihanBuah));
            } else {
                resultTextArea.setText("Maaf, stok " + pilihanBuah + " tidak ditemukan");
            }
        }
    }

    private void tambahBuah() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tambah Buah Baru");
        dialog.setHeaderText("Masukkan nama buah baru:");
        dialog.setContentText("Nama Buah:");

        dialog.showAndWait().ifPresent(namaBuah -> {
            if (!namaBuah.trim().isEmpty()) {
                if (!TokoBuah.containsKey(namaBuah)) {
                    TextInputDialog stokDialog = new TextInputDialog("0");
                    stokDialog.setTitle("Tambah Stok");
                    stokDialog.setHeaderText("Tambah jumlah stok untuk " + namaBuah + ":");
                    stokDialog.setContentText("Jumlah Stok:");

                    stokDialog.showAndWait().ifPresent(stokString -> {
                        try {
                            int stok = Integer.parseInt(stokString);
                            TokoBuah.put(namaBuah, stok);
                            updateResultTextArea();
                            saveData();
                        } catch (NumberFormatException e) {
                            showMessage("Masukkan jumlah stok dalam format angka!!", "Error!");
                        }
                    });
                } else {
                    showMessage("Buah dengan nama " + namaBuah + " sudah ada.", "Error!");
                }
            } else {
                showMessage("Masukkan nama buah terlebih dahulu.", "Error!");
            }
        });
    }

    private void tambahStok() {

        String namaBuah = inputTextField.getText();
        if (!namaBuah.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog("0");
            dialog.setTitle("Tambah Stok");
            dialog.setHeaderText("Tambah jumlah stok untuk " + namaBuah + ":");
            dialog.setContentText("Jumlah Stok:");

            dialog.showAndWait().ifPresent(stokString -> {
                try {
                    int stok = Integer.parseInt(stokString);
                    if (TokoBuah.containsKey(namaBuah)) {
                        int jumlahStokSebelumnya = TokoBuah.get(namaBuah);
                        TokoBuah.put(namaBuah, jumlahStokSebelumnya + stok);
                    } else {
                        TokoBuah.put(namaBuah, stok);
                    }
                    updateResultTextArea();
                    saveData();
                } catch (NumberFormatException e) {
                    showMessage("Masukkan jumlah stok dalam format angka!!", "Error!");
                }
            });
        } else {
            showMessage("Masukkan nama buah terlebih dahulu.", "Error!");
        }
    }

    private void hapusStok() {

        String namaBuah = inputTextField.getText();
        if (!namaBuah.isEmpty()) {
            if (TokoBuah.containsKey(namaBuah)) {
                int stokSebelumnya = TokoBuah.get(namaBuah);
                TextInputDialog dialog = new TextInputDialog("0");
                dialog.setTitle("Hapus Stok");
                dialog.setHeaderText("Jumlah stok buah yang ingin dihapus dari Buah " + namaBuah + ":");
                dialog.setContentText("Jumlah Stok:");

                dialog.showAndWait().ifPresent(stokString -> {
                    try {
                        int stok = Integer.parseInt(stokString);
                        if (stok <= stokSebelumnya) {
                            TokoBuah.put(namaBuah, stokSebelumnya - stok);
                            updateResultTextArea();
                            saveData();
                        } else {
                            showMessage("Jumlah stok yang dihapus melebihi stok saat ini.", "Error");
                        }
                    } catch (NumberFormatException e) {
                        showMessage("Masukkan jumlah stok dalam format angka!", "Error!");
                    }
                });
            } else {
                showMessage("Buah dengan nama " + namaBuah + " tidak ditemukan.", "Error!");
            }
        } else {
            showMessage("Masukkan nama buah terlebih dahulu.", "Error!");
        }
    }

    private void exitProgram() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Exit");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda ingin keluar?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveData();
            System.exit(0);
        }
    }

    private void loadData() {
        TokoBuah = new TreeMap<>();
        TokoBuah.put("Apel", 50);
        TokoBuah.put("Mangga", 75);
        TokoBuah.put("Jeruk", 100);
        TokoBuah.put("Melon", 25);
        TokoBuah.put("Semangka", 20);
        TokoBuah.put("Alpukat", 30);
        TokoBuah.put("Pepaya", 25);
        TokoBuah.put("Nanas", 30);
        TokoBuah.put("Jambu", 30);
        TokoBuah.put("Anggur", 40);
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter("TokoBuah.txt")) {
            for (Map.Entry<String, Integer> entry : TokoBuah.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateResultTextArea() {
        if (pencarianSelesai) {
            resultTextArea.setText("");
            for (Map.Entry<String, Integer> entry : TokoBuah.entrySet()) {
                resultTextArea.appendText(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        }
    }

    private void showMessage(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
