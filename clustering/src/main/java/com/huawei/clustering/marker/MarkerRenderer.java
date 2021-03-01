package com.huawei.clustering.marker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.clustering.SharedOnMarkerClickListener;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class MarkerRenderer<T> {

    private final Map<T, Marker> itemToMarkers = new HashMap<>();
    private final Map<Marker, T> markerToItems = new HashMap<>();

    @NonNull
    private final HuaweiMap huaweiMap;
    @NonNull
    private final MarkerManager.IconGenerator<T> iconGenerator;
    @NonNull
    private final MarkerManager.LatLngGenerator<T> latLngGenerator;

    private MarkerManager.ItemClickListener<T> clickListener = null;

    public MarkerRenderer(
            @NonNull HuaweiMap huaweiMap,
            @NonNull MarkerManager.IconGenerator<T> iconGenerator,
            @NonNull MarkerManager.LatLngGenerator<T> latLngGenerator,
            @NonNull SharedOnMarkerClickListener listener
    ) {
        this.huaweiMap = huaweiMap;
        this.iconGenerator = iconGenerator;
        this.latLngGenerator = latLngGenerator;
        listener.addListener(new HuaweiMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (clickListener == null)
                    return false;

                final T item = markerToItems.get(marker);
                if (item != null) {
                    clickListener.onItemClick(item);
                    return true;
                }

                return false;
            }
        });
        this.huaweiMap.setOnMarkerClickListener(listener);
    }


    void setOnClickListener(@Nullable MarkerManager.ItemClickListener<T> onClickListener) {
        clickListener = onClickListener;
    }

    void add(@NonNull Collection<T> items) {
        add(items, true);
    }

    private void add(@NonNull Collection<T> items, boolean isAnim) {
        final HashSet<T> toAdd = new HashSet<>(items);
        toAdd.removeAll(this.itemToMarkers.keySet());

        //animate
        for (T item: toAdd) {
            final BitmapDescriptor markerIcon = iconGenerator.getMarkerIcon(item);
            int FOREGROUND_MARKER_Z_INDEX = 1;
            final Marker markerToAdd = huaweiMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            latLngGenerator.getLang(item),
                            latLngGenerator.getLng(item)
                        ))
                    .icon(markerIcon)
                    .alpha(0.0F)
                    .zIndex(FOREGROUND_MARKER_Z_INDEX))
                    ;
            if (isAnim)
                animateMarkerAppearance(markerToAdd);
            else
                markerToAdd.setAlpha(1.0f);

            this.itemToMarkers.put(item, markerToAdd);
            this.markerToItems.put(markerToAdd, item);
        }
    }

    void remove(@NonNull Collection<T> items) {
        remove(items, true);
    }

    private void remove(@NonNull Collection<T> items, boolean isAnim) {
        final HashSet<T> toRemove = new HashSet<>(this.itemToMarkers.keySet());
        toRemove.retainAll(items);

        //animate
        for (T item: toRemove) {
            Marker markerToRemove = itemToMarkers.get(item);
            if (markerToRemove == null)
                return;

            if (isAnim) {
                animateWithMarkerRemove(markerToRemove);
            } else
                markerToRemove.remove();

            this.markerToItems.remove(markerToRemove);
            this.itemToMarkers.remove(item);
        }
    }

    void update(@NonNull Collection<T> items) {
        remove(items, false);
        add(items, false);
    }

    void clear() {
        remove(this.itemToMarkers.keySet());
        this.itemToMarkers.clear();
        this.markerToItems.clear();
    }

    private void animateMarkerAppearance(@NonNull Marker marker) {
        ObjectAnimator.ofFloat(marker, "alpha", 1.0F).start();
    }

    private void animateWithMarkerRemove(@NonNull final Marker marker) {
        int BACKGROUND_MARKER_Z_INDEX = 0;
        marker.setZIndex(BACKGROUND_MARKER_Z_INDEX);
        ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(marker, "alpha", 0.0F);
        fadeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                marker.remove();
            }
        });
        fadeAnimator.start();
    }

}
