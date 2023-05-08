package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ItemListDataBinding
import com.example.android.trackmysleepquality.databinding.ItemListHeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_HEADER = 0
private const val SLEEP_ITEM = 1

class SleepTrackerAdapter(private val clickListener: SleepTrackClickListener) :
    ListAdapter<SleepTrackerAdapter.DataItem, RecyclerView.ViewHolder>(SleepTrackerCallback()) {
    /*var data = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size*/

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_HEADER
            is DataItem.SleepItem -> SLEEP_ITEM
            else -> throw java.lang.ClassCastException("Invalid type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SleepQualityHeader -> {
                val item = getItem(position) as DataItem.Header
                holder.bind(item)
            }
            is SleepQualityViewHolder -> {
                val item = getItem(position) as DataItem.SleepItem
                holder.bind(clickListener, item.sleepNight)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_HEADER -> SleepQualityHeader.from(parent)
            SLEEP_ITEM -> SleepQualityViewHolder.from(parent)
            else -> throw java.lang.ClassCastException("Invalid Type")
        }
    }

    fun addHeaderSubmitList(list:List<SleepNight>){
        val adapterScope = CoroutineScope(Dispatchers.Default)
        adapterScope.launch {
            val items = when(list){
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepItem(it) }
            }
            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }

    class SleepQualityViewHolder private constructor(private val binding: ItemListDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: SleepTrackClickListener, night: SleepNight) {
            binding.sleep = night
            binding.executePendingBindings()
            binding.sleepTrackerClickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup) = SleepQualityViewHolder(
                ItemListDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    class SleepQualityHeader private constructor(private val binding: ItemListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(id: DataItem.Header) {
            binding.tvItemHeader.text = "Header"
        }

        companion object {
            fun from(parent: ViewGroup) = SleepQualityHeader(
                ItemListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    class SleepTrackerCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    class SleepTrackClickListener(val clickListener: (night: SleepNight) -> Unit) {
        fun onClick(night: SleepNight) = clickListener(night)
    }

    sealed class DataItem {
        data class SleepItem(val sleepNight: SleepNight) : DataItem() {
            override val id = sleepNight.nightId
        }

        object Header : DataItem() {
            override val id: Long
                get() = Long.MIN_VALUE
        }

        abstract val id: Long
    }
}