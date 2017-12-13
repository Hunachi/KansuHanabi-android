package com.example.hanah.flyingmuchofunfireworks.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.android.databinding.library.baseAdapters.BR
import com.example.hanah.flyingmuchofunfireworks.R
import com.example.hanah.flyingmuchofunfireworks.`object`.FireBox
import com.example.hanah.flyingmuchofunfireworks.`object`.FireWorksBall
import com.example.hanah.flyingmuchofunfireworks.databinding.FragmentItemBinding

/**
 * Created by hanah on 8/14/2017.
 */

class ItemAdapter(context: Context, val list : MutableList<FireWorksBall>, val callBack : (Int) -> Unit)
    : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(){

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.setVariable(BR.item, list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ItemViewHolder{
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        val holder = ItemViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            callBack(position)
        }
        return holder
    }

    override fun getItemCount(): Int = list.size

    fun itemId(position: Int) = list[position].id

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding :ViewDataBinding = DataBindingUtil.bind(view)
    }
}