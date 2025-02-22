//package com.example.buddypunchclone;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class EmployeeMemberAdapter extends RecyclerView.Adapter<EmployeeMemberAdapter.MemberViewHolder> {
//    private List<EmployeeResponseDto> members;
//
//    public EmployeeMemberAdapter(List<EmployeeResponseDto> members) {
//        this.members = members;
//    }
//
//    @NonNull
//    @Override
//    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_group_member, parent, false);
//        return new MemberViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
//        EmployeeResponseDto member = members.get(position);
//        holder.memberNameTextView.setText(member.getFirstName() + " " + member.getLastName());
//        holder.memberUsernameTextView.setText(member.getUsername());
//    }
//
//    @Override
//    public int getItemCount() {
//        return members.size();
//    }
//
//    static class MemberViewHolder extends RecyclerView.ViewHolder {
//        TextView memberNameTextView;
//        TextView memberUsernameTextView;
//
//        public MemberViewHolder(@NonNull View itemView) {
//            super(itemView);
//            memberNameTextView = itemView.findViewById(R.id.member_name_text_view);
//            memberUsernameTextView = itemView.findViewById(R.id.member_username_text_view);
//        }
//    }
//}