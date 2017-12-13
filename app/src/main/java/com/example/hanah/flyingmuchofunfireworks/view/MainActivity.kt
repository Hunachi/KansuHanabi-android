package com.example.hanah.flyingmuchofunfireworks.view


import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.hanah.flyingmuchofunfireworks.*
import com.example.hanah.flyingmuchofunfireworks.`object`.FireBox
import com.example.hanah.flyingmuchofunfireworks.`object`.FireWorksBall
import com.example.hanah.flyingmuchofunfireworks.adapter.ItemAdapter
import com.example.hanah.flyingmuchofunfireworks.client.SocketClient
import com.example.hanah.flyingmuchofunfireworks.databinding.FragmentListBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch


class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var socket: SocketClient
    lateinit var fireList: MutableList<FireWorksBall>
    lateinit var listAdapter: ItemAdapter
    private lateinit var sensor: Sensor
    lateinit var manager: SensorManager
    private var canRead: Boolean
    lateinit var binding: FragmentListBinding

    init {
        canRead = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_list)

        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        socket = SocketClient()
        binding =
                DataBindingUtil.setContentView(this, R.layout.fragment_list)

        fireList = mutableListOf<FireWorksBall>()
        listAdapter = ItemAdapter(applicationContext, fireList) {
            Toast.makeText(applicationContext, "failed", Toast.LENGTH_LONG)
            val id = fireList[it].id
            Log.d(id, "ドカーン！！")
            val intent = Intent(this, MotionActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)

    }

    override fun onResume() {
        super.onResume()
        canRead = true
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun toRead() {
        socket.read()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val count = fireList.size
                    fireList.clear()
                    listAdapter.notifyItemRangeRemoved(0, count)
                    fireList.addAll(it)
                    listAdapter.notifyItemRangeInserted(0,it.size)
                    canRead = true
                },{
                })
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && canRead) {
            var vSum = 0F
            (0..2).forEach {
                if (event.values[it] > 0) vSum += event.values[it]
                else vSum -= event.values[it]
            }
            if (vSum > 30) {
                canRead = false
                Toast.makeText(this, "花火を取得中・・・🎆", Toast.LENGTH_SHORT).show()
                toRead()
                //fireList.add(FireWorksBall("1","こんにちは"))
                //listAdapter.notifyItemRangeInserted(0,1)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        canRead = false
        manager.unregisterListener(this)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
