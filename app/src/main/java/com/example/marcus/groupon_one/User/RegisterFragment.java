package com.example.marcus.groupon_one.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.groupon_one.Config.DatabaseVariable;
import com.example.marcus.groupon_one.Database.PostUrlLoader_v4;
import com.example.marcus.groupon_one.Config.StatusCode;
import com.example.marcus.groupon_one.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class RegisterFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, ResendVerifyEmailDialog.OnDialogResendVerifyEmail
{
    //loader id must be unique in activity/fragment
    private boolean loadingFirstTime = true;
    private static final int LOADER_ID = 0;
    private boolean resendVerifyEmailLoadingFirstTime = true;
    private static final int RESEND_VERIFY_EMAIL_LOADER_ID = 1;

    // UI references
    private EditText mUsernameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mSignUpFormView;

    public RegisterFragment()
    {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance()
    {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_fragment_register, container, false);


        // Set up the sign up form
        mUsernameView = (EditText) view.findViewById(R.id.username);

        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);
        //populates the mEmailView AutoCompleteTextView with emails form user profile
        //populateAutoComplete();

        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.sign_up || id == EditorInfo.IME_NULL)
                {
                    Toast.makeText(getContext(), "mPasswordView onEditorAction", Toast.LENGTH_SHORT).show();
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) view.findViewById(R.id.sign_up_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptSignUp();
            }
        });

        mSignUpFormView = view.findViewById(R.id.sign_up_form_sv);
        mProgressView = view.findViewById(R.id.sign_up_progress);

        return view;
    }

    // Attempts to sign in or register the account specified by the sign up form.
    // If there are form errors (invalid email, missing fields, etc.), the
    // errors are presented and no actual sign up attempt is made.
    private void attemptSignUp()
    {
        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the sign up attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password) || !IsValid.isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!IsValid.isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for a valid username, if the user entered one
        if(TextUtils.isEmpty(username))
        {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }
        else if(!IsValid.isUsernameValid(username))
        {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(DatabaseVariable._POST_USERNAME, username);
            params.put(DatabaseVariable._POST_EMAIL, email);
            params.put(DatabaseVariable._POST_PASSWORD, password);

            Bundle bundle = new Bundle();
            bundle.putSerializable("params", params);

            // Prepare the loader.  Either re-connect with an existing one, or start a new one.
            // fragmentNumber is the loader's unique id. Loader ids are specific to the Activity or
            // Fragment in which they reside.
            if(loadingFirstTime)
            {
                getLoaderManager().initLoader(LOADER_ID, bundle, this);
                loadingFirstTime = false;
            }
            else
            {
                getLoaderManager().restartLoader(LOADER_ID, bundle, this);
            }
        }
    }

    //Shows the progress UI and hides the sign up form
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle)
    {
        try
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            showProgress(true);

            if(i == LOADER_ID)
                return new PostUrlLoader_v4(getContext(), new URL(DatabaseVariable.REGISTRATION_REGISTER_USER_URL), (HashMap<String, String>) bundle.getSerializable("params"));
            else
                return new PostUrlLoader_v4(getContext(), new URL(DatabaseVariable.REGISTRATION_RESEND_VERIFICATION_EMAIL_URL), (HashMap<String, String>) bundle.getSerializable("params"));
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(), "MalformedURLException Caught", Toast.LENGTH_SHORT).show();

            //stop showing the progress
            showProgress(false);

            return null;
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data)
    {
        //stop showing the progress
        showProgress(false);

        try
        {
            JSONObject jo = new JSONObject(data);
            boolean bool = jo.getBoolean(DatabaseVariable.ERROR);

            if(loader.getId() == LOADER_ID)
            {
                //if no error
                if(!bool)
                {
                    Toast.makeText(getActivity(), jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                else
                {
                    int status_code = jo.getInt(StatusCode.STATUS_CODE);

                    //if email is registered in the database but is not activated, then show dialog whether to send verification email again
                    if(status_code == StatusCode.USER_EMAIL_ALREADY_EXISTS_AND_NOT_ACTIVATED) {
                        mEmailView.setError(getString(R.string.error_email_registered_and_not_activated));
                        mEmailView.requestFocus();

                        ResendVerifyEmailDialog resendVerifyEmailDialog = new ResendVerifyEmailDialog(getContext(), this);
                        resendVerifyEmailDialog.show();
                    }
                    else if(status_code == StatusCode.USER_EMAIL_ALREADY_EXISTS_AND_ACTIVATED) {
                        mEmailView.setError(getString(R.string.error_email_registered_and_activated));
                        mEmailView.requestFocus();
                    }
                    else if(status_code == StatusCode.USER_USERNAME_ALREADY_EXISTS) {
                        mUsernameView.setError(getString(R.string.error_username_already_taken));
                        mUsernameView.requestFocus();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if (loader.getId() == RESEND_VERIFY_EMAIL_LOADER_ID)
            {
                //if no error
                if(!bool)
                {
                    Toast.makeText(getActivity(), jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(getActivity(), jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader)
    {

    }

    @Override
    public void resendVerificationEmail()
    {
        HashMap<String, String> params = new HashMap<>();
        params.put(DatabaseVariable._POST_EMAIL, mEmailView.getText().toString());

        Bundle bundle = new Bundle();
        bundle.putSerializable("params", params);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        // fragmentNumber is the loader's unique id. Loader ids are specific to the Activity or
        // Fragment in which they reside.
        if(resendVerifyEmailLoadingFirstTime)
        {
            getLoaderManager().initLoader(RESEND_VERIFY_EMAIL_LOADER_ID, bundle, this);
            resendVerifyEmailLoadingFirstTime = false;
        }
        else
        {
            getLoaderManager().restartLoader(RESEND_VERIFY_EMAIL_LOADER_ID, bundle, this);
        }
    }
}
