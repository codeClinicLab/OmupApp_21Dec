package com.wordpress.herovickers.omup.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wordpress.herovickers.omup.Repository.FirestoreRepository;
import com.wordpress.herovickers.omup.models.RecentCall;
import com.wordpress.herovickers.omup.models.User;
import com.wordpress.herovickers.omup.models.WalletTransaction;
import com.wordpress.herovickers.omup.utility.PasswordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class FirestoreViewModel extends ViewModel{
    private String TAG = "FIRESTORE_VIEWMODEL";
    private FirestoreRepository firestoreRepository = new FirestoreRepository();
    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<User> userOnceData = new MutableLiveData<>();
    private MutableLiveData<List<RecentCall>> listMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<RecentCall>> listMutableLiveDataSP = new MutableLiveData<>();
    private MutableLiveData<List<WalletTransaction>> walletTransLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> saveDataStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateProfileStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> walletLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateWalletLiveData = new MutableLiveData<>();
    private MutableLiveData<String> pConfirmation = new MutableLiveData<>();

    public LiveData<User> getUserData(){
        firestoreRepository.getUserData().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    userMutableLiveData.setValue(null);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    userMutableLiveData.setValue(user);
                } else {
                    Log.e(TAG, "Current data: null");
                    userMutableLiveData.setValue(null);
                }
            }
        });
        return userMutableLiveData;
    }
    public LiveData<User> getUserDataOnce(){
        firestoreRepository.getUserDataOnce().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null){
                        userOnceData.setValue(snapshot.toObject(User.class));
                    }else {
                        userOnceData.setValue(null);
                    }

                }else {
                    userOnceData.setValue(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userOnceData.setValue(null);
            }
        });
        return userOnceData;
    }
    public LiveData<List<RecentCall>> getRecentCalls(){
        firestoreRepository.getRecentCalls().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    listMutableLiveData.setValue(null);
                    return;
                }

                List<RecentCall> recentCallList = new ArrayList<>();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size()>0){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        RecentCall recentCall = doc.toObject(RecentCall.class);
                        recentCallList.add(recentCall);
                    }
                    listMutableLiveData.setValue(recentCallList);
                }else {
                    listMutableLiveData.setValue(null);
                }

            }
        });
        return listMutableLiveData;
    }
    public LiveData<List<RecentCall>> getRecentCallsSP(final String combinedId){
        firestoreRepository.getRecentCallsSP(combinedId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    listMutableLiveDataSP.setValue(null);
                    Log.e("ddddd", "nData is empty" + e.getMessage());
                    return;
                }

                List<RecentCall> recentCallList = new ArrayList<>();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size()>0){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        RecentCall recentCall = doc.toObject(RecentCall.class);
                        recentCallList.add(recentCall);
                        Log.e("ddddd", "nData is "+doc.getData());
                    }
                    listMutableLiveDataSP.setValue(recentCallList);
                }else {
                    listMutableLiveDataSP.setValue(null);
                    Log.e("ddddd", "nData is empty "+combinedId );
                }

            }
        });
        return listMutableLiveDataSP;
    }
    public LiveData<List<WalletTransaction>> getRecentTransactions(){
        firestoreRepository.getWalletTransactions().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    walletTransLiveData.setValue(null);
                    return;
                }

                List<WalletTransaction> walletTransactions = new ArrayList<>();
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getData();
                        walletTransactions.add(doc.toObject(WalletTransaction.class));
                    }
                    walletTransLiveData.setValue(walletTransactions);
                }else{
                    walletTransLiveData.setValue(null);
                }

            }
        });
        return walletTransLiveData;
    }
    public LiveData<Boolean> saveUserData(User user){
        firestoreRepository.saveUserData(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                saveDataStatus.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                saveDataStatus.setValue(false);
            }
        });
        return saveDataStatus;
    }
    public void saveRecentCallInformation(RecentCall recentCall){
        firestoreRepository.saveRecentCallInformation(recentCall);
    }
    public void saveRecentCallInfoSP(RecentCall recentCall, String combinedId){
        firestoreRepository.saveRecentCallInfoSP(recentCall, combinedId);
    }
    public LiveData<Boolean> saveWalletTransction(WalletTransaction transaction){
        firestoreRepository.saveWalletTransaction(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        walletLiveData.setValue(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                walletLiveData.setValue(false);
            }
        });
        return walletLiveData;
    }
    public LiveData<Boolean> updateUserProfile(Map<String, Object> user){
        firestoreRepository.updateUserProfile(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateProfileStatus.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateProfileStatus.setValue(false);
            }
        });
        return updateProfileStatus;
    }
    public LiveData<Boolean> updateWalletBalance(Double value){
        firestoreRepository.updateWalletBalance(value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateWalletLiveData.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateWalletLiveData.setValue(false);
            }
        });
        return updateWalletLiveData;
    }
    public LiveData<String> verifyPassword(final String password){
        firestoreRepository.getUserDataOnce().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null){
                    User user = snapshot.toObject(User.class);
                    String securedPassword = user.getPassword().get("password");
                    String salt = user.getPassword().get("salt");
                    boolean bool = PasswordUtils.verifyUserPassword(password, securedPassword, salt);
                    if (bool){
                        pConfirmation.setValue("match");
                    }else{
                        pConfirmation.setValue("no match");
                    }
                }else {
                    pConfirmation.setValue(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pConfirmation.setValue(null);
            }
        });
        return pConfirmation;
    }
}
