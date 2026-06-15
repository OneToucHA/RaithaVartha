package com.example.raithvartha

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TipCardAdapter
    private lateinit var db: AppDatabase
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var tvDateTime: TextView
    private lateinit var tvVoiceResult: TextView

    private val handler = Handler(Looper.getMainLooper())

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )

                val command = matches?.getOrNull(0) ?: ""
                tvVoiceResult.text = command

                val reply = when {

                    command.contains("paddy", true) || command.contains("ಭತ್ತ") ->
                        "For paddy crop, maintain 2 to 5 centimetre water level and apply nitrogen fertilizer regularly."

                    command.contains("tomato", true) || command.contains("ಟೊಮ್ಯಾಟೊ") ->
                        "For tomato crop, use drip irrigation and spray neem oil for pest control. "

                    command.contains("coconut", true) || command.contains("ತೆಂಗು") ->
                        "For coconut trees, irrigate weekly and apply organic manure every 3 months."

                    command.contains("areca", true) || command.contains("ಅಡಿಕೆ") ->
                        "For areca nut, ensure shade management and proper drainage."

                    command.contains("fertilizer", true) || command.contains("ಗೊಬ್ಬರ") ->
                        "Use organic compost, vermicompost and NPK fertilizers as recommended."

                    command.contains("disease", true) || command.contains("ರೋಗ") ->
                        "Remove infected leaves and spray suitable fungicide or neem solution."

                    command.contains("pest", true) || command.contains("ಕೀಟ") ->
                        "Use sticky traps, neem oil spray and biological pest control methods."

                    else ->
                        "Sorry, I do not understand. Please ask about paddy, tomato, coconut, areca nut, fertilizer or disease."
                }

                tvVoiceResult.text = reply
                speak(reply)
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startVoiceInput()
            } else {
                tvVoiceResult.text = "Mic permission denied"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDateTime = findViewById(R.id.tvDateTime)
        tvVoiceResult = findViewById(R.id.tvVoiceResult)

        db = AppDatabase.getInstance(this)
        adapter = TipCardAdapter()

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter
        viewPager.setPageTransformer { page, position ->
            page.scaleY = 1 - abs(position) * 0.1f
        }

        db.tipDao().getAllTips().observe(this) {
            adapter.setTips(it)
        }

        findViewById<Button>(R.id.btnKannada).setOnClickListener {
            adapter.setLanguage(true)
        }

        findViewById<Button>(R.id.btnEnglish).setOnClickListener {
            adapter.setLanguage(false)
        }

        findViewById<Button>(R.id.btnVoice).setOnClickListener {
            checkPermissionAndStartVoice()
        }

        findViewById<FloatingActionButton>(R.id.fabWelcome).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("🌾 Welcome")
                .setMessage("Welcome to Raitha Vartha")
                .setPositiveButton("OK", null)
                .show()
        }

        textToSpeech = TextToSpeech(this) {
            textToSpeech.language = Locale.ENGLISH
        }

        setupChips()
        startDateTimeUpdater()
    }

    private fun startDateTimeUpdater() {
        val runnable = object : Runnable {
            override fun run() {
                runOnUiThread {
                    tvDateTime.text = SimpleDateFormat(
                        "dd MMM yyyy\nhh:mm:ss a",
                        Locale.getDefault()
                    ).format(Date())
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    private fun checkPermissionAndStartVoice() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceInput()
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            tvVoiceResult.text = "Speech not available"
        }
    }

    private fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun setupChips() {
        findViewById<Chip>(R.id.chipAll).setOnClickListener {
            db.tipDao().getAllTips().observe(this) { adapter.setTips(it) }
        }

        findViewById<Chip>(R.id.chipPaddy).setOnClickListener {
            db.tipDao().getTipsByCategory("Paddy").observe(this) { adapter.setTips(it) }
        }

        findViewById<Chip>(R.id.chipAreca).setOnClickListener {
            db.tipDao().getTipsByCategory("Areca nut").observe(this) { adapter.setTips(it) }
        }

        findViewById<Chip>(R.id.chipCoconut).setOnClickListener {
            db.tipDao().getTipsByCategory("Coconut").observe(this) { adapter.setTips(it) }
        }

        findViewById<Chip>(R.id.chipTomato).setOnClickListener {
            db.tipDao().getTipsByCategory("Tomato").observe(this) { adapter.setTips(it) }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}