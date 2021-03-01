package com.huawei.clustering.marker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.clustering.SharedOnMarkerClickListener;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.BitmapDescriptor;

import java.util.List;

public final class MarkerManager<T> {

    public interface IconGenerator<T> {

        @NonNull BitmapDescriptor getMarkerIcon(@NonNull T marker);

    }

    public interface LatLngGenerator<T> {

        double getLang(T item);

        double getLng(T item);

    }

    public interface ItemClickListener<T> {

        void onItemClick(T item);

    }

    private final MarkerRenderer<T> renderer;

    public MarkerManager(
            @NonNull HuaweiMap huaweiMap,
            @NonNull IconGenerator<T> iconGenerator,
            @NonNull LatLngGenerator<T> latLngGenerator,
            @NonNull SharedOnMarkerClickListener listener
    ) {
        renderer = new MarkerRenderer<>(huaweiMap, iconGenerator, latLngGenerator, listener);
    }

    public void setOnItemClickListener(@Nullable ItemClickListener<T> listener) {
        renderer.setOnClickListener(listener);
    }

    public void addItems(@NonNull List<T> items) {
        renderer.add(items);
    }

    public void removeItems(@NonNull List<T> items) {
        renderer.remove(items);
    }

    public void updateItems(@NonNull List<T> items) {
        renderer.update(items);
    }

    public void clear() {
        renderer.clear();
    }


}
