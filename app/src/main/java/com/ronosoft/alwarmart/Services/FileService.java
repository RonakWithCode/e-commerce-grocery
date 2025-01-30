package com.ronosoft.alwarmart.Services;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FileService {



    public String filletExtension(String uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Get the file extension based on the Uri's MIME type
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(Uri.parse(uri)));

        if (extension == null) {
            // If the MIME type doesn't provide an extension, try to extract from the Uri's path

            if (uri != null) {
                int extensionStartIndex = uri.lastIndexOf('.');
                if (extensionStartIndex != -1) {
                    extension = uri.substring(extensionStartIndex + 1);
                }
            }
        }

        return extension;
    }
}
