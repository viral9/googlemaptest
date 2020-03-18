package com.example.mapofflinej;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
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

import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

public class map3 extends Fragment {

    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "Medina";

    private MapView mapView;
    private MapboxMap mapboxMap;
    private ProgressBar progressBar;
    private Button btnlist, btndown;
    private TextView tverror;

    private int regionselected;
    private boolean isEndNotified;


    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Mapbox.getInstance(getActivity().getApplicationContext(), "pk.eyJ1IjoidmlyYWwwIiwiYSI6ImNrN29tcm41cjAwMGczbm5yNWdsOGtzc2UifQ.mPi4-lhno5ovPozC8yvdSg");

        final View rootviewmap3 = inflater.inflate(R.layout.map3, container, false);
        mapView = rootviewmap3.findViewById(R.id.mapview3);
        progressBar = rootviewmap3.findViewById(R.id.probar_map3);
        btndown = rootviewmap3.findViewById(R.id.btn_download);
        btnlist = rootviewmap3.findViewById(R.id.btn_list);
        tverror = rootviewmap3.findViewById(R.id.tv_error);
        tverror = rootviewmap3.findViewById(R.id.tv_error);


        // mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap map) {
                mapboxMap = map;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        offlineManager = OfflineManager.getInstance(getActivity().getApplicationContext());
                        btndown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadRegionDialog();
                            }
                        });
                        btnlist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadlist();
                            }
                        });


                    }
                });

            }
        });


        return rootviewmap3;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void downloadlist() {
        regionselected = 0;
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(final OfflineRegion[] offlineRegions) {
                if(offlineRegions== null || offlineRegions.length == 0){
                    Toast.makeText(getActivity(), "no offline region", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> offlineRegionsarr = new ArrayList<>();
                for(OfflineRegion offlineRegion : offlineRegions)
                {
                    offlineRegionsarr.add(getRegionName(offlineRegion));
                }
                final CharSequence[] item = offlineRegionsarr.toArray(new CharSequence[offlineRegionsarr.size()]);

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("choose item")
                        .setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                regionselected = which;
                            }
                        })
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), item[regionselected], Toast.LENGTH_SHORT).show();
                                LatLngBounds bounds = (offlineRegions[regionselected].getDefinition()).getBounds();
                                double regionzoom = (offlineRegions[regionselected].getDefinition()).getMinZoom();
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(bounds.getCenter())
                                        .zoom(regionzoom)
                                        .build();
                                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                            }
                        })
                        .setNeutralButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setIndeterminate(true);
                                progressBar.setVisibility(View.VISIBLE);
                                //delete
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();


            }

            @Override
            public void onError(String error) {

            }
        });


    }

    private String getRegionName(OfflineRegion offlineRegion) {
        String regionname;
        try {
                byte[] metadata = offlineRegion.getMetadata();
                String json = new String(metadata,JSON_CHARSET);
                JSONObject jsonObject = new JSONObject(json);
                regionname = jsonObject.getString(JSON_FIELD_REGION_NAME);
        }catch (Exception e){
            regionname = String.format("notnot", offlineRegion.getID());
        }
        return regionname;
    }

    private void downloadRegionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText etdownlaod = new EditText(getActivity());
        builder.setTitle("on download")
                .setView(etdownlaod)
                .setMessage("enter name")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String regionname = etdownlaod.getText().toString();
                        if (regionname.length() == 0) {
                            Toast.makeText(getActivity().getApplicationContext(), "text is empty", Toast.LENGTH_SHORT).show();
                        } else {
                            downloadregion(regionname);

                        }
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();

    }

    private void downloadregion(final String regionname) {
        startprogress();

        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                String styleurl = style.getUri();
                LatLngBounds latLngBounds = mapboxMap.getProjection().getVisibleRegion().latLngBounds;
                double minzoom = mapboxMap.getCameraPosition().zoom;
                double maxzomm = mapboxMap.getMaxZoomLevel();
                float pixelration = getActivity().getResources().getDisplayMetrics().density;
                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(styleurl, latLngBounds, minzoom, maxzomm, pixelration);
                byte[] metadata;

                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON_FIELD_REGION_NAME, regionname);
                    String json = jsonObject.toString();
                    metadata = json.getBytes(JSON_CHARSET);
                } catch (Exception e) {
                    Timber.e("fail to generate metadata %s", e.getMessage());
                    metadata = null;
                }
                offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {

                        map3.this.offlineRegion = offlineRegion;
                        launchDownload();
                    }

                    @Override
                    public void onError(String error) {

                        Timber.e("error %s",error);
                    }
                });
            }
        });

    }

    private void startprogress() {

        btndown.setEnabled(false);
        btnlist.setEnabled(false);

        isEndNotified = false;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);



    }

    private void launchDownload() {

        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                double percentage  = status.getRequiredResourceCount() >=0 ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) : 0.0;

                if(status.isComplete()) {
                    endProgress("done");
                    return;
                }else if(status.isRequiredResourceCountPrecise()){
                    setPercenetage((int)Math.round(percentage));
                }

                Timber.d("%s/%s resources; %s bytes downloaded.",
                        String.valueOf(status.getCompletedResourceCount()),
                        String.valueOf(status.getRequiredResourceCount()),
                        String.valueOf(status.getCompletedResourceSize()));

            }

            @Override
            public void onError(OfflineRegionError error) {
                Toast.makeText(getActivity(), error.getReason(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Toast.makeText(getActivity(), "limint exceed "+limit, Toast.LENGTH_SHORT).show();
                tverror.setText("limit exceed");

            }
        });

        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

    }

    private void setPercenetage(int round) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(round);
    }


    private void endProgress(String done) {

        if(isEndNotified)
        {
            return;
        }
        btnlist.setEnabled(true);
        btndown.setEnabled(true);
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        Toast.makeText(getActivity(), done, Toast.LENGTH_SHORT).show();
    }

}