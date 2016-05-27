package cz.ccnull.cc2016project.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.listener.OnItemClickListener;
import cz.ccnull.cc2016project.model.Receiver;

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ViewHolder> {

    Context context;
    Cursor cursor;
    OnItemClickListener<Receiver> listener;

    public ReceiverAdapter(Context context, OnItemClickListener<Receiver> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.bindReceiver(new Receiver(cursor));
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void setData(Cursor c) {
        this.cursor = c;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView photo;
        Button confirm;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_name);
            photo = (ImageView) itemView.findViewById(R.id.image_photo);
            confirm = (Button) itemView.findViewById(R.id.button_confirm);
        }

        public void bindReceiver(final Receiver receiver) {
            name.setText(receiver.getName());
            photo.setImageDrawable(getUserPhoto(receiver.getName()));
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(receiver);
                }
            });
        }

    }

    private Drawable getUserPhoto(String name) {
        switch (name) {
            case "První uživatel":
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.user_pic1, null);
            case "Druhý uživatel":
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.user_pic2, null);
            default:
                return ResourcesCompat.getDrawable(context.getResources(), R.drawable.user_pic3, null);
        }
    }
}
