package ru.volgadev.avatarview

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.volgadev.avatarview.view.AvatarImageView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val avatarView1: AvatarImageView = findViewById(R.id.avatarView1)
        avatarView1.setUserName("Sergey")
        avatarView1.postDelayed({
            avatarView1.setImageDrawable(getDrawable(R.drawable.korolev))
        }, 3000L)

        val avatarView3: AvatarImageView = findViewById(R.id.avatarView3)
        with(avatarView3) {
            setUserName("Korolyov")
            setBorderColor(Color.GREEN)
            setBorderWidth(6)
            postDelayed({
                avatarView3.setImageDrawable(getDrawable(R.drawable.korolev))
            }, 5000L)
        }
    }
}