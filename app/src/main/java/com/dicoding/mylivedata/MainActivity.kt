package com.dicoding.mylivedata

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mylivedata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View Binding untuk mengakses view tanpa findViewById()
    private lateinit var binding: ActivityMainBinding

    // ViewModel yang akan mengelola data timer
    private lateinit var liveDataTimerViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi View Binding untuk akses ke layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur padding agar konten tidak tertutup system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi ViewModel menggunakan ViewModelProvider
        // ViewModel akan bertahan meski terjadi perubahan konfigurasi (rotasi layar)
        liveDataTimerViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Memulai observasi data dari ViewModel
        subscribe()
    }

    private fun subscribe() {
        // Membuat Observer yang akan dipanggil setiap kali LiveData berubah
        // Observer ini akan menerima nilai terbaru (elapsed time dalam detik)
        val elapsedTimeObserver = Observer<Long?> { aLong ->
            // Format string dengan placeholder untuk menampilkan detik yang berlalu
            val newText = this@MainActivity.resources.getString(R.string.seconds, aLong)

            // Update UI dengan nilai timer terbaru
            binding.timerTextview.text = newText
        }

        // Mendaftarkan Observer ke LiveData
        // Lifecycle owner (this) memastikan observer otomatis berhenti saat Activity destroyed
        liveDataTimerViewModel.getElapsedTime().observe(this, elapsedTimeObserver)
    }
}