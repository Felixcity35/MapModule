package tk.felixcity.mapmodule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;


import android.graphics.Color;
import android.location.Location;
   // import android.location.LocationListener;    // remove this one and replace with the google location listener.
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleApiClient.ConnectionCallbacks
       ,GoogleApiClient.OnConnectionFailedListener , LocationListener

{

    private GoogleMap mMap;

   private GoogleApiClient googleApiClient ;
   private LocationRequest locationRequest ;
   private Location lstlocation ;
   private Marker currentUserLocationMaker ;
   private static final int RequestUserLocationCode = 99 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            checkLocationPermission() ;
        }

        SupportMapFragment    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @SuppressLint("NewApi")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {

            buildgoogleapiclient();


            mMap.setMyLocationEnabled(true);

   //         mMap.setOnMyLocationClickListener(this);

//            mMap.getUiSettings().setCompassEnabled(true);
//            mMap.getUiSettings().setZoomControlsEnabled(true);
//            mMap.setOnMyLocationButtonClickListener(this);

        }

    }


    public  boolean checkLocationPermission()
    {

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION} ,RequestUserLocationCode);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION} ,RequestUserLocationCode);
            }
            return false ;

        }
        else
        {
            return true ;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case RequestUserLocationCode :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient == null)
                        {
                            buildgoogleapiclient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied ...", Toast.LENGTH_SHORT).show();
                }
                return;

        }

    }

    protected  synchronized  void buildgoogleapiclient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                         .addConnectionCallbacks(this)
                         .addOnConnectionFailedListener(this)
                         .addApi(LocationServices.API)
                         .build();

           googleApiClient.connect();

    }


    @Override
    public boolean onMyLocationButtonClick()
    {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location)
    {

              lstlocation = location ;

              if (currentUserLocationMaker != null)
              {
                  currentUserLocationMaker.remove();
              }

              LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

              MarkerOptions markerOptions = new MarkerOptions() ;
               markerOptions.position(latLng);
               markerOptions.title("User  Current Location ") ;
               markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)) ;

               currentUserLocationMaker = mMap.addMarker(markerOptions);

               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
               mMap.animateCamera(CameraUpdateFactory.zoomBy(14));

               if (googleApiClient !=null)
               {
                   LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
               }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}