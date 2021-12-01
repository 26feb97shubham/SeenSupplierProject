package com.dev.ecomercesupplier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility.Companion.checkedPosition
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.model.ModelForSpinner
import kotlinx.android.synthetic.main.select_category_item_sign_up.view.*

class AccountTypeAdapter(private  val context: Context,
private val accountTypeList : ArrayList<ModelForAccountType>)
    : RecyclerView.Adapter<AccountTypeAdapter.AccountTypeAdapterVH>() {
    inner class AccountTypeAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountTypeAdapterVH {
        val mView = LayoutInflater.from(context).inflate(R.layout.select_category_item_sign_up, parent, false)
        return AccountTypeAdapterVH(mView)
    }

    override fun onBindViewHolder(holder: AccountTypeAdapterVH, position: Int) {
        clicklistner(holder, position)
        holder.itemView.mtv_category_name.text = accountTypeList[position].name
        setuplist(position,holder)
    }

    private fun setuplist(position: Int, holder: AccountTypeAdapter.AccountTypeAdapterVH) {
        if (checkedPosition == position)
        {
            holder.itemView.account_type_card.setBackgroundColor(context.getResources().getColor(R.color.gold))
        }
        else
        {
            holder.itemView.account_type_card.setBackgroundColor(context.getResources().getColor(R.color.white))
        }
    }

    private fun clicklistner(holder: AccountTypeAdapter.AccountTypeAdapterVH, position: Int) {
        holder.itemView.setOnClickListener {
            if (checkedPosition >= 0)
                notifyItemChanged(checkedPosition)
            checkedPosition = holder.adapterPosition
            notifyItemChanged(checkedPosition)
        }
    }

    override fun getItemCount(): Int {
        return accountTypeList.size
    }
}