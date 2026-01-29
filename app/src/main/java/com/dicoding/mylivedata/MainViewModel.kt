package com.dicoding.mylivedata

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import java.util.TimerTask

// ViewModel untuk mengelola data timer yang bertahan saat konfigurasi berubah (rotasi layar)
class MainViewModel : ViewModel() {

    companion object {
        // Konstanta untuk interval 1 detik dalam milidetik
        private const val ONE_SECOND = 1000
    }

    // Menyimpan waktu awal saat ViewModel dibuat, sebagai titik referensi perhitungan
    // Menggunakan elapsedRealtime() agar timer tetap akurat meski device sleep
    private val mInitialTime = SystemClock.elapsedRealtime()

    // MutableLiveData untuk menyimpan waktu yang berlalu (elapsed time)
    // Bersifat mutable agar bisa diupdate dari dalam ViewModel
    private val mElapsedTime = MutableLiveData<Long?>()

    init {
        // Membuat timer yang akan berjalan di background thread
        val timer = Timer()

        // Menjadwalkan task yang akan dijalankan setiap 1 detik
        // Parameter: task, delay awal, interval pengulangan
        timer.schedule(object : TimerTask(){
            override fun run() {
                // Menghitung selisih waktu dari awal hingga sekarang, lalu konversi ke detik
                val newValue = (SystemClock.elapsedRealtime() - mInitialTime) / 1000

                // postValue() digunakan karena update dilakukan dari background thread
                // Jika dari main thread, bisa pakai setValue()
                mElapsedTime.postValue(newValue)
            }
        }, ONE_SECOND.toLong(), ONE_SECOND.toLong())
    }

    // Fungsi untuk mengekspos LiveData ke Activity/Fragment
    // Return type LiveData (bukan MutableLiveData) agar UI tidak bisa mengubah nilai
    fun getElapsedTime(): LiveData<Long?> {
        return mElapsedTime
    }
}