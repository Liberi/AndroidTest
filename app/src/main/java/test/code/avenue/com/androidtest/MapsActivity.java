package test.code.avenue.com.androidtest;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity  implements LocationListener {

    private ArrayList<String> placeName;
    private ArrayList<String> geometry;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMap();

        Bundle extras = getIntent().getExtras();

        position  = extras.getInt("position");
        placeName = extras.getStringArrayList("listPlaces");
        geometry  = extras.getStringArrayList("listCoord");

        try {
            JSONObject newGeometry;
            for (int i = 0; i < geometry.size(); i++) {

                newGeometry = new JSONObject(geometry.get(i));

                mapFragment.getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(newGeometry.getDouble("lat"), newGeometry.getDouble("lng")))
                        .title(placeName.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

                newGeometry = new JSONObject(geometry.get(position));

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(newGeometry.getDouble("lat"), newGeometry.getDouble("lng")));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);

                mapFragment.getMap().moveCamera(center);
                mapFragment.getMap().animateCamera(zoom);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(Location location) {
    }

}