/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author atmaf
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class APK extends javax.swing.JFrame {

    /**
     * Creates new form APK
     */
    Connection conn;
    DefaultListModel<String> listModel;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane scrollPane;
    private Connection connection;

    public APK() {
        initComponents();
        connectToDatabase();
        createTableIfNotExists();
        loadContacts();

        simpan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveContact();
            }
        });

        cari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchContact();
            }
        });
        apdet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateContact();
            }
        });

        del.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteContact();
            }
        });

        keluar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        importBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Pilih File CSV");
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        importFromCSV(selectedFile.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });

    }

    public static void saveToDatabase(String nama, String nomorTelepon, String kategori) {

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kontak.db");) {
            String query = "INSERT INTO kontak (nama, nomortelepon, kategori) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nama);
                stmt.setString(2, nomorTelepon);
                stmt.setString(3, kategori);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Data berhasil disimpan: " + nama + ", " + nomorTelepon + ", " + kategori);
                }
            } catch (SQLException e) {
                System.out.println("Terjadi kesalahan saat menyimpan data: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Koneksi database gagal: " + e.getMessage());
        }
    }

    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:kontak.db");
            if (connection != null) {
                System.out.println("Database Connected");
            } else {
                System.out.println("Tidak dapat menghubungkan ke database.");
            }
        } catch (SQLException e) {
            System.out.println("Error koneksi database: " + e.getMessage());
        }
    }

    public void createTableIfNotExists() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS kontak ("
                    + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Nama TEXT, "
                    + "NomorTelepon TEXT, "
                    + "Kategori TEXT);";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Tabel kontak sudah dibuat atau sudah ada.");
        } catch (SQLException e) {
            System.out.println("Terjadi kesalahan saat membuat tabel: " + e.getMessage());
        }
    }

    public void loadContacts() {
        if (connection == null) {
            System.out.println("Koneksi database null!");
            return;
        }

        listModel = new DefaultListModel<>();
        jList1.setModel(listModel);
        try {
            String sql = "SELECT * FROM kontak";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (!resultSet.isBeforeFirst()) {
                System.out.println("Tidak ada data kontak.");
                return;
            }

            while (resultSet.next()) {
                String nama = resultSet.getString("Nama");
                String nomorTelepon = resultSet.getString("NomorTelepon");
                String kategori = resultSet.getString("Kategori");
                listModel.addElement(nama + " - " + nomorTelepon + " - " + kategori);
            }
        } catch (SQLException e) {
            System.out.println("Terjadi kesalahan saat memuat kontak: " + e.getMessage());
        }
    }

    private void saveContact() {
        if (!validateInput()) {
            return;
        }
        try {

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO kontak (Nama, NomorTelepon, Kategori) VALUES (?, ?, ?)");
            stmt.setString(1, nama.getText());
            stmt.setString(2, nomor.getText());
            stmt.setString(3, kategori.getSelectedItem().toString());
            stmt.executeUpdate();
            loadContacts();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat menyimpan kontak: " + e.getMessage());
        }
    }

    private void updateContact() {
        if (!validateInput()) {
            return;
        }
        int selectedIndex = jList1.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedContact = jList1.getSelectedValue();
            String[] parts = selectedContact.split(" - ");
            String nama = parts[0];
            String nomorTelepon = parts[1];

            try {
                PreparedStatement stmt = connection.prepareStatement("UPDATE kontak SET Nama=?, NomorTelepon=?, Kategori=? WHERE Nama=? AND NomorTelepon=?");
                stmt.setString(1, this.nama.getText());
                stmt.setString(2, this.nomor.getText());
                stmt.setString(3, kategori.getSelectedItem().toString());
                stmt.setString(4, nama);
                stmt.setString(5, nomorTelepon);
                stmt.executeUpdate();
                loadContacts();
                JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saat memperbarui kontak.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.");
        }
    }

    private void deleteContact() {
        int selectedIndex = jList1.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedContact = jList1.getSelectedValue();
            String[] parts = selectedContact.split(" - ");
            String nama = parts[0];
            String nomorTelepon = parts[1];

            try {
                PreparedStatement stmt = connection.prepareStatement("DELETE FROM kontak WHERE Nama=? AND NomorTelepon=?");
                stmt.setString(1, nama);
                stmt.setString(2, nomorTelepon);
                stmt.executeUpdate();
                loadContacts();
                JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saat menghapus kontak.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.");
        }
    }

    private void searchContact() {
        String searchTerm = nama.getText();
        listModel.clear();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM kontak WHERE Nama LIKE ? OR NomorTelepon LIKE ?");
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listModel.addElement(rs.getString("Nama") + " - " + rs.getString("NomorTelepon") + " - " + rs.getString("Kategori"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat mencari kontak.");
        }
    }

    private boolean validateInput() {
        if (nama.getText().isEmpty() || nomor.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Nomor Telepon harus diisi.");
            return false;
        }
        if (!nomor.getText().matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor Telepon hanya boleh berisi angka.");
            return false;
        }
        return true;
    }

    private void exportToCSV() {
        try (PrintWriter writer = new PrintWriter(new File("kontak.csv"))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listModel.getSize(); i++) {
                sb.append(listModel.getElementAt(i)).append("\n");
            }
            writer.write(sb.toString());
            JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke CSV.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void importFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {

                System.out.println("Baris " + lineNumber + ": " + line);

                String[] columns = line.split(" - ");

                System.out.println("Jumlah kolom pada baris " + lineNumber + ": " + columns.length);

                if (columns.length != 3) {
                    System.out.println("Format baris tidak sesuai pada baris " + lineNumber);
                    lineNumber++;
                    continue;
                }

                String nama = columns[0].trim();
                String nomorTelepon = columns[1].trim();
                String kategori = columns[2].trim();

                saveToDatabase(nama, nomorTelepon, kategori);
                lineNumber++;
            }
            System.out.println("Data CSV berhasil diimpor.");
        } catch (IOException e) {
            System.out.println("Error membaca file CSV: " + e.getMessage());
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        nama = new javax.swing.JTextField();
        kategori = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        simpan = new javax.swing.JButton();
        nomor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cari = new javax.swing.JButton();
        apdet = new javax.swing.JButton();
        del = new javax.swing.JButton();
        keluar = new javax.swing.JButton();
        importBtn = new javax.swing.JButton();
        export = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel1.setText("APLIKASI PENGELOLA KONTAK");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(268, 268, 268))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "keluarga", "teman", "kerja" }));

        jScrollPane1.setViewportView(jList1);

        simpan.setBackground(new java.awt.Color(0, 204, 0));
        simpan.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        simpan.setText("SIMPAN");

        jLabel2.setText("NAMA");

        jLabel3.setText("NOMOR");

        jLabel4.setText("KATEGORI");

        cari.setBackground(new java.awt.Color(51, 204, 0));
        cari.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        cari.setText("CARI");

        apdet.setBackground(new java.awt.Color(153, 204, 0));
        apdet.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        apdet.setText("PERBARUI");

        del.setBackground(new java.awt.Color(204, 204, 0));
        del.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        del.setText("HAPUS");

        keluar.setBackground(new java.awt.Color(204, 0, 0));
        keluar.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        keluar.setText("KELUAR");

        importBtn.setBackground(new java.awt.Color(102, 204, 0));
        importBtn.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        importBtn.setText("IMPOR");

        export.setBackground(new java.awt.Color(153, 204, 0));
        export.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        export.setText("EKSPOR");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kategori, 0, 129, Short.MAX_VALUE)
                            .addComponent(nomor)
                            .addComponent(nama))
                        .addGap(39, 39, 39)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(simpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cari)
                        .addGap(18, 18, 18)
                        .addComponent(apdet)
                        .addGap(18, 18, 18)
                        .addComponent(del)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(223, 223, 223)
                        .addComponent(importBtn)
                        .addGap(18, 18, 18)
                        .addComponent(export))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(301, 301, 301)
                        .addComponent(keluar)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nomor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(kategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(simpan)
                    .addComponent(cari)
                    .addComponent(apdet)
                    .addComponent(del))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importBtn)
                    .addComponent(export))
                .addGap(26, 26, 26)
                .addComponent(keluar)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver ditemukan");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver tidak ditemukan");
            e.printStackTrace();
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(APK.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(APK.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(APK.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(APK.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new APK().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton apdet;
    private javax.swing.JButton cari;
    private javax.swing.JButton del;
    private javax.swing.JButton export;
    private javax.swing.JButton importBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> kategori;
    private javax.swing.JButton keluar;
    private javax.swing.JTextField nama;
    private javax.swing.JTextField nomor;
    private javax.swing.JButton simpan;
    // End of variables declaration//GEN-END:variables
}
