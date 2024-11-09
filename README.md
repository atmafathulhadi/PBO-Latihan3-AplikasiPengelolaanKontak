
# Aplikasi Pengelolaan Kontak

**Tugas Pemrograman GUI**  
Nama: Atma Fathul Hadi  
NPM: 2210010425  

## 1. Deskripsi Program
Aplikasi ini adalah program berbasis GUI menggunakan Java yang memungkinkan pengguna untuk mengelola kontak. Pengguna dapat menambahkan, mencari, memperbarui, menghapus, serta menyimpan kontak. Aplikasi ini juga memiliki fitur impor dan ekspor kontak untuk kemudahan pengelolaan data.

## 2. Komponen GUI
Aplikasi ini menggunakan komponen GUI berikut:

- **JFrame**: Sebagai jendela utama aplikasi.
- **JTable**: Untuk menampilkan daftar kontak yang tersimpan.
- **JPanel**: Untuk mengatur layout dan grup komponen.
- **JLabel**: Menampilkan label teks, seperti judul aplikasi dan label input.
- **JTextField**: Tempat untuk memasukkan informasi kontak, seperti nama dan nomor telepon, serta pencarian.
- **JButton**: Tombol untuk menambah, menghapus, memperbarui kontak, menyimpan, mencari, mengimpor, dan mengekspor data kontak.
- **JScrollPane**: Menyediakan area gulir untuk tabel kontak.
- **JOptionPane**: Menampilkan pesan pop-up seperti konfirmasi penghapusan atau pesan error jika terjadi kesalahan.

## 3. Logika Program
Program ini menghubungkan ke basis data dan memungkinkan pengguna untuk:

- **Menambah Kontak**: Menyimpan data kontak baru ke basis data.
- **Mencari Kontak**: Menampilkan kontak berdasarkan nama yang dicari.
- **Memperbarui Kontak**: Mengubah informasi kontak yang sudah ada.
- **Menghapus Kontak**: Menghapus kontak yang dipilih dari daftar.
- **Menyimpan Data**: Menyimpan data kontak yang telah dimodifikasi ke basis data.
- **Mengimpor Kontak**: Mengimpor data kontak dari file eksternal.
- **Mengekspor Kontak**: Mengekspor data kontak ke file eksternal.

### Kode Terkait:
- **Fungsi untuk Menambah Kontak**  
  Kode berikut digunakan untuk menambahkan kontak ke basis data:
  ```java
  public void addContact(String name, String phone) {
      try {
          String query = "INSERT INTO contacts (name, phone) VALUES (?, ?)";
          PreparedStatement pst = conn.prepareStatement(query);
          pst.setString(1, name);
          pst.setString(2, phone);
          pst.executeUpdate();
          loadContacts();
      } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, "Error saat menambahkan kontak", "Error", JOptionPane.ERROR_MESSAGE);
      }
  }
  ```

- **Event untuk Mencari Kontak**  
  Event ini melakukan pencarian berdasarkan nama:
  ```java
  searchButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          String keyword = searchField.getText();
          searchContacts(keyword);
      }
  });
  ```

- **Event untuk Memperbarui Kontak**  
  Menggunakan tombol "Perbarui" untuk memperbarui data kontak yang dipilih:
  ```java
  updateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          int selectedRow = table.getSelectedRow();
          if (selectedRow != -1) {
              updateContact(selectedRow);
          } else {
              JOptionPane.showMessageDialog(null, "Pilih kontak untuk diperbarui", "Error", JOptionPane.WARNING_MESSAGE);
          }
      }
  });
  ```

- **Event untuk Menghapus Kontak**  
  Tombol ini menghapus kontak yang dipilih dari tabel:
  ```java
  deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          int selectedRow = table.getSelectedRow();
          if (selectedRow != -1) {
              deleteContact(selectedRow);
          } else {
              JOptionPane.showMessageDialog(null, "Pilih kontak untuk dihapus", "Error", JOptionPane.WARNING_MESSAGE);
          }
      }
  });
  ```

- **Event untuk Mengimpor Kontak**  
  Mengimpor data kontak dari file eksternal:
  ```java
  importButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          importContacts();
      }
  });
  ```

- **Event untuk Mengekspor Kontak**  
  Mengekspor data kontak ke file eksternal:
  ```java
  exportButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          exportContacts();
      }
  });
  ```

- **Event untuk Menyimpan Kontak**  
  Menyimpan perubahan data ke basis data:
  ```java
  saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          saveContacts();
      }
  });
  ```

## 4. Cara Menjalankan Program
1. Buka program di IDE seperti NetBeans atau Eclipse.
2. Pastikan koneksi basis data telah dikonfigurasi.
3. Jalankan program.
4. Tambahkan kontak dengan memasukkan nama dan nomor telepon pada kolom input.
5. Klik "Tambah" untuk menambah kontak ke basis data.
6. Gunakan kolom pencarian untuk menemukan kontak berdasarkan nama.
7. Klik "Perbarui" untuk mengubah data kontak yang dipilih, atau "Hapus" untuk menghapusnya.
8. Klik "Impor" untuk mengimpor data kontak dari file, dan "Ekspor" untuk mengekspor data ke file eksternal.
9. Klik "Simpan" untuk menyimpan semua perubahan ke basis data.

## 5. Struktur Program
- **APK.java**: Berisi kode utama untuk aplikasi, termasuk komponen GUI, koneksi basis data, dan logika pemrosesan kontak.

Screenshot Hasil Program:
![1](https://github.com/atmafathulhadi/PBO-Latihan3-AplikasiPengelolaanKontak/blob/main/1.png)
![2](https://github.com/atmafathulhadi/PBO-Latihan3-AplikasiPengelolaanKontak/blob/main/2.png)
![3](https://github.com/atmafathulhadi/PBO-Latihan3-AplikasiPengelolaanKontak/blob/main/3.png)
![4](https://github.com/atmafathulhadi/PBO-Latihan3-AplikasiPengelolaanKontak/blob/main/4.png)

## 6. Indikator Penilaian
| No  | Komponen         |  Persentase  |
| :-: | ---------------- |   :-----:    |
|  1  | Komponen GUI     |    20%       |
|  2  | Logika Program   |    20%       |
|  3  | Events           |    15%       |
|  4  | Kesesuaian UI    |    15%       |
|  5  | Memenuhi Variasi |    30%       |
|     | **TOTAL**        |    100%      |

---

Aplikasi ini memberikan solusi pengelolaan kontak yang intuitif dengan fitur lengkap untuk penambahan, penghapusan, pencarian, pembaruan, dan penyimpanan kontak. Fitur impor dan ekspor memungkinkan pengguna untuk mengelola data dengan lebih fleksibel.
