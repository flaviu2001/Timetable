package com.flaviu.timetable.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flaviu.timetable.R
import com.flaviu.timetable.database.Card
import com.flaviu.timetable.databinding.TimetableCardBinding
import com.flaviu.timetable.weekdayLongToString
import kotlinx.android.synthetic.main.weekday.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_STRING = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class CardAdapter (private val clickListener: CardListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(CardDiffCallback()){
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is DataItem.TextItem -> ITEM_VIEW_TYPE_STRING
            is DataItem.CardItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val cardItem= getItem(position) as DataItem.CardItem
                holder.bind(clickListener, cardItem.card)
            }
            is TextViewHolder -> {
                val textItem = getItem(position) as DataItem.TextItem
                holder.itemView.weekday.text= weekdayLongToString(textItem.weekday)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_VIEW_TYPE_STRING -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown view type $viewType")
        }
    }

    fun addHeaderAndSubmitList(list: List<Card>?) {
        adapterScope.launch {
            if (list == null) {
                submitList(emptyList())
                return@launch
            }
            val items: MutableList<DataItem> = emptyList<DataItem>().toMutableList()
            var currWeekday = 0
            for (i in list.indices) {
                if (currWeekday < list[i].weekday)
                    currWeekday = list[i].weekday
                if (list[i].weekday == currWeekday) {
                    items.add(DataItem.TextItem(list[i].weekday.toLong()))
                    ++currWeekday
                }
                items.add(DataItem.CardItem(list[i]))
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class ViewHolder private constructor(private val binding: TimetableCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CardListener, item: Card) {
            binding.card = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TimetableCardBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.weekday, parent, false)
                return TextViewHolder(view)
            }
        }
    }

}

class CardDiffCallback :
    DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class CardListener(val clickListener: (cardId: Long) -> Unit) {
    fun onClick(card: Card) = clickListener(card.cardId)
}

sealed class DataItem {
    data class CardItem(val card: Card): DataItem() {
        override val id = Pair(0L, card.cardId)
    }
    data class TextItem(val weekday: Long): DataItem() {
        override val id = Pair(1L, weekday)
    }
    abstract val id: Pair<Long, Long>
}