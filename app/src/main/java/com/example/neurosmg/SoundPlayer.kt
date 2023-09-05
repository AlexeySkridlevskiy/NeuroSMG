import android.content.Context
import android.media.MediaPlayer

class SoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(resourceId: Int) {
        stopSound()

        mediaPlayer = MediaPlayer.create(context, resourceId)

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }

        mediaPlayer?.start()
    }

    fun stopSound() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
