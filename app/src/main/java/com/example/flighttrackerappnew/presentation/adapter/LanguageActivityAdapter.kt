package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.LanguageDataList
import com.example.flighttrackerappnew.databinding.LanguageSelectionItems1Binding
import com.example.flighttrackerappnew.databinding.LanguageSelectionItems2Binding
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.listener.LanguageSelectionListener
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible

class LanguageActivityAdapter(private val config: Config, private val function: () -> Unit) :
    RecyclerView.Adapter<LanguageActivityAdapter.MyViewHolder>() {

    private var selectedLanguageName: String? = null
    private var dataList = ArrayList<LanguageDataList>()
    private var languageSelectionListener: LanguageSelectionListener? = null

    fun setListener(languageSelectionListener: LanguageSelectionListener) {
        this.languageSelectionListener = languageSelectionListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return when (viewType) {
            1 -> {
                val binding = LanguageSelectionItems1Binding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyViewHolder.Type1(binding)
            }

            2 -> {
                val binding = LanguageSelectionItems2Binding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyViewHolder.Type2(binding)
            }

            else -> throw IllegalArgumentException("Invalid viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        val isSelected = selectedLanguageName == data.name

        when (holder) {
            is MyViewHolder.Type1 -> holder.bind(
                data,
                isSelected,
                dataList.size,
                languageSelectionListener
            ) {
                updateSelection(it)
            }

            is MyViewHolder.Type2 -> holder.bind(data, isSelected, languageSelectionListener) {
                updateSelection(it)
            }

            else -> {}
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSelection(data: LanguageDataList) {
        selectedLanguageName = data.name
        config.selectedLanguageCode = data.code
        notifyDataSetChanged()
        function.invoke()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(languageData: ArrayList<LanguageDataList>) {
        dataList = ArrayList(languageData)
        notifyDataSetChanged()
    }

    sealed class MyViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {

        class Type1(private val binding: LanguageSelectionItems1Binding) :
            MyViewHolder(binding.root) {
            fun bind(
                data: LanguageDataList,
                isSelected: Boolean,
                listSize: Int,
                languageSelectionListener: LanguageSelectionListener?,
                listener: (LanguageDataList) -> Unit
            ) {
                binding.apply {
                    ivFlag.setImageResource(data.flag)
                    tvCountriesName.text = data.name
                    tick.setImageResource(if (isSelected) R.drawable.iv_selected_lang_tick else 0)
                    if (listSize - 1 == adapterPosition) {
                        binding.barrier.invisible()
                    } else {
                        binding.barrier.visible()
                    }
                    root.setOnClickListener {
                        languageSelectionListener?.onLanguageSelection()
                        listener(data)
                    }
                }
            }
        }

        class Type2(private val binding: LanguageSelectionItems2Binding) :
            MyViewHolder(binding.root) {
            fun bind(
                data: LanguageDataList,
                isSelected: Boolean,
                languageSelectionListener: LanguageSelectionListener?,
                listener: (LanguageDataList) -> Unit
            ) {
                binding.apply {
                    ivFlag.setImageResource(data.flag)
                    tvCountriesName.text = data.name
                    if (isSelected) {
                        root.background =
                            ContextCompat.getDrawable(binding.root.context, R.drawable.surface_bg_s)
                    } else {
                        root.background =
                            ContextCompat.getDrawable(binding.root.context, R.drawable.surface_bg)
                    }
                    root.setOnClickListener {
                        languageSelectionListener?.onLanguageSelection()
                        listener(data)
                    }
                }

            }
        }
    }
}