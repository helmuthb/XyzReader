package com.example.xyzreader.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;

import com.example.xyzreader.remote.RemoteEndpointUtil;
import com.example.xyzreader.util.EventBus;
import com.example.xyzreader.util.RefreshStatus;
import com.squareup.otto.Produce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public UpdaterService() {
        super(TAG);
        EventBus.register(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            EventBus.post(RefreshStatus.STATE_ERROR);
            return;
        }

        EventBus.post(RefreshStatus.STATE_LOADING);

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildDirUri();

        // No deletion of all objects. This is causing strange behavior.
        // cpo.add(ContentProviderOperation.newDelete(dirUri).build());
        // instead mark for deletion
        cpo.add(ContentProviderOperation.newUpdate(dirUri)
                .withValue(ItemsContract.ItemsColumns.DELETED, 1)
                .build());

        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray();
            if (array == null) {
                EventBus.post(RefreshStatus.STATE_ERROR);
                throw new JSONException("Invalid parsed item array" );
            }

            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
                values.put(ItemsContract.Items.SERVER_ID, object.getString("id" ));
                values.put(ItemsContract.Items.AUTHOR, object.getString("author" ));
                values.put(ItemsContract.Items.TITLE, object.getString("title" ));
                values.put(ItemsContract.Items.BODY, object.getString("body" ));
                values.put(ItemsContract.Items.THUMB_URL, object.getString("thumb" ));
                values.put(ItemsContract.Items.PHOTO_URL, object.getString("photo" ));
                values.put(ItemsContract.Items.ASPECT_RATIO, object.getString("aspect_ratio" ));
                values.put(ItemsContract.Items.PUBLISHED_DATE, object.getString("published_date"));
                values.put(ItemsContract.Items.DELETED, 0);
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }

            // now delete all items which were not updated
            cpo.add(ContentProviderOperation.newDelete(dirUri)
                    .withSelection(ItemsContract.ItemsColumns.DELETED + "=?",
                            new String[] { "1" })
                    .build());
            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            EventBus.post(RefreshStatus.STATE_ERROR);
            Log.e(TAG, "Error updating content.", e);
        }

        EventBus.post(RefreshStatus.STATE_FINISHED);
    }
}
