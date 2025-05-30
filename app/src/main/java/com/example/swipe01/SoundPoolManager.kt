package com.example.swipe01  // ← 実際のパッケージ名に揃える

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

object SoundPoolManager {
    private var soundPool: SoundPool? = null
    private var twistSoundId: Int = 0
    private var cutSoundId: Int = 0
    private var isLoaded = false

    fun initialize(context: Context) {
        if (soundPool != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        twistSoundId = soundPool!!.load(context, R.raw.twist, 1)
        cutSoundId = soundPool!!.load(context, R.raw.cut, 1)

        soundPool!!.setOnLoadCompleteListener { _, _, _ -> isLoaded = true }
    }

    fun playTwistSound() {
        if (isLoaded) soundPool?.play(twistSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playCutSound() {
        if (isLoaded) soundPool?.play(cutSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
