package com.example.mapofflinej;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapofflinej.buttom_sheet_loaction_name.BottomSheetLisner;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.mapofflinej.global.placemarkArrayList;

public class map extends Fragment implements OnMapReadyCallback  {

    GoogleMap Map;

    //GoogleMap mgoogleMap;
    MapView mapView;
    ArrayList<LatLng> list_LatLng = new ArrayList<>();
    //ArrayList<placemark> placemarkArrayList = new ArrayList<>() ;

    BottomSheetBehavior bottomSheetBehavior;
    Button btnroute,btnchangeloc;
    TextView tvto,tvfrom;
    String selectedlocation;
    int ic = 0;
    String json = null;
    GoogleApiClient googleApiClient;

    boolean connection = false;


    protected synchronized void buildgoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
            .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) getActivity().getApplicationContext())
            .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) getActivity().getApplicationContext())
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.map, container, false);
        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        //mapFragment.getMapAsync(this);
        //list_LatLng.add()

        //return inflater.inflate(R.layout.map,container,false);


        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
            || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connection =true;

        }else {
            connection = false;
        }
        if (connection) {


            getdata();
            placemarkArrayList = new ArrayList<>();


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



            tvto = rootview.findViewById(R.id.tv_toloaction);
            tvfrom = rootview.findViewById(R.id.tv_fromloaction);
            btnroute = rootview.findViewById(R.id.btn_route);
            btnchangeloc = rootview.findViewById(R.id.btn_change_loc);
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
                    ic = 1;
                    dialoglistplaces();
                    //tvto.setText(selectedlocation);
                }
            });
            tvfrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ic = 2;
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
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }

            return rootview;
        }else {
            View noconnection = inflater.inflate(R.layout.noconnection,container,false);
            return noconnection;
        }

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
       // buildgoogleApiClient();

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

                LatLng latLng_MAP = new LatLng(longitude,latitude);
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
        LatLng mid = new LatLng(22.72943,39.102172);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(7)
                .target(mid)
                .tilt(10)
                .build();
        Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = getRequestUrl(placemarkArrayList.get(11),placemarkArrayList.get(1));

                TaskRequestDirection taskRequestDirection = new TaskRequestDirection();

                taskRequestDirection.execute(url);
            }
        },400);





    }

    private String getRequestUrl(placemark origin, placemark destination) {
        String str_origin = "origin=" + origin.latitude +"," + origin.longitude;

        String str_destination = "destination=" + destination.latitude+","+origin.longitude;

        //String str_origin = "origin=" + "23.014594" +"," + "72.530216";
        //String str_destination = "destination=" + "23.011781"+","+"72.502707";

        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_origin+"&"+str_destination+"&"+sensor+"&"+mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&key=AIzaSyBzT-JBlLadDGBT62ru5Z6igr09_T20wLg";
        return url;
    }

    private String requestDirection(String requrl) throws IOException {
        String response_str = " ";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(requrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = " ";

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            response_str = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return response_str;

    }

    public class TaskRequestDirection extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String responeString = " ";
            try {
                responeString = requestDirection(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responeString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParse taskParse = new TaskParse();
            taskParse.execute(s);

        }
    }

    public class TaskParse extends AsyncTask<String ,Void ,List<List<HashMap<String,String>>> >{


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject = null;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                routes = directionsJSONParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for(int i=0;i< lists.size();i++)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);
                for(int j =0 ; j< path.size();j++)
                {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }

            /*for(List<HashMap<String, String>> path : lists)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String, String> point : path)
                {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat,lng));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }*/

            if(polylineOptions!=null) {
                Map.addPolyline(polylineOptions);

            }else {
                Toast.makeText(getActivity().getApplicationContext(), "direction not found", Toast.LENGTH_SHORT).show();
            }



        }
    }



}
