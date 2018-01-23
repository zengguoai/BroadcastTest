package com.example.localreceivertest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by weiguanghua on 18-1-23.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{
    private List<Contacts> mContactsList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView contatName;
        private TextView contactNumber;
        public ViewHolder(View view){
            super(view);
            contatName = view.findViewById(R.id.contacts_name);
            contactNumber= view.findViewById(R.id.contacts_number);
        }
    }

    public ContactsAdapter(List<Contacts> mContactsList){
        this.mContactsList =mContactsList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contacts contacts =  mContactsList.get(position);
        holder.contatName.setText(contacts.getName());
        holder.contactNumber.setText(contacts.getNumber());
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }
}
