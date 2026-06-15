package com.example.raithvartha

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Tip::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tipDao(): TipDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "raitha_varta_db"
                )
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = INSTANCE ?: return@launch
                val dao = database.tipDao()
                if (dao.getCount() == 0) {
                    dao.insertAll(getSampleTips())
                }
            }
        }

        private fun getSampleTips(): List<Tip> {
            return listOf(
                Tip(
                    titleKannada = "ಧಾನ್ಯದ ಕೀಟ ನಿಯಂತ್ರಣ",
                    titleEnglish = "Paddy Pest Control",
                    instructionKannada = "ಇಂದು ಬೆಳಗ್ಗೆ 6-8 ಗಂಟೆ ನಡುವೆ ಕ್ಲೋರೋಫೈರಿಫಾಸ್ ಸಿಂಪಡಿಸಿ. ಎಕರೆಗೆ 2 ಲೀಟರ್ ನೀರಿನಲ್ಲಿ 5ml ಮಿಶ್ರ ಮಾಡಿ.",
                    instructionEnglish = "Spray Chlorpyrifos between 6-8 AM today. Mix 5ml in 2 liters of water per acre.",
                    cropCategory = "Paddy",
                    imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Riceplant.jpg/800px-Riceplant.jpg",
                    successStory = "ರಾಮಣ್ಣ (ಮಂಡ್ಯ) ಈ ಸಲಹೆ ಬಳಸಿ 30% ಹೆಚ್ಚು ಇಳುವರಿ ಪಡೆದರು!",
                    isDailyTip = true
                ),
                Tip(
                    titleKannada = "ತೆಂಗಿನ ನೀರಾವರಿ",
                    titleEnglish = "Coconut Irrigation",
                    instructionKannada = "ಬೇಸಿಗೆಯಲ್ಲಿ ತೆಂಗಿಗೆ ಪ್ರತಿ 3 ದಿನಕ್ಕೊಮ್ಮೆ 200 ಲೀಟರ್ ನೀರು ನೀಡಿ. ಬೆಳಗ್ಗೆ ಮಾತ್ರ ನೀರು ಹಾಕಿ.",
                    instructionEnglish = "Give 200 liters of water every 3 days in summer. Water only in the morning.",
                    cropCategory = "Coconut",
                    imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Coconut_palm_tree.jpg/800px-Coconut_palm_tree.jpg",
                    successStory = "ಸುರೇಶ್ (ತುಮಕೂರು) ಈ ವಿಧಾನದಿಂದ ಸಿಂಗಾರ ಉದುರುವಿಕೆ ತಡೆದರು."
                ),
                Tip(
                    titleKannada = "ಟೊಮ್ಯಾಟೊ ರೋಗ",
                    titleEnglish = "Tomato Blight Control",
                    instructionKannada = "ಎಲೆ ಮೇಲೆ ಕಂದು ಮಚ್ಚೆ ಕಂಡರೆ Mancozeb 2g/ಲೀಟರ್ ಸಿಂಪಡಿಸಿ. ಮಳೆ ಬಂದ ನಂತರ ಮತ್ತೊಮ್ಮೆ ಸಿಂಪಡಿಸಿ.",
                    instructionEnglish = "If brown spots appear on leaves, spray Mancozeb 2g/liter. Repeat after rain.",
                    cropCategory = "Tomato",
                    imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/800px-Tomato_je.jpg",
                    successStory = "ಲಕ್ಷ್ಮಮ್ಮ (ಕೋಲಾರ) 40% ಬೆಳೆ ನಷ್ಟ ತಡೆದರು ಈ ಸಲಹೆಯಿಂದ."
                ),
                Tip(
                    titleKannada = "ಅಡಿಕೆ ಕೊಳೆ ರೋಗ",
                    titleEnglish = "Arecanut Rot Prevention",
                    instructionKannada = "ಮಳೆಗಾಲ ಮೊದಲು Bordeaux mixture (1%) ಸಿಂಪಡಿಸಿ. ತೋಟದಲ್ಲಿ ನೀರು ನಿಲ್ಲದಂತೆ ಚರಂಡಿ ಮಾಡಿ.",
                    instructionEnglish = "Spray Bordeaux mixture (1%) before monsoon. Ensure proper drainage.",
                    cropCategory = "Areca nut",
                    imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Arecanut_tree.jpg/800px-Arecanut_tree.jpg",
                    successStory = "ಗಣೇಶ್ (ಶಿವಮೊಗ್ಗch) ಕೊಳೆ ರೋಗದಿಂದ ₹50,000 ಉಳಿಸಿದರು."
                )
            )
        }
    }
}