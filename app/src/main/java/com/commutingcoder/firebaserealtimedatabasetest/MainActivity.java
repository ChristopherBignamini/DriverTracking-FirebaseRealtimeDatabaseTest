package com.commutingcoder.firebaserealtimedatabasetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Position mMyPosition;
    private Position mOtherPosition;
    private Boolean mMyAudioStatus;
    private Boolean mOtherAudioStatus;
    private EditText mMyLatitudeEditText;
    private EditText mMyLongitudeEditText;
    private Button mUpdateMyPositionButton;
    private TextView mOtherLatitudeEditText;
    private TextView mOtherLongitudeEditText;
    private Button mUpdateOtherPositionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyAudioStatus = new Boolean(true);
        mOtherAudioStatus = new Boolean(false);
        mMyPosition = new Position(23.2,46.03);
        mOtherPosition = new Position(0.0,0.0);

        // Wire UI stuff
        mMyLatitudeEditText = (EditText) findViewById(R.id.my_latitude_text);
        mMyLongitudeEditText = (EditText) findViewById(R.id.my_longitude_text);
        mUpdateMyPositionButton = (Button) findViewById(R.id.update_my_position_button);
        mOtherLatitudeEditText = (TextView) findViewById(R.id.other_latitude_text);
        mOtherLongitudeEditText = (TextView) findViewById(R.id.other_longitude_text);
        mUpdateOtherPositionButton = (Button) findViewById(R.id.update_other_position_button);

        // Setup reference to rt database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference trackingReference = database.getReference("tracking_c1_c2");
        DatabaseReference myAudioStatusReference = trackingReference.child("audio_status").child("my_audio");
        DatabaseReference otherAudioStatusReference = trackingReference.child("audio_status").child("other_audio");
        final DatabaseReference myPositionReference = trackingReference.child("positions").child("my_position");
        DatabaseReference otherPositionReference = trackingReference.child("positions").child("other_position");

        // Initialize UI stuff
        mMyLatitudeEditText.setText(String.valueOf(mMyPosition.getLatitude()));
        mMyLatitudeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: check if better procedure exist
                try {
                    Double d = Double.parseDouble(s.toString());
                    mMyPosition.setLatitude(Double.valueOf(s.toString()));
                } catch (NumberFormatException ex) {
                    // Do something smart here...
                    mMyPosition.setLatitude(0.0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // TODO: check if better procedure exist
        mMyLatitudeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    mMyLatitudeEditText.setText(String.valueOf(mMyPosition.getLatitude()));
                }
            }
        });

        mMyLongitudeEditText.setText(String.valueOf(mMyPosition.getLongitude()));
        mMyLongitudeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMyPosition.setLongitude(Double.valueOf(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mOtherLatitudeEditText.setText(String.valueOf(mOtherPosition.getLatitude()));
        mOtherLongitudeEditText.setText(String.valueOf(mOtherPosition.getLongitude()));
        mUpdateMyPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set value for my position
                // TODO: have a look to custom java object usage
                Log.d(TAG, "My position is: " + mMyPosition.getLatitude() + " " + mMyPosition.getLongitude());
                myPositionReference.child("latitude").setValue(mMyPosition.getLatitude());
                myPositionReference.child("longitude").setValue(mMyPosition.getLongitude());

            }
        });
        mUpdateOtherPositionButton.setEnabled(false);// TODO: can we change color here?
        mUpdateOtherPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherLatitudeEditText.setText(String.valueOf(mOtherPosition.getLatitude()));
                mOtherLongitudeEditText.setText(String.valueOf(mOtherPosition.getLongitude()));
                mUpdateOtherPositionButton.setEnabled(false);
            }
        });


        // Set value for my audio status
        myAudioStatusReference.setValue(mMyAudioStatus);


//        // TODO: debug only
//        otherAudioStatusReference.setValue(mOtherAudioStatus);
//        otherPositionReference.child("latitude").setValue(mOtherPosition.getLatitude());
//        otherPositionReference.child("longitude").setValue(mOtherPosition.getLongitude());

        // Set other audio status value change event listener
        otherAudioStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Boolean value = dataSnapshot.getValue(Boolean.class);
                Log.d(TAG, "Other audio status value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Set other position value change event listener
        otherPositionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mOtherPosition.setLatitude((double) (dataSnapshot.child("latitude").getValue()));
                mOtherPosition.setLongitude((double) (dataSnapshot.child("longitude").getValue()));
                mUpdateOtherPositionButton.setEnabled(true);
                Log.d(TAG, "Other position is: " + mOtherPosition.getLatitude() + " " + mOtherPosition.getLongitude());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private class Position {

        public double mLatitude;
        public double mLongitude;

        public Position(double latitude, double longitude) {
            mLatitude = latitude;
            mLongitude = longitude;
        }

        public void setLatitude(double latitude) {
            mLatitude = latitude;
        }

        public void setLongitude(double longitude) {
            mLongitude = longitude;
        }

        public double getLatitude() {
            return mLatitude;
        }

        public double getLongitude() {
            return mLongitude;
        }
    }
}
