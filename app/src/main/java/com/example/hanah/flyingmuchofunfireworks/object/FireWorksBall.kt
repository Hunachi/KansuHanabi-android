package com.example.hanah.flyingmuchofunfireworks.`object`


/**
 * Created by hanah on 8/13/2017.
 */
data class FireWorksBall(var id: String = "0", var name: String = "花火")

class FireBox(private val balls : MutableList<FireWorksBall>){
        val count: Int = balls.count()
        fun fireAt(index: Int) : FireWorksBall = balls[index]
}