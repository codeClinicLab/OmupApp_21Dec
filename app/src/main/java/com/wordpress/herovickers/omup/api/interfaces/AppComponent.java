package com.wordpress.herovickers.omup.api.interfaces;

import com.wordpress.herovickers.omup.api.ApplicationModule;
import com.wordpress.herovickers.omup.api.HttpModule;
import com.wordpress.herovickers.omup.destinations.UserProfileActivity;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {HttpModule.class, ApplicationModule.class})
public interface AppComponent {
    void inject(UserProfileActivity mainActivity);
/*void inject(MainActivity mainActivity);
  void inject(SignInScreen signInScreen);
  void inject(SignUpScreen signUpScreen);
  void inject(AstrologersLstForChat astrologersLstForChat);
  void inject(AstrologyProfile astrologyProfile);
  void inject(AstrologersLstForCall astrologersLstForCall);
  void inject(ChatCallHistory chatCallHistory);
  void inject(OrderDetailFrag orderDetailFrag);
  void inject(WalletTransactions walletTransactions);
  void inject(RechargePaymentFrag rechargePaymentFrag);
  void inject(ProfileFrag profileFrag);
  void inject(P2PChatDetailJava p2PChatDetailJava);
*/


}
