package com.thuanduong.education.network.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.thuanduong.education.network.R;


public class JoinedEventMissionViewHolder extends RecyclerView.ViewHolder {
    public CheckBox checkBox ;
    public TextView name,amount,had;
    public EditText input;
    public JoinedEventMissionViewHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.joined_mission_checkbox);
        name = itemView.findViewById(R.id.joined_mission_name);
        amount = itemView.findViewById(R.id.joined_mission_amount);
        had = itemView.findViewById(R.id.joined_mission_had);
        input = itemView.findViewById(R.id.joined_mission_input_number);
    }

}