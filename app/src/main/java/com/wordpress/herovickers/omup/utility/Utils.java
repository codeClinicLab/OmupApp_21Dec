package com.wordpress.herovickers.omup.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.amulyakhare.textdrawable.TextDrawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class Utils {
    public static final int CAMERA = 54;
    public static final int GALLERY = 24;
    private static final String IMAGE_DIRECTORY = "/OmupApp/profileImage";
    public static void displayContactImage(ImageView contactImage, char charAt, int size) {
        if (!Character.isLetter(charAt)){
            charAt = 'O';
        }
        int[] colors = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN,
                Color.CYAN, Color.GRAY, Color.BLACK, Color.MAGENTA,Color.LTGRAY };
        int color = colors[(new Random().nextInt(size))%colors.length];
        TextDrawable drawable = TextDrawable.builder().buildRound(String.valueOf(charAt), color);
        contactImage.setImageDrawable(drawable);
    }

    public static void showPictureDialog(final Activity context){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary(context);
                                break;
                            case 1:
                                takePhotoFromCamera(context);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private static void choosePhotoFromGallary(Activity context) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        context.startActivityForResult(galleryIntent, GALLERY);
    }

    private static void takePhotoFromCamera(Activity context) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        context.startActivityForResult(intent, CAMERA);
    }

    //The method takes an image bitmap and the context as argument
    //This method returns the path to which the image is saved if saved successfully
    //TODO This method should be in the background
    public static String saveImage(Bitmap myBitmap, Context context) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        //Compress the image
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        //Creates the directory to save the image
        File profilePictureDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        // have the object build the directory structure, if needed.
        if (!profilePictureDirectory.exists()) {
            profilePictureDirectory.mkdirs();
        }

        try {
            File f = new File(profilePictureDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    //TODO put this method in the background
    public static Bitmap generateThumnail(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);



        return ThumbnailUtils.extractThumbnail(bitmap,
                100, 100);

    }
}
