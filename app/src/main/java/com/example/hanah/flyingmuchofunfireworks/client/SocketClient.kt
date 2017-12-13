package com.example.hanah.flyingmuchofunfireworks.client

import android.util.Log
import com.example.hanah.flyingmuchofunfireworks.`object`.FireWorksBall
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.Socket
import java.util.concurrent.CountDownLatch

/**
 * Created by hanah on 8/14/2017.
 */
class SocketClient {

    private val IP = "127.0.0.1"
    private val PORT = 80
    lateinit var input: InputStream
    lateinit var output: OutputStream
    lateinit var br: BufferedReader
    lateinit var bw: BufferedWriter

    fun firstInit() : Single<Unit> = Single.fromCallable{
        Socket(IP, PORT).use {
            Log.d("Tag","Socket")
            input = it.inputStream
            output = it.outputStream
            br = BufferedReader(InputStreamReader(input))
            bw = BufferedWriter(OutputStreamWriter(output))
        }
    }

    fun send(fireWorks: String): Single<Unit> =
            Single.fromCallable {
                Socket(IP, PORT).use {
                    input = it.inputStream
                    output = it.outputStream
                    br = BufferedReader(InputStreamReader(input))
                    bw = BufferedWriter(OutputStreamWriter(output))
                    bw.write(fireWorks)
                    bw.write(0)
                    bw.flush()
                    //br.readLine()
                }
            }

    fun read(): Single<MutableList<FireWorksBall>> =
            Single.fromCallable {
                Socket(IP, PORT).use {
                    Log.d("send", "Socket")
                    input = it.inputStream
                    output = it.outputStream
                    br = BufferedReader(InputStreamReader(input))
                    bw = BufferedWriter(OutputStreamWriter(output))
                    Log.d("connect", "nya-n")
                    bw.write("get_list")
                    bw.write(0)
                    bw.flush()
                    val list: MutableList<FireWorksBall> = mutableListOf()
                    val num = br.readLine()
                    //val byte = br.read().toByte()
                    //val num = java.lang.String(arrayOf(byte).toByteArray(), "US-ASCII").toString()
                    //val stringBuilder = StringBuilder()
                    for (i in 1..num.toInt()){
                        val id : String = br.readLine() ?: break
                        val name : String = br.readLine() ?: break
                        Log.d(id,name)
                        list.add(FireWorksBall(id, name))
                    }
                    Log.d("send", num)
                    list
                }
            }


    fun wait(time : Long) : Single<Unit> =
            Single.fromCallable{
                Thread.sleep(time)
            }
}