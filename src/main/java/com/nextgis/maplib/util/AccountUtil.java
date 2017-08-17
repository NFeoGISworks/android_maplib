/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android.
 * Author:   Dmitry Baryshnikov (aka Bishop), bishop.dev@gmail.com
 * Author:   NikitaFeodonit, nfeodonit@yandex.com
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * *****************************************************************************
 * Copyright (c) 2012-2015. NextGIS, info@nextgis.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.maplib.util;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncInfo;
import android.os.Build;
import android.util.Base64;

import com.nextgis.maplib.api.IGISApplication;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class AccountUtil {
    public static boolean verifySignature(String data, String signature) {
        try {
            // add public key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzbmnrTLjTLxqCnIqXgIJ\n" +
                    "jebXVOn4oV++8z5VsBkQwK+svDkGK/UcJ4YjXUuPqyiZwauHGy1wizGCgVIRcPNM\n" +
                    "I0n9W6797NMFaC1G6Rp04ISv7DAu0GIZ75uDxE/HHDAH48V4PqQeXMp01Uf4ttti\n" +
                    "XfErPKGio7+SL3GloEqtqGbGDj6Yx4DQwWyIi6VvmMsbXKmdMm4ErczWFDFHIxpV\n" +
                    "ln/VfX43r/YOFxqt26M7eTpaBIvAU6/yWkIsvidMNL/FekQVTiRCl/exPgioDGrf\n" +
                    "06z5a0sd3NDbS++GMCJstcKxkzk5KLQljAJ85Jciiuy2vv14WU621ves8S9cMISO\n" + "HwIDAQAB";
            byte[] keyBytes = Base64.decode(key.getBytes("UTF-8"), Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            PublicKey publicKey = keyFactory.generatePublic(spec);

            // verify signature
            Signature signCheck = Signature.getInstance("SHA256withRSA");
            signCheck.initVerify(publicKey);
            signCheck.update(data.getBytes("UTF-8"));
            byte[] sigBytes = Base64.decode(signature, Base64.DEFAULT);
            return signCheck.verify(sigBytes);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSyncActive(Account account, String authority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return isSyncActiveHoneycomb(account, authority);
        } else {
            SyncInfo currentSync = ContentResolver.getCurrentSync();
            return currentSync != null && currentSync.account.equals(account) && currentSync.authority.equals(authority);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isSyncActiveHoneycomb(Account account, String authority) {
        for (SyncInfo syncInfo : ContentResolver.getCurrentSyncs()) {
            if (syncInfo.account.equals(account) && syncInfo.authority.equals(authority)) {
                return true;
            }
        }
        return false;
    }

    public static AccountData getAccountData(Context context, String accountName) throws IllegalStateException {
        IGISApplication app = (IGISApplication) context.getApplicationContext();
        Account account = app.getAccount(accountName);

        if (null == account) {
            throw new IllegalStateException("Account is null");
        }

        AccountData accountData = new AccountData();

        accountData.url = app.getAccountUrl(account);
        accountData.login = app.getAccountLogin(account);
        accountData.password = app.getAccountPassword(account);

        return accountData;
    }

    public static class AccountData {
        public String url;
        public String login;
        public String password;
    }
}
