package com.example.xyzreader.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;
import android.widget.ImageView;

import com.example.xyzreader.data.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the data and operations directly used by the views, activities and fragments.
 */

public class ArticleViewModel {
    private static final ArticleViewModel theModel = new ArticleViewModel();
    private int position;
    ArrayList<Item> items;
    private MutableLiveData<Integer> positionLiveData;
    private MutableLiveData<Boolean> isListLiveData;
    private MutableLiveData<List<Item>> itemsLiveData;

    private ArticleViewModel() {
        items = null;
        position = -1;
        positionLiveData = new MutableLiveData<>();
        positionLiveData.setValue(-1);
        isListLiveData = new MutableLiveData<>();
        isListLiveData.setValue(true);
        itemsLiveData = new MutableLiveData<>();
    }

    public static ArticleViewModel getViewModel() {
        return theModel;
    }

    public LiveData<List<Item>> getItemsLiveData() {
        return itemsLiveData;
    }

    public void setAllItemsCursor(Cursor cursor) {
        int oldPosition = getPosition();
        // copy data from cursor into items
        if (cursor == null) {
            items = null;
        }
        else if (!cursor.isClosed()) {
            items = new ArrayList<>();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                items.add(new Item(cursor));
            }
            cursor.close();
        }
        itemsLiveData.setValue(items);
        int newPosition = getPosition();
        if (oldPosition != newPosition) {
            positionLiveData.setValue(newPosition);
        }
        if (isList() != isListLiveData.getValue()) {
            isListLiveData.setValue(isList());
        }
    }

    public boolean isItemSelected() {
        return items != null
                && position >= 0
                && position < items.size();
    }

    public boolean isList() {
        return !isItemSelected();
    }

    public Item getItem() {
        if (!isItemSelected()) {
            return null;
        }
        return items.get(position);
    }

    public int getPosition() {
        return isItemSelected() ? position : -1;
    }

    public LiveData<Integer> getPositionLiveData() {
        return positionLiveData;
    }

    public LiveData<Boolean> getIsListLiveData() {
        return isListLiveData;
    }

    public void setPosition(int position) {
        this.position = position;
        positionLiveData.setValue(getPosition());
        if (isList() != isListLiveData.getValue()) {
            isListLiveData.setValue(isList());
        }
    }
}
