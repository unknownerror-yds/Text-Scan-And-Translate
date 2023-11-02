package com.example.textscanandtranslate.feature.prevscantext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.textandtranslate.R;
import com.example.textscanandtranslate.feature.db.TextPojo;
import com.example.textscanandtranslate.feature.translatepage.Translate;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TextEntryAdapter extends RecyclerView.Adapter<TextEntryAdapter.TextEntryViewHolder> {
    private final List<TextPojo> textEntries;
    private final Context context;

    public TextEntryAdapter(List<TextPojo> textEntries,Context context) {
        this.textEntries = textEntries;
        this.context = context;
    }

    @Override
    public TextEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new TextEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextEntryViewHolder holder, int position) {
        TextPojo entry = textEntries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return textEntries.size();
    }

    public class TextEntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView timeStampView;
        private final MaterialButton translate;
        public TextEntryViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textTextView);
            timeStampView = itemView.findViewById(R.id.timeStampView);
            translate = itemView.findViewById(R.id.translateButton);

            translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(context, Translate.class);
                        String textToTransfer = textView.getText().toString();
                        intent.putExtra("transferredText", textToTransfer);
                        //Since we are trying to start an activity from a context that is not an Activity.
                        // To resolve this error, you can add the FLAG_ACTIVITY_NEW_TASK flag when starting the activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                }

            });
        }

        public void bind(TextPojo entry) {
            textView.setText(entry.getText());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
//            String formattedTimestamp = dateFormat.format(entry.getTimestampDate());
            timeStampView.setText(entry.getTimestamp());
        }
    }

    public void clear() {
        textEntries.clear();
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

}
