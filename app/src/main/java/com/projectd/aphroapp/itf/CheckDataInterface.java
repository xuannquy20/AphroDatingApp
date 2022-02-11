package com.projectd.aphroapp.itf;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface CheckDataInterface {
        public void onStart();
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
}
