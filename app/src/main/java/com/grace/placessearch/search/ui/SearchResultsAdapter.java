package com.grace.placessearch.search.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grace.placessearch.common.PlacesSearchConstants;
import com.grace.placessearch.R;
import com.grace.placessearch.common.app.PlacesSearchPreferenceManager;
import com.grace.placessearch.data.model.Venue;
import com.grace.placessearch.common.util.PlacesSearchUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.VenueViewHolder> {

    private List<Venue> data = new ArrayList<>();
    private VenueListener listener;
    private PlacesSearchPreferenceManager placesPreferenceManager;
    private Context context;
    private Picasso picasso;

    public SearchResultsAdapter(Context context, VenueListener listener, PlacesSearchPreferenceManager placesPreferenceManager, Picasso picasso) {
        this.context = context;
        this.placesPreferenceManager = placesPreferenceManager;
        this.listener = listener;
        this.picasso = picasso;
    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new VenueViewHolder(inflater.inflate(R.layout.list_item_venue, parent, false));
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {
        holder.bind(context, data.get(position), listener, picasso, placesPreferenceManager);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Venue> batches) {
        this.data = new ArrayList<>(batches);
        notifyDataSetChanged();
    }

    public interface VenueListener {
        void onVenueItemClicked(Venue venue);
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.venue_container)
        public RelativeLayout venueContainer;

        @Bind(R.id.venue_category_image)
        public ImageView venueCategoryImage;

        @Bind(R.id.venue_name)
        public TextView venueName;

        @Bind(R.id.category_name)
        public TextView categoryName;

        @Bind(R.id.distance_to_user_location)
        public TextView distanceToUserLocation;

        @Bind(R.id.favorite_status)
        public ImageView favoriteStatusImage;

        @Bind(R.id.non_favorite_status)
        public ImageView nonFavoriteStatusImage;

        private View itemView;
        private boolean isFavorite;
        private boolean favoriteStatusNeedsUpdating;

        public VenueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            isFavorite = false;
            this.itemView = itemView;
        }

        private static void loadImage(Context context, ImageView imageView, Venue venue, Picasso picasso) {

            if (venue.getCategories().isEmpty() || venue.getCategories().get(0).getIcon() == null) {
                return;
            }

            final String imageUrl = venue.getListImgUrl();

            if (imageUrl == null) {
                imageView.setImageDrawable(context.getDrawable(android.R.drawable.stat_notify_error));
                return;
            }

            picasso.load(imageUrl).into(imageView);
        }

        public void bind(final Context context, final Venue data, final VenueListener listener, Picasso picasso, final PlacesSearchPreferenceManager placesPreferenceManager) {

            isFavorite = false;

            // Venue name
            venueName.setText(data.getName());

            // Category
            if (data.getCategories().size() >= 1) {
                categoryName.setText(data.getCategories().get(0).getName());
            } else {
                categoryName.setText(context.getString(R.string.unavailable));
            }

            // distance
            String distanceToUserStr = PlacesSearchUtil.getDistanceInMilesToUserLocation(data.getLocation());
            if (distanceToUserStr == null) {
                distanceToUserLocation.setText(context.getString(R.string.unavailable));
            } else {
                distanceToUserLocation.setText(distanceToUserStr + " miles");
            }

            // load category image
            loadImage(context, venueCategoryImage, data, picasso);

            venueContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onVenueItemClicked(data);
                }
            });
            setInitialFavoriteStatus(placesPreferenceManager, data);

            itemView.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
                @Override
                public void onWindowFocusChanged(boolean hasFocus) {
                    if (hasFocus && getFavoriteStatusNeedsUpdating()) {
                        refreshFavoriteStatus(data.getId(), placesPreferenceManager);
                        setFavoriteStatusNeedsUpdating(false);
                    }

                    // If losing focus(going to another activity perhaps), set to refresh fav status when focus is regained
                    if (!hasFocus) {
                        setFavoriteStatusNeedsUpdating(true);
                    }
                }
            });
        }

        private boolean getFavoriteStatusNeedsUpdating() {
            return this.favoriteStatusNeedsUpdating;
        }

        public void setFavoriteStatusNeedsUpdating(boolean needsUpdating) {
            this.favoriteStatusNeedsUpdating = needsUpdating;
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
                        .setDuration(PlacesSearchConstants.HEART_CROSS_FADE_ANIMATION_DURATION)
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
            int duration = PlacesSearchConstants.HEART_CROSS_FADE_ANIMATION_DURATION;
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
