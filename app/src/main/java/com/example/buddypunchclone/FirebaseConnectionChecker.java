//package com.example.buddypunchclone;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//
//public class FirebaseConnectionChecker {
//
//    private static final String TEST_KEY = "testConnection";
//    private DatabaseReference databaseReference;
//
//    public FirebaseConnectionChecker() {
//        // Get a reference to the Firebase Realtime Database
//        databaseReference = FirebaseDatabase.getInstance().getReference(TEST_KEY);
//    }
//
//    public void checkConnection(FirebaseConnectionListener listener) {
//        // Write a test value to the database
//        databaseReference.setValue("Connection Successful")
//                .addOnSuccessListener(aVoid -> {
//                    // If writing is successful, read the value back
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            String value = dataSnapshot.getValue(String.class);
//                            if ("Connection Successful".equals(value)) {
//                                listener.onConnectionSuccess();
//                            } else {
//                                listener.onConnectionFailed("Unexpected value: " + value);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            listener.onConnectionFailed(databaseError.getMessage());
//                        }
//                    });
//                })
//                .addOnFailureListener(e -> listener.onConnectionFailed(e.getMessage()));
//    }
//
//    public interface FirebaseConnectionListener {
//        void onConnectionSuccess();
//        void onConnectionFailed(String error);
//    }
//}
