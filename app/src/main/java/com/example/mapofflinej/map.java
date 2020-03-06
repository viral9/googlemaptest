package com.example.mapofflinej;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapofflinej.buttom_sheet_loaction_name.BottomSheetLisner;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class map extends Fragment implements OnMapReadyCallback  {

    GoogleMap Map;

    //GoogleMap mgoogleMap;
    MapView mapView;
    ArrayList<LatLng> list_LatLng = new ArrayList<>();
    ArrayList<placemark> placemarkArrayList = new ArrayList<>() ;

    BottomSheetBehavior bottomSheetBehavior;
    Button btnroute,btnchangeloc;
    TextView tvto,tvfrom;
    String selectedlocation;
    int ic = 0;
    String json = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        //mapFragment.getMapAsync(this);
        //list_LatLng.add()

        //return inflater.inflate(R.layout.map,container,false);

        getdata();

       /* View rootview = inflater.inflate(R.layout.map,container,false);
        mapView = rootview.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
             mgoogleMap = googleMap;
            // mgoogleMap.setMyLocationEnabled(true);
             LatLng latLng = new LatLng(23.027375,72.506706);
             mgoogleMap.addMarker(new MarkerOptions().position(latLng).title("ohhh"));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(12)
                        .build();
                mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootview;*/



        View rootview = inflater.inflate(R.layout.map,container,false);
        tvto = rootview.findViewById(R.id.tv_toloaction);
        tvfrom = rootview.findViewById(R.id.tv_fromloaction);
        btnroute = rootview.findViewById(R.id.btn_route);
        btnchangeloc =rootview.findViewById(R.id.btn_change_loc);
        btnchangeloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                temp = tvto.getText().toString();
                tvto.setText(tvfrom.getText().toString());
                tvfrom.setText(temp);

            }
        });
        tvto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(!tvto.getText().equals("hello")) {
                    tvto.setText(selectedlocation);
                }*/
                ic=1;
                dialoglistplaces();
                //tvto.setText(selectedlocation);
            }
        });
        tvfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ic=2;
                dialoglistplaces();
                //tvfrom.setText(selectedlocation);

            }
        });
        /*tvto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttom_sheet_loaction_name buttomSheetLoactionName = new buttom_sheet_loaction_name();
                buttomSheetLoactionName.setCallback(new BottomSheetLisner() {
                    @Override
                    public void OnClickText(String text) {
                        tvto.setText(text);
                    }
                });

                buttomSheetLoactionName.show(getActivity().getSupportFragmentManager(),"sheetbartext");
            }
        });*/

        View bottomsheet = rootview.findViewById(R.id.bottomsheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);

        mapView = rootview.findViewById(R.id.mapView);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

       return rootview;

    }

    private void dialoglistplaces() {

        final ArrayList<String> titlename = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("place names");
        for(int i=0;i<placemarkArrayList.size();i++)
        {
            titlename.add(placemarkArrayList.get(i).getTitle());
        }
        builder.setItems(titlename.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String titlenamesel = titlename.get(which);
                     //tvto.setText(titlenamesel);

                        if(ic==1)
                        {
                            tvto.setText(titlenamesel);
                        }
                        else if(ic==2)
                        {
                            tvfrom.setText(titlenamesel);
                        }
                    //selectedlocation = titlenamesel;
                     dialog.dismiss();

            }
        });
        builder.show();

    }

    private void getdata() {
        //String json;
        //R.drawable.

        try {
            //InputStream loc = getActivity().getAssets().open("placemarks.txt");
            InputStream loc = getContext().getAssets().open("placemarks.txt");
            int size = loc.available();
            byte[] buffer = new byte[size];
            loc.read(buffer);
            loc.close();
            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        Map = googleMap;

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("locations");
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject locjson = jsonArray.optJSONObject(i);
                String origin = locjson.optString("origin");
                String title = locjson.optString("title");
                String type = locjson.optString("type");
                double latitude = locjson.optDouble("latitude");
                double longitude = locjson.optDouble("longitude");

                /*placemarkArrayList.add(origin,title,type,latitude,longitude);
                placemarkArrayList.get(i).setTitle(title);
                placemarkArrayList.get(i).setType(type);
                placemarkArrayList.get(i).setLatitude(latitude);
                placemarkArrayList.get(i).setLongitude(longitude);*/

                placemark placemarkobj = new placemark();
                placemarkobj.setOrigin(origin);
                placemarkobj.setType(type);
                placemarkobj.setTitle(title);
                placemarkobj.setLatitude(latitude);
                placemarkobj.setLongitude(longitude);
                placemarkArrayList.add(placemarkobj);

               // String titl = placemarkArrayList.get(i).getTitle();

                LatLng latLng_MAP = new LatLng(latitude,longitude);
                Map.addMarker(new MarkerOptions().position(latLng_MAP).title(title).snippet(type));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //LatLng latLng_BATNUL_WAADI = new LatLng(39.96854067,21.35699136);
        //LatLng latLng_MINA_CAMP = new LatLng(39.88321424,21.41417986);

        Map.getUiSettings().setZoomControlsEnabled(true);
        Map.getUiSettings().setZoomGesturesEnabled(true);


        //Map.addMarker(new MarkerOptions().position(latLng_MINA_CAMP).title("MINA CAMP").snippet("house"));
        //Map.addMarker(new MarkerOptions().position(ahme).title("mycust").snippet("yeh jaga hamara he").icon(BitmapDescriptorFactory.fromResource(R.drawable.pintwo)));
        //Map.addMarker(new MarkerOptions().position(latLng_BATNUL_WAADI).title("BATNUL WAADI").snippet("flag"));
        //Map.moveCamera(CameraUpdateFactory.newLatLng(ahme));
//        Map.getMyLocation();
        LatLng mid = new LatLng(39.102172,22.72943);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(7)
                .target(mid)
                .tilt(10)
                .build();
        Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        String url = getRequestUrl(placemarkArrayList.get(0),placemarkArrayList.get(1));

    }

    private String getRequestUrl(placemark origin, placemark destination) {
        return null;
    }


}
