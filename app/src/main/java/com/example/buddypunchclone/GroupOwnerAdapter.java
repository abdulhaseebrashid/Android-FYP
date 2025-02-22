package com.example.buddypunchclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupOwnerAdapter extends RecyclerView.Adapter<GroupOwnerAdapter.GroupViewHolder> {

    private final List<GroupResponseDto> groupList;
    private final OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(Long groupId);
    }

    public GroupOwnerAdapter(List<GroupResponseDto> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }
    // Add this method inside the class
    public void updateData(List<GroupResponseDto> newData) {
        groupList.clear();  // Clear the old list
        groupList.addAll(newData);  // Add the new data
        notifyDataSetChanged();  // Notify RecyclerView to refresh
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_owner, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupResponseDto group = groupList.get(position);

        // Get the owner name (assuming first employee is the owner or
        // you might need to modify this based on your actual data structure)
        String ownerName = "No Owner";
        if (!group.getEmployees().isEmpty()) {
            EmployeeResponseDto owner = group.getEmployees().get(0);
            ownerName = owner.getFirstName() + " " + owner.getLastName();
        }

        // Set group name if available, otherwise show the ID
        String groupName = group.getName() != null ? group.getName() : "Group " + group.getId();
        holder.groupNameTextView.setText(groupName);

        // Set group description if available, otherwise show a default message
        String description = group.getDescription() != null ?
                group.getDescription() :
                "No description available";
        holder.groupDescriptionTextView.setText(description);

        holder.groupIdTextView.setText("ID: " + group.getId());
        holder.ownerNameTextView.setText("Owner: " + ownerName);
        holder.memberCountTextView.setText("Members: " + group.getEmployees().size());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGroupClick(group.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView;
        TextView groupDescriptionTextView;
        TextView groupIdTextView;
        TextView ownerNameTextView;
        TextView memberCountTextView;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            groupDescriptionTextView = itemView.findViewById(R.id.groupDescriptionTextView);
            groupIdTextView = itemView.findViewById(R.id.groupIdTextView);
            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
            memberCountTextView = itemView.findViewById(R.id.memberCountTextView);
        }
    }
}