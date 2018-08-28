package udev.com.ride;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener,
        GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationSource.OnLocationChangedListener,DirectionFinderListener {


    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    LinearLayout placePicker;
    public static final String TAG = "PlacePicker";
    private LocationRequest locationRequest;
    private Location lastLocation;
    private GoogleMap mMap;
    private Boolean mapReady;
    private GoogleApiClient apiClient;
    private PlaceAutocompleteFragment autocompleteFragment;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient mFusedLocation;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,userRefer;
    private double latitude,longitude;
    String username;
    private CircleImageView profile_pic;
    private TextView user_name_textview;
    private TextView user_email_textview;
    private Button signOutBtn;
    private String destinationName;
    private String originName;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ValueEventListener mValueEventListener;
    private double engagedLati,engagedLong;
    String txt;
    PostOnDatabase postOnDatabase;
    PinList pinList;
    String result;
    private ArrayList<PinList> pin_array;
    private CustomAdapter adapter;
    ListView pin_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);//to disable name of the app on head

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton pin_btn = (FloatingActionButton) findViewById(R.id.pin_button);

        fab.setVisibility(View.GONE);

        FloatingActionButton contact_list = (FloatingActionButton) findViewById(R.id.friends);
        contact_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ContactActivity.class));
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        pin_array = new ArrayList<PinList>();

        adapter = new CustomAdapter(this,pin_array);

        //google apiclient to use gogle maps api
        apiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)//google api client for google location services
                .addOnConnectionFailedListener(this).addApi(AppIndex.API).build();

        //in navigatiion header items need to be addressed through navigation view
        profile_pic=(CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.profile_pic);

        user_name_textview = (TextView)navigationView.getHeaderView(0).findViewById(R.id.username);

        user_email_textview=(TextView)navigationView.getHeaderView(0).findViewById(R.id.userid);


        //sign out functionality
        signOutBtn = (Button)navigationView.getHeaderView(0).findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        //fusedlocation api to get in mapready()
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        //place autocomplette fragment.... search bar to give auto complete location
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Enter the place");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLngDestination = place.getLatLng();
                double latitudeDestinationName = latLngDestination.latitude;
                double longitudeDestinationName = latLngDestination.longitude;
                destinationName = (String.valueOf(latitudeDestinationName) + "," + (String.valueOf(longitudeDestinationName)));
                Log.i(TAG, "Place: " + place.getName());
                Toast.makeText(getApplicationContext(),place.getAddress(),Toast.LENGTH_LONG).show();
                sendRequest();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //referencing database references in the firebase realtime database
        reference = database.getReference("users");

        userRefer = database.getReference("userlist");


        postOnDatabase = new PostOnDatabase();

        //Firebase authentication instantiating
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        //checking if the app is logged in
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        }else {
            postOnDatabase.setUsername(user.getEmail());
            username = postOnDatabase.getUsername();
            Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();

            user_name_textview.setText(user.getDisplayName());
            user_email_textview.setText(user.getEmail());

            //used Github package for glide in to get photo from online content
            Glide.with(getApplicationContext()).load(user.getPhotoUrl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profile_pic);

            postOnDatabase.userlist(username);

            postOnDatabase.setStatus("Online");

            postOnDatabase.setRequested("");

            postOnDatabase.setSearched("");

            postOnDatabase.setEngage("");

            postOnDatabase.setGivelocation("");

            UserDetail userDetail = new UserDetail("Online","goog","1223","7999","jhkkkk");

            reference.push().child("use").setValue(userDetail);

        }

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,Object> values = (Map<String, Object>) dataSnapshot.child(username).getValue();



                final String engaged = (String) values.get("Engage");
                final String value = (String) values.get("Requested");
                final String givelocation = (String) values.get("GiveLocation");
                if(!value.equals("")){
                    postOnDatabase.setRequested("");

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.request_window,null);

                    TextView userName = (TextView) view.findViewById(R.id.userIdTextView);
                    Button acceptRequest = (Button)view.findViewById(R.id.acceptRequest);
                    Button declineRequest = (Button)view.findViewById(R.id.declineRequest);

                    builder.setView(view);
                    final AlertDialog dialog = builder.create();

                    userName.setText(value + " ,requested for location");

                    acceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            postOnDatabase.setEngage(value);

                            dialog.dismiss();
                        }
                    });

                    declineRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            postOnDatabase.setRequested("");

                            dialog.dismiss();
                        }
                    });

                if(!isFinishing())
                    dialog.show();


                }
                if(!engaged.equals("")){

                    postOnDatabase.setEngage("");



                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.request_window,null);

                    TextView userName = (TextView) view.findViewById(R.id.userIdTextView);
                    Button acceptRequest = (Button)view.findViewById(R.id.acceptRequest);
                    Button declineRequest = (Button)view.findViewById(R.id.declineRequest);

                    userName.setText("Start Getting Location?");

                    builder.setView(view);
                    final AlertDialog dialog = builder.create();
                    acceptRequest.setText("Start");
                    acceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            postOnDatabase.setGivelocation(engaged);
                            postOnDatabase.setEngage("");
                            dialog.dismiss();

                        }
                    });

                    declineRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    });

                    if(!isFinishing())
                    dialog.show();

                }
                if(givelocation!=""){
                    engagedLati = (double)dataSnapshot.child(givelocation).child("Location").child("Latitude").getValue();
                    engagedLong = (double)dataSnapshot.child(givelocation).child("Location").child("Longitude").getValue();

                    MarkerOptions mark = new MarkerOptions().position(new LatLng(engagedLati,engagedLong)).draggable(true).title("mine").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_on_black_24dp));

                    mMap.addMarker(mark);

                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            destinationName = (String.valueOf(engagedLati) + "," + (String.valueOf(engagedLong)));
                            sendRequest();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(mValueEventListener);


        pin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPinnedLocation();

            }
        });


    }

    private void getPinnedLocation() {
        Thread thread = new Thread(){

            @Override
            public void run() {
                super.run();

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                result = null;
                try {
                    List<android.location.Address> addreses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addreses != null && addreses.size() > 0) {
                        Log.e("Adress", String.valueOf(addreses));
                        Address address = addreses.get(0);

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(",");
                        }
                        result = sb.toString();
                        pinList = new PinList(result,(String.valueOf(latitude)),String.valueOf(longitude));
                        Log.e("address :",result);

                        reference.child(username).child("Pinned").push().setValue(pinList);

                    }
                }catch (IOException e){
                    Log.e("Geocoder exception","Geocoder not set");
                }
            }
        };
        thread.start();

    }

    /*this whole onstop() ondestroy() onstop() onpostresume()
    are being used to check and update the status of the user
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        postOnDatabase.setStatus("Offline");
    }


    @Override
    protected void onStart() {
        super.onStart();

        apiClient.connect();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        postOnDatabase.setStatus("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
        postOnDatabase.setStatus("Offline");
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.GREEN).
                    width(20);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }
    
    private void sendRequest() {
        String origin = originName;
        String destination = destinationName;
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==R.id.profile_info){
            drawer.openDrawer(Gravity.LEFT);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id != R.id.navigation_map) {
            try {
                placePicker.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        if(id == R.id.normal_map){

            mMap.clear();//clearing the map for all the markers or pinned

        }else if (id == R.id.nav_share) {

        } else if (id == R.id.navigation_map) {
            placePicker = ((LinearLayout) findViewById(R.id.placePicker));
            placePicker.setVisibility(View.VISIBLE);
        } else if (id == R.id.realtime_location){
            requestDialog();
        } else if (id == R.id.pinned_location){
            pin_location_alert();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pin_location_alert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.pin_list,null);

        pin_list_view = (ListView)view.findViewById(R.id.pin_list_view);

        pin_list_view.setAdapter(adapter);

        builder.setView(view);

        AlertDialog dialog = builder.create();

        reference.child(username).child("Pinned").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_pin_list(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_pin_list(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.show();
    }

    private void append_pin_list(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            String pin_name = (String)((DataSnapshot)i.next()).getValue();
            String pin_lat = (String)((DataSnapshot)i.next()).getValue();
            String pin_lon = (String)((DataSnapshot)i.next()).getValue();
            PinList pin = new PinList(pin_name,pin_lat,pin_lon);

            adapter.add(pin);
        }
    }

    //to prepare the alert dialog for entering username
    private void requestDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog,null);

        final EditText editEmail = (EditText)view.findViewById(R.id.editEmail);
        Button requestLocation = (Button)view.findViewById(R.id.requestLocation);
        Button cancelDialog = (Button)view.findViewById(R.id.cancel);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        requestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = editEmail.getText().toString();
                if(txt.isEmpty()){
                    Toast.makeText(getApplicationContext(),"please enter a username!!",Toast.LENGTH_LONG).show();
                }else{
                    findUser(dialog);
                }
            }
        });

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                navigationView.setCheckedItem(R.id.normal_map);

            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {

                    navigationView.setCheckedItem(R.id.normal_map);
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });


    }

    private void findUser(final AlertDialog dialog) {
        userRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (((String) dataSnapshot.child(txt).getValue()).equalsIgnoreCase(txt)) {
                        dialog.dismiss();
                        status_definer(txt);
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"not found!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void status_definer(final String userSearched) {


        reference.child(txt).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String status;

                status = (String) dataSnapshot.getValue();

                builder.setTitle(userSearched).setMessage("Status : "+status);

                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        postOnDatabase.setRequested(userSearched);


                    }
                });
                builder.setNegativeButton("decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);


                AlertDialog dialog = builder.create();
                if(!isFinishing())
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mapReady = true;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.ambuish);
        googleMap.setMapStyle(styleOptions);//google is styled... get reference from mapstyle.withgoogle.com


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                CameraPosition cameraPos = CameraPosition.builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(17).tilt(44).bearing(0).build();//position of caera to be on map

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 2000, null);//animationn of movement of camera


            }
        });
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (mapReady) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            originName =latitude + ","+longitude;

            postOnDatabase.setLocation(latitude,longitude);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


}
