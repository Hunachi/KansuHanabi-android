package com.example.hanah.flyingmuchofunfireworks.view

import android.annotation.TargetApi
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast

import com.example.hanah.flyingmuchofunfireworks.R
import com.example.hanah.flyingmuchofunfireworks.client.SocketClient
import com.example.hanah.flyingmuchofunfireworks.databinding.ActivityMotionBinding
import com.example.hanah.flyingmuchofunfireworks.viewModel.MotionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

class MotionActivity : AppCompatActivity(), SensorEventListener, MotionViewModel.Callback {


    val SIV3D_SIZE_X = 1
    val SIV3D_SIZE_Y = 1
    lateinit var binding: ActivityMotionBinding
    lateinit var viewModel: MotionViewModel
    var id = "0"
    //var text  = "座標を選んでください"
    var displayPointX = 0
    var displayPointY = 0
    var pointX: Float = 0F
    var pointY: Float = 0F
    var canExpletion: Boolean
    private lateinit var manager: SensorManager
    private lateinit var sensor: Sensor
    lateinit var socket: SocketClient
    lateinit var imageView: ImageView
    lateinit var audioAttributes: AudioAttributes
    lateinit var soundPool: SoundPool
    var sound: Int = 0


    init {
        canExpletion = false
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion)
        socket = SocketClient()
        id = intent.getStringExtra("id")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_motion)
        viewModel = MotionViewModel(this, this)
        binding.motion = viewModel
        imageView = findViewById(R.id.imageView) as ImageView
        imageView.setImageDrawable(null)
        audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(10)
                .build()
        sound = soundPool.load(this, R.raw.hanabi, 1)
    }

    override fun onResume() {
        super.onResume()
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            pointX = event.x
            pointY = event.y
            imageView.x = pointX - 50F
            imageView.y = pointY - 250F
            pointX = pointX / displayPointX * SIV3D_SIZE_X
            pointY = pointY / displayPointY * SIV3D_SIZE_Y
            canExpletion = true
            imageView.setImageResource(R.drawable.star)
            Log.d(pointX.toString(), pointY.toString())
            //text = "振って打ち上げ🎆"
        }
        return false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val point = Point()
        this.windowManager.defaultDisplay.getSize(point)
        displayPointX = point.x
        displayPointY = point.y
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && canExpletion) {
            var sum = 0F
            (0..2).forEach {
                if (event.values[it] > 0) sum += event.values[it]
                else sum -= event.values[it]
            }
            if (sum >= 30F) {
                canExpletion = false //必要です.
                Log.d("Tag", id + "\n" + pointX.toString() + "\n" + pointY.toString()
                        + " " + displayPointX.toString() + " " + displayPointY.toString())
                //go
                socket.send("id:" + id + "\n" + "x:" + pointX.toString() + "y:" + pointY.toString() + "\n")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1F)
                        }, {
                            //Toast.makeText(this, "failed", Toast.LENGTH_LONG).show()
                        })
                soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1F)
                SocketClient().wait(500)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ canExpletion = true }, {})
            }
        }
    }

    override fun onPause() {
        super.onPause()
        manager.unregisterListener(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onFinish() {
        if (canExpletion) finish()
    }

}
