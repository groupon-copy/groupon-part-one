package com.example.marcus.groupon_one.Administrator;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marcus.groupon_one.Config.AdminDatabaseVariable;
import com.example.marcus.groupon_one.Config.DatabaseVariable;
import com.example.marcus.groupon_one.Database.PostUrlLoader;
import com.example.marcus.groupon_one.One.MainFeedActivity;
import com.example.marcus.groupon_one.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class DealCreateActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>
{
    //must be unique in activity/fragment
    private static final int LOADER_ID = 0;
    private boolean isLoadFirstTime = true;

    private EditText vendor_id;
    private EditText bold_text;
    private EditText fine_print_text;
    private EditText highlight_text;
    private EditText original_price;
    private EditText current_price;
    private EditText num_bought;
    private EditText num_thumbs_up;
    private EditText num_thumbs_down;
    private CheckBox is_limited_time_offer;
    private CheckBox is_limited_availability;
    private EditText valid_from;
    private EditText valid_until;
    private EditText img_rel_url;

    private EditText tag_list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_activity_deal_create);

        vendor_id = (EditText)findViewById(R.id.dcreate_vid);
        bold_text = (EditText)findViewById(R.id.dcreate_bold_text);
        fine_print_text = (EditText)findViewById(R.id.dcreate_fine_print_text);
        highlight_text = (EditText)findViewById(R.id.dcreate_highlightText);
        original_price = (EditText)findViewById(R.id.dcreate_originalPrice);
        current_price = (EditText)findViewById(R.id.dcreate_currentPrice);
        num_bought = (EditText)findViewById(R.id.dcreate_numBought);
        num_thumbs_up = (EditText)findViewById(R.id.dcreate_numThumbsUp);
        num_thumbs_down = (EditText)findViewById(R.id.dcreate_numThumbsDown);
        valid_from = (EditText)findViewById(R.id.dcreate_validFrom);
        valid_until = (EditText)findViewById(R.id.dcreate_validUntil);
        is_limited_availability = (CheckBox)findViewById(R.id.dcreate_isLimitedAvailabilityCheckBox);
        is_limited_time_offer = (CheckBox)findViewById(R.id.dcreate_isLimitedTimeOfferCheckBox);
        img_rel_url = (EditText)findViewById((R.id.dcreate_img_ref));

        tag_list = (EditText)findViewById(R.id.dcreate_tagList);

        /*
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            vid = extras.getString("MSG");
            vendor_id.setText(vid);
        }
        */
    }

    boolean functional_check()
    {
        return true;
        //return (!vendor_id.getText().toString().equals(""))&&(!bold_text.getText().toString().equals(""));
    }
    public void createDealAndTags(View v)
    {
        if (functional_check())
        {
            HashMap<String, String> params = new HashMap<>();

            params.put(DatabaseVariable.DEAL_VENDOR_ID, vendor_id.getText().toString());
            params.put(DatabaseVariable.DEAL_BOLD_TEXT, bold_text.getText().toString());
            params.put(DatabaseVariable.DEAL_FINE_PRINT_TEXT, fine_print_text.getText().toString());
            params.put(DatabaseVariable.DEAL_HIGHLIGHT_TEXT, highlight_text.getText().toString());
            params.put(DatabaseVariable.DEAL_VALID_FROM, valid_from.getText().toString());
            params.put(DatabaseVariable.DEAL_VALID_UNTIL, valid_until.getText().toString());
            params.put(DatabaseVariable.DEAL_ORIGINAL_PRICE, original_price.getText().toString());
            params.put(DatabaseVariable.DEAL_CURRENT_PRICE, current_price.getText().toString());
            params.put(DatabaseVariable.DEAL_NUM_BOUGHT, num_bought.getText().toString());
            params.put(DatabaseVariable.DEAL_NUM_THUMBS_UP, num_thumbs_up.getText().toString());
            params.put(DatabaseVariable.DEAL_NUM_THUMBS_DOWN, num_thumbs_down.getText().toString());
            params.put(DatabaseVariable.DEAL_IMG_REL_URL, img_rel_url.getText().toString());

            int i;
            if(is_limited_time_offer.isChecked()) i = 1; else i = 0;
            params.put("is_limited_time_offer", Integer.toString(i));
            if(is_limited_availability.isChecked()) i = 1; else i = 0;
            params.put("is_limited_availability", Integer.toString(i));

            params.put(AdminDatabaseVariable._POST_TAG_LIST, tag_list.getText().toString());

            Bundle bundle = new Bundle();
            bundle.putSerializable("params", params);
            //bundle.putString("loaderType", "createDeal");
            // Prepare the loader.  Either re-connect with an existing one, or start a new one.
            // fragmentNumber is the loader's unique id. Loader ids are specific to the Activity or
            // Fragment in which they reside.
            if(isLoadFirstTime) {
                isLoadFirstTime = false;
                getLoaderManager().initLoader(LOADER_ID, bundle, this);
            } else {
                getLoaderManager().restartLoader(LOADER_ID, bundle, this);
            }
        }
        else
        {
            Toast.makeText(this, "Fill in minimum values", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args)
    {
        try
        {
            return new PostUrlLoader(this, new URL(AdminDatabaseVariable.INSERT_DEAL_AND_TAGS_URL), (HashMap<String, String>) args.getSerializable("params"));
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "MalformedURLException", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onLoadFinished(android.content.Loader<String> loader, String data)
    {
        try
        {
            JSONObject jo = new JSONObject(data);

            //if no error
            if(!jo.getBoolean(DatabaseVariable.ERROR)) {
                Toast.makeText(DealCreateActivity.this, "success", Toast.LENGTH_SHORT).show();

                //go back to main feed activity
                Intent intent = new Intent(this, MainFeedActivity.class);
                startActivity(intent);
            } else {
                //print the error message
                Toast.makeText(this, jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<String> loader)
    {
        Toast.makeText(this, "onLoaderReset has been called", Toast.LENGTH_SHORT).show();
    }
}
