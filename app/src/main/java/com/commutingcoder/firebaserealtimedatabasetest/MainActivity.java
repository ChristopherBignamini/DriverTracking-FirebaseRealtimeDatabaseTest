package com.commutingcoder.firebaserealtimedatabasetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyAudioStatus = new Boolean(true);
        mOtherAudioStatus = new Boolean(false);
        mMyPosition = new Position(23.2,46.03);
        mOtherPosition = new Position(1.9,1.7);

        // Setup reference to rt database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference trackingReference = database.getReference("tracking_c1_c2");
        DatabaseReference myAudioStatusReference = trackingReference.child("audio_status").child("my_audio");
        DatabaseReference otherAudioStatusReference = trackingReference.child("audio_status").child("other_audio");
        DatabaseReference myPositionReference = trackingReference.child("positions").child("my_position");
        DatabaseReference otherPositionReference = trackingReference.child("positions").child("other_position");

        // Set value for my audio status
        myAudioStatusReference.setValue(mMyAudioStatus);

        // Set value for my position
        // TODO: have a look to custom java object usage
        myPositionReference.child("latitude").setValue(mMyPosition.getLatitude());
        myPositionReference.child("longitude").setValue(mMyPosition.getLongitude());

        // TODO: debug only
        otherAudioStatusReference.setValue(mOtherAudioStatus);
        otherPositionReference.child("latitude").setValue(mOtherPosition.getLatitude());
        otherPositionReference.child("longitude").setValue(mOtherPosition.getLongitude());

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
