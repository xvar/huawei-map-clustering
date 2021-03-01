package com.huawei.clustering;

import androidx.annotation.NonNull;

import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SharedOnMarkerClickListener implements HuaweiMap.OnMarkerClickListener {

    private final List<HuaweiMap.OnMarkerClickListener> listeners = Collections.synchronizedList(
            new ArrayList<HuaweiMap.OnMarkerClickListener>()
    );

    public void addListener(@NonNull HuaweiMap.OnMarkerClickListener listener) {
        listeners.add(listener);
    }

    public void removeListener(@NonNull HuaweiMap.OnMarkerClickListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        boolean result = false;
        for (HuaweiMap.OnMarkerClickListener l: listeners) {
            result = result || l.onMarkerClick(marker);
        }
        return result;
    }
}
