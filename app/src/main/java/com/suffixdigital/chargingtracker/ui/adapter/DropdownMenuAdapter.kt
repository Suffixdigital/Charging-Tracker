package com.suffixdigital.chargingtracker.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.data.AgencyDataItem
import com.suffixdigital.chargingtracker.databinding.FragmentProfileBinding

class DropdownMenuAdapter(
    context: Context,
    @LayoutRes resource: Int,
    val agencyDataItemList: MutableList<AgencyDataItem>,
    val binding: FragmentProfileBinding
) : ArrayAdapter<AgencyDataItem>(context, resource, agencyDataItemList) {

    private val filteredAgencyDataItemList: MutableList<AgencyDataItem> =
        ArrayList(agencyDataItemList)

    override fun getCount(): Int {
        return filteredAgencyDataItemList.size
    }

    override fun getItem(position: Int): AgencyDataItem {
        return filteredAgencyDataItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return filteredAgencyDataItemList[position].agencyNumber.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView = super.getView(position, convertView, parent)
        val textview: TextView = itemView.findViewById(R.id.child_item)
        textview.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    parent.context,
                    R.color.black_000000_CC
                )
            )
        )
        val agencyDataItem: AgencyDataItem = getItem(position)
        val filterText = binding.dropdownMenu.text.toString()
        val spannableString = SpannableString(agencyDataItem.agencyName)
        if (filterText.isNotEmpty()) {
            val startIndex = agencyDataItem.agencyName.indexOf(filterText, ignoreCase = true)
            if (startIndex != -1) {
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    startIndex + filterText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            parent.context,
                            R.color.black_000000
                        )
                    ),
                    startIndex,
                    startIndex + filterText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        textview.text = spannableString
        return itemView
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun convertResultToString(resultValue: Any): String {
                return (resultValue as AgencyDataItem).agencyName
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val suggestedAgencyItemList: MutableList<AgencyDataItem> = ArrayList()
                constraint?.let {
                    for (agencyDataItem in agencyDataItemList) {
                        if (agencyDataItem.agencyName.lowercase()
                                .contains(it.toString().lowercase())
                        ) {
                            suggestedAgencyItemList.add(agencyDataItem)
                        }
                    }
                }
                return FilterResults().apply {
                    values = suggestedAgencyItemList
                    count = suggestedAgencyItemList.size
                }
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                filteredAgencyDataItemList.clear()
                if (results.count > 0) {
                    for (result in results.values as List<*>) {
                        if (result is AgencyDataItem) {
                            filteredAgencyDataItemList.add(result)
                        }
                    }
                    notifyDataSetChanged()
                } else if (constraint == null) {
                    filteredAgencyDataItemList.addAll(agencyDataItemList)
                    notifyDataSetInvalidated()
                }
            }
        }
    }

}



