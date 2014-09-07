package com.york.cs.swe.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * Authentication Service 
 * @author sebas
 *
 */
public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        AuthenticatorApp authenticator = new AuthenticatorApp(this);
        return authenticator.getIBinder();
    }
}
