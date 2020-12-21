package com.wordpress.herovickers.omup.data;


public class Contact {

    private String mContactName;
//Todo replace String with appropriate data type
    private String mContactNumber;

//    private int mImageResourceID = NO_IMAGE_PROVIDED;

//    private static final int NO_IMAGE_PROVIDED = -1;
//Todo replace String with appropriate data type
    public Contact(String contactName, String contactNumber) {
        mContactName = contactName;
        mContactNumber = contactNumber;
    }

//    public Contact(String defaultTranslation, String miwokTranslation, int imageResourceID, int audioResourceID) {
//        mDefaultTranslation = defaultTranslation;
//        mMiwokTranslation = miwokTranslation;
//        mImageResourceID = imageResourceID;
//    }

    public String getmContactName() {
        return mContactName;
    }
    //Todo replace String with appropriate data type
    public String getContactNumber() {
        return mContactNumber;
    }

//    public int getImageResourceID() {
//        return mImageResourceID;
//    }

//    public boolean hasImage() {
//        return mImageResourceID != NO_IMAGE_PROVIDED;
//    }
}
