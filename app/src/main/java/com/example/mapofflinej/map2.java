package com.example.mapofflinej;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.offline.model.NotificationOptions;
import com.mapbox.mapboxsdk.plugins.offline.model.OfflineDownloadOptions;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflinePlugin;
import com.mapbox.mapboxsdk.plugins.offline.utils.OfflineUtils;

import org.json.JSONObject;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class map2 extends Fragment {

    MapView mapView;

    //OfflineManager offlineManager;
    private boolean isEndNotified;
    OfflineManager offlineManager;
    ProgressBar progressBar;
    TextView tvprogress;
    OfflineTilePyramidRegionDefinition definition;
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "madina";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity(),"pk.eyJ1IjoidmlyYWwwIiwiYSI6ImNrN29tcm41cjAwMGczbm5yNWdsOGtzc2UifQ.mPi4-lhno5ovPozC8yvdSg");
         final View rootviewmap2 = inflater.inflate(R.layout.map2,container,false);




        mapView = rootviewmap2.findViewById(R.id.mapview);

        tvprogress = rootviewmap2.findViewById(R.id.tv_percent);
        tvprogress.setText("hello");




        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                if (definition == null) {
                mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {


                            //OfflineManager   offlineManager = new OfflineManager.getInstance(MainActivity.this);
                            offlineManager = OfflineManager.getInstance(getActivity().getApplicationContext());
                            /*LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    //.include(new LatLng(24.479563, 39.491861))
                                    //.include(new LatLng(24.265036, 39.870893))
                                    .include(new LatLng(24.707693,39.865602))
                                    .include(new LatLng(24.247110,39.381806))
                                    .build();*/
                        LatLngBounds latLngBounds = mapboxMap.getProjection().getVisibleRegion().latLngBounds;
                            definition = new OfflineTilePyramidRegionDefinition(
                                    style.getUri(),
                                    latLngBounds,
                                    10,
                                    15,
                                    getActivity().getApplicationContext().getResources().getDisplayMetrics().density);

                            byte[] metadata = new byte[0];
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(JSON_FIELD_REGION_NAME, "Medina");
                                String json = jsonObject.toString();
                                metadata = json.getBytes(JSON_CHARSET);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            NotificationOptions notificationOptions = NotificationOptions.builder(getApplicationContext())
                                    .smallIconRes(R.drawable.mapbox_logo_icon)
                                    .returnActivity(MainActivity.class.getName())
                                    .build();

                            OfflinePlugin.getInstance(getApplicationContext()).startDownload(

                                    OfflineDownloadOptions.builder()
                                            //.metadata(metadata)
                                            .metadata(OfflineUtils.convertRegionName("Medina"))
                                            .definition(definition)
                                            .notificationOptions(notificationOptions)
                                            .build()

                            );


                            if (metadata != null) {
                                offlineManager.createOfflineRegion(
                                        definition,
                                        metadata,
                                        new OfflineManager.CreateOfflineRegionCallback() {
                                            @Override
                                            public void onCreate(OfflineRegion offlineRegion) {



                                                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                                                progressBar = rootviewmap2.findViewById(R.id.probar);
                                                startProgress();



                                                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                                    @Override
                                                    public void onStatusChanged(OfflineRegionStatus status) {
                                                        double persentage = status.getRequiredResourceCount() >= 0 ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) : 0.0;
                                                        final double newper = persentage;
                                                        if (status.isComplete()) {
                                                            endProgress("done");
                                                        } else if (status.isRequiredResourceCountPrecise()) {
                                                            setPersentage((int) Math.round(persentage));
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    tvprogress.setText(String.valueOf(newper));
                                                                }
                                                            });

                                                        }

                                                    }





                                                    @Override
                                                    public void onError(OfflineRegionError error) {
                                                        Toast.makeText(getActivity().getApplicationContext(), error.getReason(), Toast.LENGTH_SHORT).show();
                                                        tvprogress.setText(error.getReason());
                                                    }

                                                    @Override
                                                    public void mapboxTileCountLimitExceeded(long limit) {
                                                        Toast.makeText(getActivity().getApplicationContext(), "exceed" + limit, Toast.LENGTH_SHORT).show();
                                                        tvprogress.setText("exceed"+String.valueOf(limit));
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onError(String error) {
                                                Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }



                    }
                });
                        }





                //mapView = mapboxMap;

                /*OfflineManager offlineManager = OfflineManager.getInstance(MainActivity.this);
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(37.7897, -119.5073))
                        .include(new LatLng(37.6744, -119.6815))
                        .build();

                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                        Style.getUri,latLngBounds,10,20,MainActivity.this.getResources().getDisplayMetrics().density);
                );*/

                //LatLng latLng = new LatLng(37.7897, -119.5073);
                //mapboxMap.addMarker(new MarkerOptions().position(latLng).title("find").snippet("yoho"));
                /*LatLng latLng = new LatLng(23.027375,72.506706);
                mapboxMap.getUiSettings().setQuickZoomGesturesEnabled(true);
                mapboxMap.addMarker(new MarkerOptions().position(latLng).title("sysdny"));
                //mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
                //mapboxMap.getUiSettings().setZoomControlEnabled(true);

                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"));*/
                //LatLng latLng = new LatLng(23.027375,72.506706);
                //LatLng latLng = new LatLng(21.355002,39.984443);
                //mapboxMap.addMarker(new MarkerOptions().position(latLng).title("find").snippet("yoho"));

                for(int i =0;i<global.placemarkArrayList.size();i++)
                {
                    LatLng latLng = new LatLng(global.placemarkArrayList.get(i).longitude,global.placemarkArrayList.get(i).latitude);
                    mapboxMap.addMarker(new MarkerOptions().position(latLng).title(global.placemarkArrayList.get(i).title).snippet(global.placemarkArrayList.get(i).type));
                }
                    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {

                            String makrname =  marker.getTitle();
                            for(int i =0;i<global.placemarkArrayList.size();i++)
                            {
                                if(makrname.equals(global.placemarkArrayList.get(i).getTitle())) {
                                    LatLng latLng = new LatLng(global.placemarkArrayList.get(i).longitude, global.placemarkArrayList.get(i).latitude);
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(latLng)
                                            .zoom(15)
                                            .tilt(20)
                                            .build();
                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1000);
                                }
                            }

                            return false;
                        }
                    });

                LatLng latLng = new LatLng(24.450495, 39.624884);
                //mapboxMap.addMarker(new MarkerOptions().position(latLng).title("find").snippet("yoho"));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(10)
                        .tilt(20)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1000);
            }
        });
        return rootviewmap2;

    }

    private void setPersentage(final int persentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(persentage);
        //tvprogress.setText(persentage);

    }

    private void endProgress(final String message) {
        if(isEndNotified)
        {
            return;
        }
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void startProgress() {
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);


    }



    }


