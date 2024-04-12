package com.crazyostudio.ecommercegrocery.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crazyostudio.ecommercegrocery.Model.AddressModel;
import com.crazyostudio.ecommercegrocery.R;
import com.crazyostudio.ecommercegrocery.databinding.AddresslayoutBinding;
import com.crazyostudio.ecommercegrocery.interfaceClass.AddressInterface;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressAdapterViewHolder>{
    ArrayList<AddressModel> address;
    AddressInterface addressInterface;
    Context context;

    public AddressAdapter(ArrayList<AddressModel> address, AddressInterface addressInterface, Context context) {
        this.address = address;
        this.addressInterface = addressInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressAdapter.AddressAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressAdapter.AddressAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.addresslayout, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.AddressAdapterViewHolder holder, int position) {
        AddressModel addressS = address.get(position);
//        holder.binding.address.setText(addressS.getAddress());
        String type;
        if (addressS.isHomeSelected()) {
            type = "Home";
            Glide.with(context).load(R.drawable.home_shipping).into(holder.binding.AddressType);
        }else {
            type = "Work";
            Glide.with(context).load(R.drawable.office_building).into(holder.binding.AddressType);
        }


        holder.binding.deliveryTo.setText("Delivering to "+type);
        holder.binding.deliveryAddress.setText(addressS.getFlatHouse()+addressS.getAddress());
        holder.binding.edit.setOnClickListener(view->{
            Toast.makeText(context, "work", Toast.LENGTH_SHORT).show();
        });
        holder.binding.getRoot().setOnClickListener(deliver-> addressInterface.addersSelect(addressS));
        holder.binding.delete.setOnClickListener(Edit->
                addressInterface.remove(addressS,position)
//                showPopupMenu(holder.binding.edit,position,addressS)
        );



    }
//    private void showPopupMenu(View view,int position,AddressModel address) {
//        // inflate menu
//        PopupMenu popup = new PopupMenu(view.getContext(),view);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.address_menu, popup.getMenu());
//        popup.setOnMenuItemClickListener(menuItem -> {
//            if (menuItem.getItemId()==R.id.edit){
//                addressInterface.Edit(address,position);
//                return true;
//            }else if (menuItem.getItemId()==R.id.remove){
//                addressInterface.remove(address,position);
//                return true;
//            }
//            return false;
//        });
//        popup.show();
//    }
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
