package com.example.xyzreader.data;

import android.database.Cursor;

import com.example.xyzreader.util.Util;

import java.util.Date;

/**
 * Model class - representing an item in the XyzReader
 */

public class Item {
    public final long id;
    public final String title;
    public final String author;
    public final String body;
    public final String thumbnailUrl;
    public final String photoUrl;
    public final double aspectRatio;
    public final Date publishDate;

    public Item(Cursor cursor) {
        id = cursor.getLong(ArticleLoader.Query._ID);
        title = cursor.getString(ArticleLoader.Query.TITLE);
        author = cursor.getString(ArticleLoader.Query.AUTHOR);
        body = cursor.getString(ArticleLoader.Query.BODY);
        thumbnailUrl = cursor.getString(ArticleLoader.Query.THUMB_URL);
        photoUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
        aspectRatio = cursor.getDouble(ArticleLoader.Query.ASPECT_RATIO);
        String dateString = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
        publishDate = Util.parseDateString(dateString);
    }
}
