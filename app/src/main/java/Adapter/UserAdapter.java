package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wassup.AddS;
import com.example.wassup.MainActivity;
import com.example.wassup.Message_page;
import com.example.wassup.R;
import com.example.wassup.frag_users;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter <UserAdapter.ViewHolder>{
    private Context mContext;
    private List<AddS> mUsers;

    public UserAdapter(Context mContext,List<AddS> mUsers)
    {
        this.mUsers=mUsers;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AddS user=mUsers.get(position);
        holder.username.setText(user.getUsername());
        holder.status.setText(user.getStatus());
        Glide.with(mContext).load(user.getImage()).into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent (mContext, Message_page.class);
                intent.putExtra("userid",user.getUid());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username,status;
        public ImageView profile_image;
        public ViewHolder(View itemView)
        {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            status=itemView.findViewById(R.id.status);
            profile_image=itemView.findViewById(R.id.profile_image);

        }
    }

}
