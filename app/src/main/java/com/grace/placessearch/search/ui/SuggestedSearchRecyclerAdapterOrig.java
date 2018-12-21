package com.grace.placessearch.search.ui;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.grace.placessearch.R;
import com.grace.placessearch.common.ui.view.ViewUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
@Deprecated
public class SuggestedSearchRecyclerAdapterOrig extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_DATA = 1;

    private List<String> suggestedSearchResults;
    private ViewGroup searchHeader;
    private SuggestedSearchOnClickListener suggestedSearchItemClickListener;
    private View.OnTouchListener backgroundTouchListener;
    private RecyclerView recyclerView;
    private String searchInput;

    public SuggestedSearchRecyclerAdapterOrig(RecyclerView recyclerView, List<String> suggestedSearchResults, ViewGroup searchHeader) {
        this.recyclerView = recyclerView;
        this.suggestedSearchResults = suggestedSearchResults;
        this.searchHeader = searchHeader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suggested_search_header, viewGroup, false);
                return new SuggestedSearchHeaderViewHolder(view);

            case ITEM_TYPE_DATA:
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suggested_search_item, viewGroup, false);
                final SuggestedSearchItemViewHolder suggestedSearchItemViewHolder = new SuggestedSearchItemViewHolder(view);

                // text click listener
                suggestedSearchItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPos = suggestedSearchItemViewHolder.getAdapterPosition();
                        if (adapterPos != RecyclerView.NO_POSITION) {
                            suggestedSearchItemClickListener.onSuggestedSearchItemClick(suggestedSearchResults.get(adapterPos));
                        }
                    }
                });

                return suggestedSearchItemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SuggestedSearchItemViewHolder) {
            boldAndSetText(viewHolder);
            setAnimation(viewHolder.itemView, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return suggestedSearchResults.size();
    }

    public void setClickListeners(SuggestedSearchOnClickListener listener, View.OnTouchListener backgroundTouchListener) {
        this.suggestedSearchItemClickListener = listener;
        this.backgroundTouchListener = backgroundTouchListener;
    }

    public void clearSearchResults() {
        suggestedSearchResults.clear();
        notifyDataSetChanged();
    }

    public boolean isSearchResultsEmpty() {
        return suggestedSearchResults.isEmpty();
    }

    public void setSearchResults(List<String> newSearchResults, String newSearchInput) {

        // update data
        // 1) if newSearchInput is substring of searchInput AND new list size is < current list size, this means a search refine
        // 2) else, do refresh of entire list

        // refine list
        if (newSearchInput != null && searchInput != null &&
                newSearchInput.contains(searchInput) && // current searchInput is a substring of newSearchInput
                newSearchResults.size() < suggestedSearchResults.size()) {

            this.searchInput = newSearchInput;

            int i;
            do {
                for (i = 0; i < suggestedSearchResults.size(); i++) {
                    String searchTerm = suggestedSearchResults.get(i);
                    if (!newSearchResults.contains(searchTerm)) { // if term is not in new results, remove
                        suggestedSearchResults.remove(i);
                        notifyItemRemoved(i);
                        break;
                    } else {
                        boldAndSetText(recyclerView.findViewHolderForAdapterPosition(i));
                    }
                }
            } while (i < suggestedSearchResults.size());

            // add newSearchResults to end of suggestedSearchResults list
            for (i = 0; i < newSearchResults.size(); i++) {
                if (!suggestedSearchResults.contains(newSearchResults.get(i))) {
                    suggestedSearchResults.add(newSearchResults.get(i));
                    notifyItemInserted(suggestedSearchResults.size() - 1);
                }
            }

        } else { // refresh list

            this.searchInput = newSearchInput;

            suggestedSearchResults.clear();
            if (newSearchResults != null) {
                suggestedSearchResults.addAll(newSearchResults);
            }
            notifyDataSetChanged();
        }
    }

    private void setAnimation(View view, int position) {
        if (position != 0) {
            startAnimations(view, position);
        }
    }

    private void startAnimations(View view, int position) {

        float factor = (float) position / 10;
        if (factor == 0) {
            factor = .05f;
        }

        int startDelay = position * 100;
        int duration = 600;

        // alpha
        view.setAlpha(0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1);
        anim.setDuration(duration);
        anim.setStartDelay(startDelay);

        // translate
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "translationY", 200, 0);
        anim1.setDuration(duration);
        anim1.setInterpolator(new AccelerateInterpolator(factor));

        anim.start();
        anim1.start();

    }

    private void boldAndSetText(RecyclerView.ViewHolder viewHolder) {

        if (viewHolder == null) {
            return;
        }

        int adapterPos = viewHolder.getAdapterPosition();
        if (adapterPos != RecyclerView.NO_POSITION) {
            String suggestedSearch = suggestedSearchResults.get(adapterPos);
            SpannableStringBuilder spannableStringBuilder = ViewUtils.INSTANCE.applyBoldStyleToText(suggestedSearch, searchInput);
            ((SuggestedSearchItemViewHolder) viewHolder).suggestedSearchItemTextView.setText(spannableStringBuilder);
        }
    }

    public interface SuggestedSearchOnClickListener {
        void onSuggestedSearchItemClick(String item);
    }

    public static class SuggestedSearchItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.suggested_search_item_text)
        public TextView suggestedSearchItemTextView;

        public SuggestedSearchItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class SuggestedSearchHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.suggested_search_header_text)
        public TextView suggestedSearchHeaderText;

        public SuggestedSearchHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
