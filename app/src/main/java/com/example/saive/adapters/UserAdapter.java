package com.example.saive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.ImageButton;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saive.R;
import com.example.saive.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onBlockToggle(User user, int position);
    }

    private List<User> userList;
    private OnUserActionListener listener;

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserRole.setText(user.getRole());

        if (user.isBlocked()) {
            holder.btnBlockUser.setImageResource(R.drawable.ic_check_circle); // Using check_circle as "unblock" icon placeholder
            holder.btnBlockUser.setColorFilter(Color.GRAY);
            holder.tvUserName.setTextColor(Color.GRAY);
        } else {
            holder.btnBlockUser.setImageResource(R.drawable.ic_notifications); // Using notifications as "block" icon placeholder
            holder.btnBlockUser.setColorFilter(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.colorMaroon));
            holder.tvUserName.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.colorNoirBlack));
        }

        holder.btnBlockUser.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBlockToggle(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole;
        ImageButton btnBlockUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnBlockUser = itemView.findViewById(R.id.btnBlockUser);
        }
    }
}
