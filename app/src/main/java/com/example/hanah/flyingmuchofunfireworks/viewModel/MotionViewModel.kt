package com.example.hanah.flyingmuchofunfireworks.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.android.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.example.hanah.flyingmuchofunfireworks.R
import com.example.hanah.flyingmuchofunfireworks.view.MainActivity

/**
 * Created by hanah on 8/17/2017.
 */
class MotionViewModel(private val context: Context, private val callbacks : Callback) : BaseObservable(){


    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun setImage(imageView: ImageView, id : Int?){
            Glide.with(imageView).load(id).into(imageView)//loadの中にresourceを入れたらtestできる
        }
    }

    @Bindable
    var imageId : Int? = null
    get
    set(value) {
        field = value
        notifyPropertyChanged(BR.imageId)
    }

    @Bindable
    var onClickable = true
    get
    set(value) {
        field = value
    }

    var text = ""

    fun onClickStopButton(view: View){
        callbacks.onFinish()
    }

    fun onTouch(){
        imageId = R.drawable.hanabi
    }
    /*
     * 時間あったらきれいに書き直します。
     * http://d.hatena.ne.jp/tetsu831/20111017/1318833874
     */

    interface Callback{

        fun onFinish()

    }
}