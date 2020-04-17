package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wassup.AddS;
import com.example.wassup.Chat;
import com.example.wassup.Message_page;
import com.example.wassup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.ViewHolder>{
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private List<Chat> mChat;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext,List<Chat> mChat)
    {
        this.mChat=mChat;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
        View view =LayoutInflater.from(mContext).inflate(R.layout.rigth_chat,parent,false);
        return new MessageAdapter.ViewHolder(view);}
        else{ View view =LayoutInflater.from(mContext).inflate(R.layout.left_chat,parent,false);
            return new MessageAdapter.ViewHolder(view);}

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat= mChat.get(position);

        holder.show_message.setText(chat.getMessage());


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        //public ImageView profile_image;
        public ViewHolder(View itemView)
        {
            super(itemView);

            show_message=itemView.findViewById(R.id.show_message);
            //profile_image=itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }
}
