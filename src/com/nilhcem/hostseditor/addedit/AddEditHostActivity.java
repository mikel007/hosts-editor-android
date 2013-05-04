package com.nilhcem.hostseditor.addedit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.nilhcem.hostseditor.R;
import com.nilhcem.hostseditor.bus.event.CreatedHostEvent;
import com.nilhcem.hostseditor.core.BaseActivity;
import com.nilhcem.hostseditor.list.ListHostsActivity;
import com.nilhcem.hostseditor.model.Host;
import com.squareup.otto.Subscribe;

public class AddEditHostActivity extends BaseActivity {
	public static final String EXTRA_HOST_ORIGINAL = "hostOriginal"; // useful in Edit mode, this is the original Host entry before being edited.
	public static final String EXTRA_HOST_MODIFIED = "hostModified"; // the new Host entry, after having been edited.

	private AddEditHostFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Host hostToEdit = getHostFromIntent();
		initActionBar(hostToEdit);

		// Add fragment
		FragmentManager fragmentMngr = getSupportFragmentManager();
		mFragment = (AddEditHostFragment) fragmentMngr.findFragmentByTag(AddEditHostFragment.TAG);
		if (mFragment == null) {
			mFragment = new AddEditHostFragment();
			if (hostToEdit != null) {
				mFragment.setHostToEdit(hostToEdit);
			}
			FragmentTransaction ft = fragmentMngr.beginTransaction();
			ft.add(android.R.id.content, mFragment, AddEditHostFragment.TAG);
			ft.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, ListHostsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Subscribe
	public void onHostCreatedFromFragment(CreatedHostEvent event) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(EXTRA_HOST_ORIGINAL, event.getOriginal());
		returnIntent.putExtra(EXTRA_HOST_MODIFIED, event.getHost());
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	private Host getHostFromIntent() {
		Host host = null;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			host = bundle.getParcelable(EXTRA_HOST_ORIGINAL);
		}
		return host;
	}

	private void initActionBar(Host hostToEdit) {
		int titleRes;
		if (hostToEdit == null) {
			titleRes = R.string.add_host_title;
		} else {
			titleRes = R.string.edit_host_title;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(titleRes);
	}
}
