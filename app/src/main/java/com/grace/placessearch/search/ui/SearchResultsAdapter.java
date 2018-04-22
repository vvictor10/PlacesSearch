package com.grace.placessearch.search.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grace.placessearch.R;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.util.PlacesSearchUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.VenueViewHolder> {

    private static final int crossFadeAnimationDuration = 150;

    private List<Venue> data = new ArrayList<>();
    private VenueListener listener;
    private PlacesSearchPreferenceManager placesPreferenceManager;
    private Context context;
    private Picasso picasso;

    public interface VenueListener {
        void onClick(Venue venue);
    }

    public SearchResultsAdapter(Context context, VenueListener listener, PlacesSearchPreferenceManager placesPreferenceManager, Picasso picasso) {
        this.context = context;
        this.placesPreferenceManager = placesPreferenceManager;
        this.listener = listener;
        this.picasso = picasso;
    }

    @Override
    public int getItemCount() {
        Timber.i("Data size %d", data.size());
        return data.size();
    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.i("called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new VenueViewHolder(inflater.inflate(R.layout.list_item_venue, parent, false));
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {
        Timber.i("position %d", position);
        holder.bind(data.get(position), listener, picasso, placesPreferenceManager);
    }

    public void updateData(List<Venue> batches) {
        this.data = new ArrayList<>(batches);
        Timber.i("data size %d", data.size());
        notifyDataSetChanged();
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.venue_container)
        public RelativeLayout venueContainer;

        @Bind(R.id.venue_image)
        public ImageView venueImage;

        @Bind(R.id.venue_name)
        public TextView venueName;

        @Bind(R.id.category_name)
        public TextView categoryName;

        @Bind(R.id.distance_to_center)
        public TextView distanceToCenter;

        @Bind(R.id.favorite_status)
        public ImageView favoriteStatusImage;

        @Bind(R.id.non_favorite_status)
        public ImageView nonFavoriteStatusImage;

        private boolean isFavorite;

        public VenueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            isFavorite = false;
        }

        public void bind(final Venue data, final VenueListener listener, Picasso picasso, PlacesSearchPreferenceManager placesPreferenceManager) {

            isFavorite = false;

            Timber.i("Binding data for venue - %s", data.getName());

            venueName.setText(data.getName());

            if (data.getCategories().size() >= 1) {
                categoryName.setText(data.getCategories().get(0).getName());
            } else {
                categoryName.setText("Not Available");
            }

            distanceToCenter.setText(PlacesSearchUtil.getDistanceInMiles(data));

            loadImage(venueImage, data, picasso);

            venueContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("Clicked");
                    listener.onClick(data);
                }
            });

            setInitialFavoriteStatus(placesPreferenceManager, data);
        }

        private static void loadImage(ImageView imageView, Venue venue, Picasso picasso) {

            if (venue.getCategories().isEmpty() || venue.getCategories().get(0).getIcon() == null) {
                return;
            }

            final String imageUrl = venue.getCategories().get(0).getIcon().getPrefix()
                    + "bg_88" + venue.getCategories().get(0).getIcon().getSuffix();

            picasso.load(imageUrl).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Timber.i("Image loaded for url %s", imageUrl);
                }

                @Override
                public void onError() {
                    Timber.w("Failed to load browse merch. zone page image");
                }
            });
        }

        private void refreshFavoriteStatus(String venueId, PlacesSearchPreferenceManager placesPreferenceManager) {
            boolean latestFavoriteStatus = PlacesSearchUtil.isFavorite(placesPreferenceManager, venueId);
            if (!isFavorite && latestFavoriteStatus) { // newly favorited
                isFavorite = latestFavoriteStatus;
                animateFavoriteStatus(false, favoriteStatusImage, nonFavoriteStatusImage);
            } else if (isFavorite && !latestFavoriteStatus) { // newly un-favorited
                isFavorite = latestFavoriteStatus;
                animateFavoriteStatus(false, nonFavoriteStatusImage, favoriteStatusImage);
            }
        }

        private void setInitialFavoriteStatus(final PlacesSearchPreferenceManager placesPreferenceManager, final Venue venue) {
            isFavorite = PlacesSearchUtil.isFavorite(placesPreferenceManager, venue.getId());
            if (isFavorite) {
                animateFavoriteStatus(true, favoriteStatusImage, nonFavoriteStatusImage);
            } else {
                animateFavoriteStatus(true, nonFavoriteStatusImage, favoriteStatusImage);
            }

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavorite = !isFavorite;
                    if (isFavorite) {
                        PlacesSearchUtil.addFavorite(placesPreferenceManager, venue.getId());
                        animateFavoriteStatus(false, favoriteStatusImage, nonFavoriteStatusImage);
                    } else {
                        PlacesSearchUtil.removeFavorite(placesPreferenceManager, venue.getId());
                        animateFavoriteStatus(false, nonFavoriteStatusImage, favoriteStatusImage);
                    }
                }
            };

            nonFavoriteStatusImage.setOnClickListener(onClickListener);
            favoriteStatusImage.setOnClickListener(onClickListener);
        }

        private void animateFavoriteStatus(boolean isInitAnimation, View viewToDisplay, final View viewToHide) {
            if (viewToHide.getVisibility() != View.INVISIBLE) {
                viewToHide.setVisibility(View.INVISIBLE);
            }
            if (viewToDisplay.getVisibility() != View.VISIBLE) {
                viewToDisplay.setVisibility(View.VISIBLE);
            }
        }

        private void animateFavoriteStatus1(boolean isInitAnimation, View viewToDisplay, final View viewToHide) {

            if (viewToHide.getVisibility() != View.VISIBLE) {
                return;
            }

            // Skip scale down animation for nonFavoriteStatusImage.
            if (viewToHide == favoriteStatusImage) {

                // Animate the 'view-to-hide' to 0% opacity. After the animation ends,
                // set its visibility to GONE as an optimization step (it won't
                // participate in layout passes, etc.)
                viewToHide.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .setDuration(crossFadeAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                viewToHide.setVisibility(View.GONE);
                            }
                        });
            }

            // Set the 'view-to-display' to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            viewToDisplay.setScaleX(0f);
            viewToDisplay.setScaleY(0f);
            viewToDisplay.setVisibility(View.VISIBLE);

            // Animate the 'view-to-display' to 100% opacity, and clear any animation
            // listener set on the view.

            // If displaying 'non-favorite' status or initial display of 'favorite'
            // status - animate alpha value instantly
            int duration = crossFadeAnimationDuration;
            if (viewToDisplay == nonFavoriteStatusImage || isInitAnimation) {
                duration = 0;
            }

            viewToDisplay.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration)
                    .setListener(null);
        }


    }


}
