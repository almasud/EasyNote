package com.github.almasud.EasyNote.views.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.github.almasud.EasyNote.databinding.ItemNoteBinding;
import com.github.almasud.EasyNote.models.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class NoteRVAdapter
        extends RecyclerView.Adapter<NoteRVAdapter.NoteViewHolder> implements Filterable {
    private static final String TAG = "NotesRVAdapter";
    private List<Note> mNotes = new ArrayList<>();
    private List<Note> mFilteredNotes = new ArrayList<>();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat mDateFormatDay = new SimpleDateFormat(
            "dd", Locale.getDefault()
    );
    private SimpleDateFormat mDateFormatMonth = new SimpleDateFormat(
            "MMM", Locale.getDefault()
    );
    private SetOnNoteClickListener mSetOnNoteClickListener;

    public NoteRVAdapter(Fragment fragment) {
        mSetOnNoteClickListener = (SetOnNoteClickListener) fragment;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding viewBinding = ItemNoteBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false);
        return new NoteViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = mFilteredNotes.get(position);
        holder.setData(note);
        // Set a listener for item click event
        holder.itemView.setOnClickListener(v -> mSetOnNoteClickListener.onNoteClick(note));
    }

    @Override
    public int getItemCount() {
        return mFilteredNotes.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            FilterResults filterResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null || constraint.toString().isEmpty()) {
                    Log.d(TAG, "performFiltering: constraint is null or empty");
                    filterResults.count = mNotes.size();
                    filterResults.values = mNotes;
                } else {
                    Log.d(TAG, "performFiltering: constraint is not null or empty");
                    List<Note> resultNotes = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();

                    for (Note note : mNotes) {
                        try {
                            if (note.getTitle().toLowerCase(Locale.getDefault()).contains(filterPattern)
                            || note.getDetails().toLowerCase(Locale.getDefault()).contains(filterPattern)
                            || mDateFormatDay.format(mDateFormat.parse(note.getDate()))
                                    .toLowerCase(Locale.getDefault()).contains(filterPattern)
                            || mDateFormatMonth.format(mDateFormat.parse(note.getDate()))
                                    .toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                                // Set the result note
                                resultNotes.add(note);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    filterResults.count = resultNotes.size();
                    filterResults.values = resultNotes;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredNotes = new ArrayList<>();
                mFilteredNotes = (List<Note>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private ItemNoteBinding itemNoteBinding;

        NoteViewHolder(@NonNull ItemNoteBinding itemNoteBinding) {
            super(itemNoteBinding.getRoot());
            this.itemNoteBinding = itemNoteBinding;
        }

        void setData(Note note) {
            // Set the Note color into UI
            itemNoteBinding.cvNoteColor.setCardBackgroundColor(note.getColor());
            // Try to set the Note date into UI
            try {
                Log.d(TAG, "setData: date: " + note.getDate());
                itemNoteBinding.tvDateDay.setText(mDateFormatDay.format(
                        mDateFormat.parse(note.getDate()))
                );
                itemNoteBinding.tvDateMonth.setText(mDateFormatMonth.format(
                        mDateFormat.parse(note.getDate()))
                );
            } catch (ParseException e) {
                Log.w(TAG, "setData: Error: " + e.getMessage());
                e.printStackTrace();
            }
            // Set the Note title and description into UI
            itemNoteBinding.tvNoteTitle.setText(note.getTitle());
            itemNoteBinding.tvNoteDetails.setText(note.getDetails());
        }
    }

    // Set the Notes into Recyclerview
    public void setNotes(List<Note> notes) {
        Log.d(TAG, "setNotes: is called");
        // Clear list data before any data set
        mNotes.clear();
        mFilteredNotes.clear();

        // Set notes data
        mNotes = notes;
        mFilteredNotes = notes;
        // Notify to refresh the Recyclerview
        notifyDataSetChanged();
    }

    public interface SetOnNoteClickListener {
        void onNoteClick(Note note);
    }
}
