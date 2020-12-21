package com.wordpress.herovickers.omup.Repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.models.WalletTransaction;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FirestoreRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    //Add user information to database after successful signup
    public Task<Void> saveUserData(User user){
        DocumentReference reference = db.collection("Users").document(currentUser.getUid());
        return reference.set(user);
    }
    //Add RecentCall Information to Database
    public void saveRecentCallInformation(RecentCall recentCall){
        db.collection("Users/" + currentUser.getUid() + "/Conversations").add(recentCall);
    }
    //Add RecentCall information between users to the Conversation documents
    public void saveRecentCallInfoSP(RecentCall recentCall, String combinedId){
        db.collection("AllConversations/" + combinedId + "/PhoneCalls/").add(recentCall);
    }
    //Add Wallet Transaction Details to Database
    public Task<DocumentReference> saveWalletTransaction(WalletTransaction transaction){
        return db.collection("Users/"+currentUser.getUid()+"/WalletTransactions").add(transaction);
    }
    //Update User Profile Information
    public Task<Void> updateUserProfile(Map<String, Object> user){
        DocumentReference reference = db.collection("Users").document(currentUser.getUid());
        return reference.update(user);
    }
    //Update Wallet Balance
    public Task<Void> updateWalletBalance(Double value){
        DocumentReference reference = db.collection("Users").document(currentUser.getUid());
        return reference.update("wallet.balance", FieldValue.increment(value));
    }
    //Query UserInfo
    public DocumentReference getUserData() {
        return db.collection("Users").document(currentUser.getUid());
    }
    //get uset data one
    public DocumentReference getUserDataOnce(){
        return db.collection("Users").document(currentUser.getUid());
    }
    //Query recent calls made by user
    public Query getRecentCalls(){
        //Query only calls less than 1month old
        Long timeLimit = System.currentTimeMillis()- TimeUnit.DAYS.toMillis(30);
        return db.collection("Users/"+currentUser.getUid()+"/Conversations")
                .whereGreaterThan("createdAt", timeLimit)
                .orderBy("createdAt", Query.Direction.DESCENDING);
    }
    //Query recent calls made between users
    public Query getRecentCallsSP(String combineId){
        Long timeLimit = System.currentTimeMillis()-TimeUnit.DAYS.toMillis(30);
        return db.collection("AllConversations/"+combineId+"/PhoneCalls")
                .whereGreaterThan("createdAt", timeLimit)
                .orderBy("createdAt", Query.Direction.DESCENDING);
    }
    //Query all wallet transaction
    public Query getWalletTransactions(){
        return db.collection("Users/"+currentUser.getUid()+"/WalletTransactions")
                .orderBy("createdAt", Query.Direction.DESCENDING);
    }
}
