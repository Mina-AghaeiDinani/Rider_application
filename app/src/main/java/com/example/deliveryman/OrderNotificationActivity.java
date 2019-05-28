package com.example.deliveryman;

import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryman.directionHelper.FetchURL;
import com.example.deliveryman.directionHelper.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OrderNotificationActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static String orderId, customerId, restaurantId;
    private DatabaseReference mRefCustomerLocation;
    private DatabaseReference mRefRestaurantLocation;
    private DatabaseReference mRefOrderInfo;
    //Location
    private static Double latA;
    private static Double latB;
    private static Double lngA;
    private static Double lngB;

    //Map
    private GoogleMap mMap;
    private static MarkerOptions customerPlace, restaurantPlace;
    private Polyline currentPolyline;
    //Views
    TextView tvRestaurantName, tvCustomerName, tvDistance, tvFee, tvDate, tvTime;
    ImageView imgCustomer, imgRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);
        //Defines Views
        tvRestaurantName = findViewById(R.id.tvResNameNot);
        tvCustomerName = findViewById(R.id.tvCusNameNot);
        tvDate = findViewById(R.id.tvDateNot);
        tvDistance = findViewById(R.id.tvTotalDistanceNot);
        tvTime = findViewById(R.id.tvTimeNot);
        tvFee = findViewById(R.id.tvTotalFeeNot);
        imgCustomer = findViewById(R.id.imgCustomerNot);
        imgRestaurant = findViewById(R.id.imgRestaurantNot);
        //give order Id to variable orderId
        //get reference of Order,just to test I gave it to
        orderId = "-Lfe40j4WM43u2Irm7Wl";

        mRefOrderInfo = FirebaseDatabase.getInstance()
                .getReference("OrderInfo").child(orderId);
        //Location of customer
        mRefOrderInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final CartInfo cartInfo = dataSnapshot.getValue(CartInfo.class);

                //Assign values to each view
                tvCustomerName.setText(cartInfo.getCustomerName());
                tvRestaurantName.setText(cartInfo.getRestaurantName());
                restaurantId = cartInfo.getRestaurantId();
                customerId = cartInfo.getCustomerId();
                Picasso.get()
                        .load(cartInfo.getRestaurantImage())
                        .placeholder(R.drawable.logo_restaurant)
                        .fit()
                        .centerCrop()
                        .into(imgRestaurant);
                Picasso.get()
                        .load(cartInfo.getCustomerImage())
                        .placeholder(R.drawable.customer_unknown)
                        .fit()
                        .centerCrop()
                        .into(imgCustomer);
                //get references to get Latitude and Longitude
                mRefCustomerLocation = FirebaseDatabase.getInstance()
                        .getReference("CustomersLocation").child(customerId);

                //Location of customer
                mRefCustomerLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                        latA = locationOfPlaces.getLat();
                        lngA = locationOfPlaces.getLng();

                        //....................
                        //get reference of restaurant
                        mRefRestaurantLocation = FirebaseDatabase.getInstance()
                                .getReference("RestaurantsLocation").child(restaurantId);
                        //Location of restaurant
                        mRefRestaurantLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                                latB = locationOfPlaces.getLat();
                                lngB = locationOfPlaces.getLng();
                                //Compute distance

                                double earthRadius = 6371;
                                double latDiff = Math.toRadians(latB - latA);
                                double lngDiff = Math.toRadians(lngB - lngA);
                                double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                                        Math.cos(Math.toRadians(latA)) *
                                                Math.cos(Math.toRadians(latB)) * Math.sin(lngDiff / 2) *
                                                Math.sin(lngDiff / 2);
                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                double distance = earthRadius * c;
                                Double Fee = distance * 0.5;
                                //Assign to field
                                tvDistance.setText(String.format("%.2f", distance) + " Km");
                                tvFee.setText(String.format("%.2f", Fee) + "$");
                                position();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        Button btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //..............
            }
        });


    }

    private void position() {
        if (latA == null || latB == null || lngA == null || lngB == null)
            return;
        customerPlace = new MarkerOptions().position(new LatLng(latA, lngA)).title("Customer Place");
        restaurantPlace = new MarkerOptions().position(new LatLng(latB, lngB)).title("Restaurant Place");
        new FetchURL(OrderNotificationActivity.this).execute(getUrl(customerPlace.getPosition(), restaurantPlace.getPosition(), "walking"), "walking");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearByNot);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(customerPlace);
        mMap.addMarker(restaurantPlace);
        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPlace.getPosition(), zoomLevel));

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(customerPlace.getPosition().latitude, customerPlace.getPosition().longitude))
                .radius(10000)
                .strokeColor(Color.parseColor("#2271cce7"))
                .fillColor(Color.parseColor("#2271cce7")));
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_api_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void readCustomerLocation() {
        mRefCustomerLocation = FirebaseDatabase.getInstance()
                .getReference("CustomersLocation").child(customerId);
        //Location of customer
        mRefCustomerLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                latA = locationOfPlaces.getLat();
                lngA = locationOfPlaces.getLng();
                // Toast.makeText(OrderNotificationActivity.this, "" + latA, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void readRestaurantLocation() {
        mRefRestaurantLocation = FirebaseDatabase.getInstance()
                .getReference("RestaurantsLocation").child(restaurantId);
        //Location of customer
        mRefRestaurantLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationOfPlaces locationOfPlaces = dataSnapshot.getValue(LocationOfPlaces.class);
                latB = locationOfPlaces.getLat();
                lngB = locationOfPlaces.getLng();
                //  Toast.makeText(OrderNotificationActivity.this, "" + latB, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }


}
