package cz.ccnull.cc2016project.ui.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cz.ccnull.cc2016project.R;
import cz.ccnull.cc2016project.listener.OnItemClickListener;
import cz.ccnull.cc2016project.model.Receiver;

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ViewHolder> {

    Cursor cursor;
    OnItemClickListener<Receiver> listener;

    public ReceiverAdapter(OnItemClickListener<Receiver> listener) {
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
        Button confirm;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_name);
            confirm = (Button) itemView.findViewById(R.id.button_confirm);
        }

        public void bindReceiver(final Receiver receiver) {
            name.setText(receiver.getName());
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(receiver);
                }
            });
        }

    }
}
