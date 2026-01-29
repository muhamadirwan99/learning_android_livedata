# MyLiveData - Aplikasi Timer dengan LiveData

Aplikasi Android sederhana yang mendemonstrasikan penggunaan **LiveData** dan **ViewModel** untuk membuat timer yang dapat bertahan saat terjadi perubahan konfigurasi (seperti rotasi layar).

## 📱 Fitur Utama

- **Timer Otomatis**: Menghitung waktu yang berlalu sejak aplikasi dibuka
- **Tahan Rotasi**: Timer tidak reset saat layar diputar
- **LiveData Pattern**: Menggunakan observer pattern untuk update UI otomatis
- **ViewModel**: Data timer dikelola dengan lifecycle-aware component

## 🏗️ Arsitektur

Aplikasi ini menggunakan arsitektur **MVVM (Model-View-ViewModel)** dengan komponen Android Architecture:

```
┌─────────────────┐
│   MainActivity  │ ◄─── View Layer (UI)
│  (View/UI)      │
└────────┬────────┘
         │ observe()
         │
┌────────▼────────┐
│  MainViewModel  │ ◄─── ViewModel Layer
│   (LiveData)    │
└────────┬────────┘
         │
┌────────▼────────┐
│   Timer Task    │ ◄─── Logic Layer
└─────────────────┘
```

## 📂 Struktur Kode

### 1. **MainViewModel.kt**
ViewModel yang mengelola logika timer dan data.

**Komponen Utama:**
- `mInitialTime`: Waktu awal sebagai referensi perhitungan
- `mElapsedTime`: MutableLiveData yang menyimpan waktu yang berlalu
- `Timer`: Background thread untuk update timer setiap detik
- `getElapsedTime()`: Fungsi public untuk mengekspos LiveData ke UI

**Mengapa ViewModel?**
- Data bertahan saat rotasi layar
- Memisahkan logika bisnis dari UI
- Lifecycle-aware (otomatis dibersihkan saat tidak dibutuhkan)

### 2. **MainActivity.kt**
Activity yang menampilkan UI dan mengobservasi data dari ViewModel.

**Komponen Utama:**
- `binding`: View Binding untuk akses view yang type-safe
- `liveDataTimerViewModel`: Instance dari MainViewModel
- `subscribe()`: Mendaftarkan Observer untuk menerima update dari LiveData

**Mengapa Observer Pattern?**
- Update UI otomatis saat data berubah
- Lifecycle-aware (observer berhenti saat Activity destroyed)
- Menghindari memory leak

## 🔄 Flow Data

```
1. MainViewModel dibuat → Timer dimulai
2. Setiap 1 detik → Timer menghitung elapsed time
3. postValue() → Update MutableLiveData
4. LiveData berubah → Observer di MainActivity dipanggil
5. Observer update → TextView di UI diupdate
```

## 🎯 Konsep Penting

### LiveData vs MutableLiveData

- **MutableLiveData**: Digunakan di dalam ViewModel, bisa diubah nilainya
- **LiveData**: Diekspos ke UI, read-only untuk mencegah UI mengubah data

```kotlin
// Di ViewModel (private, bisa diubah)
private val mElapsedTime = MutableLiveData<Long?>()

// Diekspos ke UI (public, read-only)
fun getElapsedTime(): LiveData<Long?> {
    return mElapsedTime
}
```

### setValue() vs postValue()

- **setValue()**: Digunakan di **main thread**
- **postValue()**: Digunakan di **background thread** (seperti dalam Timer)

```kotlin
// Karena TimerTask berjalan di background thread
mElapsedTime.postValue(newValue) // ✅ Correct
// mElapsedTime.setValue(newValue) // ❌ Crash!
```

### SystemClock.elapsedRealtime()

Menggunakan `elapsedRealtime()` bukan `currentTimeMillis()` karena:
- Tetap akurat meski device sleep
- Tidak terpengaruh perubahan waktu sistem
- Ideal untuk mengukur interval waktu

## 🛠️ Teknologi yang Digunakan

- **Language**: Kotlin
- **Min SDK**: Android 5.0 (API 21)
- **Architecture Components**:
  - LiveData
  - ViewModel
  - ViewModelProvider
- **View Binding**: Untuk akses view yang type-safe
- **Java Timer**: Untuk background task scheduling

## 📖 Cara Kerja Detail

### Saat Aplikasi Pertama Kali Dibuka:

1. `MainActivity` dibuat dan `onCreate()` dipanggil
2. `ViewModelProvider` membuat instance `MainViewModel` (atau mengambil yang sudah ada)
3. `MainViewModel.init{}` dijalankan:
   - Menyimpan waktu awal (`mInitialTime`)
   - Membuat Timer yang berjalan setiap 1 detik
4. `subscribe()` mendaftarkan Observer ke LiveData
5. Timer mulai update nilai setiap detik
6. Observer menerima update dan mengubah TextView

### Saat Layar Dirotasi:

1. `MainActivity` lama di-destroy
2. `MainActivity` baru dibuat
3. **`MainViewModel` TIDAK dibuat ulang** (masih instance yang sama)
4. Timer tetap berjalan tanpa reset
5. `subscribe()` mendaftarkan Observer baru
6. Observer langsung menerima nilai terakhir dari LiveData
7. Timer terus berjalan dari nilai sebelumnya

## 🎓 Pembelajaran

Aplikasi ini mengajarkan:

1. **Separation of Concerns**: UI tidak tahu bagaimana data dihasilkan
2. **Observer Pattern**: Update otomatis tanpa polling manual
3. **Lifecycle Management**: Mencegah memory leak dengan lifecycle-aware components
4. **Configuration Changes**: Menangani rotasi layar dengan benar
5. **Threading**: Background task dengan postValue()

## 📝 Catatan Pengembangan

### Potensi Improvement:

1. **Memory Leak Prevention**: 
   - Saat ini Timer tidak pernah di-cancel
   - Sebaiknya override `onCleared()` di ViewModel untuk membersihkan Timer

   ```kotlin
   override fun onCleared() {
       super.onCleared()
       timer?.cancel() // Batalkan timer saat ViewModel destroyed
   }
   ```

2. **Modern Approach**:
   - Pertimbangkan menggunakan Kotlin Coroutines + Flow
   - Lebih idiomatis untuk Kotlin dan lebih mudah di-test

3. **Testing**:
   - Tambahkan unit test untuk ViewModel
   - Gunakan `InstantTaskExecutorRule` untuk testing LiveData

## 🚀 Cara Menjalankan

1. Clone repository ini
2. Buka dengan Android Studio
3. Sync Gradle
4. Run aplikasi di emulator atau device fisik
5. Coba rotasi layar untuk melihat timer tetap berjalan

## 📚 Referensi

- [Android LiveData Documentation](https://developer.android.com/topic/libraries/architecture/livedata)
- [Android ViewModel Documentation](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Guide to App Architecture](https://developer.android.com/topic/architecture)

---

**Dibuat untuk pembelajaran Android Architecture Components** 📱✨
