package com.crazyostudio.ecommercegrocery.Adapter;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.AddresslayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressAdapterViewHolder>{
    ArrayList<String> address;
    AddressInterface addressInterface;
    Context context;

    public AddressAdapter(ArrayList<String> address, AddressInterface addressInterface, Context context) {
        this.address = address;
        this.addressInterface = addressInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressAdapter.AddressAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressAdapter.AddressAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.addresslayout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.AddressAdapterViewHolder holder, int position) {
        String addressS = address.get(position);
        holder.binding.address.setText(addressS);
        holder.binding.getRoot().setOnClickListener(deliver-> addressInterface.addersSelect(addressS));
        holder.binding.edit.setOnClickListener(Edit-> showPopupMenu(holder.binding.edit,position,addressS));



    }
    private void showPopupMenu(View view,int position,String address) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.address_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId()==R.id.edit){
                addressInterface.Edit(address,position);
                return true;
            }else if (menuItem.getItemId()==R.id.remove){
                addressInterface.remove(address,position);
                return true;
            }
            return false;
        });
        popup.show();
    }
    @Override
    public int getItemCount() {
        return address.size();
    }

    public static class AddressAdapterViewHolder extends RecyclerView.ViewHolder {
        AddresslayoutBinding binding;
        public AddressAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AddresslayoutBinding.bind(itemView);
        }
    }
}
