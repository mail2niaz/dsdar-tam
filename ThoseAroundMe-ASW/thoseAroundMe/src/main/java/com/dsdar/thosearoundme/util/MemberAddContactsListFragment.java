/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dsdar.thosearoundme.util;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdar.thosearoundme.BuildConfig;
import com.dsdar.thosearoundme.R;
import com.dsdar.thosearoundme.TeamMemberAddActivity;
import com.dsdar.thosearoundme.others.Contact;

/**
 * This fragment displays a list of contacts stored in the Contacts Provider.
 * Each item in the list shows the contact's thumbnail photo and display name.
 * On devices with large screens, this fragment's UI appears as part of a
 * two-pane layout, along with the UI of {@link ContactDetailFragment}. On
 * smaller screens, this fragment's UI appears as a single pane.
 * 
 * This Fragment retrieves contacts based on a search string. If the user
 * doesn't enter a search string, then the list contains all the contacts in the
 * Contacts Provider. If the user enters a search string, then the list contains
 * only those contacts whose data matches the string. The Contacts Provider
 * itself controls the matching algorithm, which is a "substring" search: if the
 * search string is a substring of any of the contacts data, then there is a
 * match.
 * 
 * On newer API platforms, the search is implemented in a SearchView in the
 * ActionBar; as the user types the search string, the list automatically
 * refreshes to display results ("type to filter"). On older platforms, the user
 * must enter the full string and trigger the search. In response, the trigger
 * starts a new Activity which loads a fresh instance of this fragment. The
 * resulting UI displays the filtered list and disables the search feature to
 * prevent furthering searching.
 */
public class MemberAddContactsListFragment extends ListFragment implements
		AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	// Defines a tag for identifying log entries
	private static final String TAG = "ContactsListFragment";

	// Bundle key for saving previously selected search result item
	private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.example.android.contactslist.ui.SELECTED_ITEM";

	private ContactsAdapter mAdapter; // The main query adapter
	private ImageLoader mImageLoader; // Handles loading the contact image in a
										// background thread
	private String mSearchTerm; // Stores the current search query term

	// Contact selected listener that allows the activity holding this fragment
	// to be notified of
	// a contact being selected
	private OnContactsInteractionListener mOnContactSelectedListener;

	// Stores the previously selected search item so that on a configuration
	// change the same item
	// can be reselected again
	private int mPreviouslySelectedSearchItem = 0;

	// Whether or not the search query has changed since the last time the
	// loader was refreshed
	private boolean mSearchQueryChanged;

	// Whether or not this fragment is showing in a two-pane layout
	private boolean mIsTwoPaneLayout = false;

	// Whether or not this is a search result view of this fragment, only used
	// on pre-honeycomb
	// OS versions as search results are shown in-line via Action Bar search
	// from honeycomb onward
	private boolean mIsSearchResultView = false;

	private TeamMemberAddActivity itsContactListActivity;

	private Context itsContext;

	SharedPreferences itsSharedPreference;

	ArrayList<Contact> itsContactsList;

	private JSONArray aMembersArray;

	private ArrayList<HashMap<Integer, Boolean>> checkedTvStates = new ArrayList<HashMap<Integer, Boolean>>();
	private HashMap<Integer, String> hmapRadio = new HashMap<Integer, String>();

	/**
	 * Fragments require an empty constructor.
	 */
	public MemberAddContactsListFragment() {
	}

	/**
	 * In platform versions prior to Android 3.0, the ActionBar and SearchView
	 * are not supported, and the UI gets the search string from an EditText.
	 * However, the fragment doesn't allow another search when search results
	 * are already showing. This would confuse the user, because the resulting
	 * search would re-query the Contacts Provider instead of searching the
	 * listed results. This method sets the search query and also a boolean that
	 * tracks if this Fragment should be displayed as a search result view or
	 * not.
	 * 
	 * @param query
	 *            The contacts search query.
	 */
	public void setSearchQuery(String query) {
		if (TextUtils.isEmpty(query)) {
			mIsSearchResultView = false;
		} else {
			mSearchTerm = query;
			mIsSearchResultView = true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if this fragment is part of a two-pane set up or a single pane
		// by reading a
		// boolean from the application resource directories. This lets allows
		// us to easily specify
		// which screen sizes should use a two-pane layout by setting this
		// boolean in the
		// corresponding resource size-qualified directory.
		// mIsTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

		// Let this fragment contribute menu items
		setHasOptionsMenu(true);

		// Create the main contacts adapter
		mAdapter = new ContactsAdapter(getActivity());
		itsContext = this.getActivity();
		itsContactListActivity = new TeamMemberAddActivity();
		itsSharedPreference = itsContext.getApplicationContext()
				.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
						itsContactListActivity.MODE_PRIVATE);
		itsContactsList = new ArrayList<Contact>();
		if (savedInstanceState != null) {
			// If we're restoring state after this fragment was recreated then
			// retrieve previous search term and previously selected search
			// result.
			mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
			mPreviouslySelectedSearchItem = savedInstanceState.getInt(
					STATE_PREVIOUSLY_SELECTED_KEY, 0);
		}

		/*
		 * An ImageLoader object loads and resizes an image in the background
		 * and binds it to the QuickContactBadge in each item layout of the
		 * ListView. ImageLoader implements memory caching for each image, which
		 * substantially improves refreshes of the ListView as the user scrolls
		 * through it.
		 * 
		 * To learn more about downloading images asynchronously and caching the
		 * results, read the Android training class Displaying Bitmaps
		 * Efficiently.
		 * 
		 * http://developer.android.com/training/displaying-bitmaps/
		 */
		mImageLoader = new ImageLoader(getActivity(),
				getListPreferredItemHeight()) {
			@Override
			protected Bitmap processBitmap(Object data) {
				// This gets called in a background thread and passed the data
				// from
				// ImageLoader.loadImage().
				return loadContactPhotoThumbnail((String) data, getImageSize());
			}
		};

		// Set a placeholder loading image for the image loader
		mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

		// Add a cache to the image loader
		mImageLoader.addImageCache(getActivity().getSupportFragmentManager(),
				0.1f);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the list fragment layout
		return inflater.inflate(R.layout.member_add_contact_list_fragment,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up ListView, assign adapter and set some listeners. The adapter
		// was previously
		// created in onCreate().

		setListAdapter(mAdapter);
		int ht = getHeight(itsContext);
		Log.d("niaz..", "niaz..==" + ht);
		// getListView().setLayoutParams(
		// new LayoutParams(LayoutParams.WRAP_CONTENT, 105));
		getListView()
				.setLayoutParams(
						new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
								ht - 400));
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setOnItemClickListener(this);
		getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView,
					int scrollState) {
				// Pause image loader to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageLoader.setPauseWork(true);
				} else {
					mImageLoader.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {
			}
		});

		if (mIsTwoPaneLayout) {
			// In a two-pane layout, set choice mode to single as there will be
			// two panes
			// when an item in the ListView is selected it should remain
			// highlighted while
			// the content shows in the second pane.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}

		// If there's a previously selected search item from a saved state then
		// don't bother
		// initializing the loader as it will be restarted later when the query
		// is populated into
		// the action bar search view (see onQueryTextChange() in
		// onCreateOptionsMenu()).
		if (mPreviouslySelectedSearchItem == 0) {
			// Initialize the loader, and create a loader identified by
			// ContactsQuery.QUERY_ID
			getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
		}

	}

	@SuppressLint("NewApi")
	public static int getWidth(Context mContext) {
		int width = 0;
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT > 12) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth(); // Deprecated
		}
		return width;
	}

	@SuppressLint("NewApi")
	public static int getHeight(Context mContext) {
		int height = 0;
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT > 12) {
			Point size = new Point();
			display.getSize(size);
			height = size.y;
		} else {
			height = display.getHeight(); // Deprecated
		}
		return height;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			// Assign callback listener which the holding activity must
			// implement. This is used
			// so that when a contact item is interacted with (selected by the
			// user) the holding
			// activity will be notified and can take further action such as
			// populating the contact
			// detail pane (if in multi-pane layout) or starting a new activity
			// with the contact
			// details (single pane layout).
			mOnContactSelectedListener = (OnContactsInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnContactsInteractionListener");
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		// In the case onPause() is called during a fling the image loader is
		// un-paused to let any remaining background work complete.
		mImageLoader.setPauseWork(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// Gets the Cursor object currently bound to the ListView
		final Cursor cursor = mAdapter.getCursor();
		Contact aContactToBeRemoved = null;
		String aContactCursorId;
		String aEmailId = null;
		String aPhoneNumber = null;
		boolean isExist = false;
		// Moves to the Cursor row corresponding to the ListView item that was
		// clicked
		cursor.moveToPosition(position);

		// Creates a contact lookup Uri from contact ID and lookup_key
		final Uri uri = Contacts.getLookupUri(cursor.getLong(ContactsQuery.ID),
				cursor.getString(ContactsQuery.LOOKUP_KEY));

		// Notifies the parent activity that the user selected a contact. In a
		// two-pane layout, the
		// parent activity loads a ContactDetailFragment that displays the
		// details for the selected
		// contact. In a single-pane layout, the parent activity starts a new
		// activity that
		// displays contact details in its own Fragment.
		// niaz checking
		Log.d("positionI", String.valueOf(position));
		CheckedTextView aCheckedTV = (CheckedTextView) v
				.findViewById(android.R.id.text1);
		mOnContactSelectedListener.onContactSelected(uri);
		HashMap<Integer, Boolean> aHashMap = new HashMap<Integer, Boolean>();
		aHashMap.put(position, true);
		final Uri aContactUri = Contacts.getLookupUri(
				cursor.getLong(ContactsQuery.ID),
				cursor.getString(ContactsQuery.LOOKUP_KEY));

		String aName = cursor.getString(ContactsQuery.DISPLAY_NAME);

		TextView textName = (TextView) v.findViewById(R.id.phone);

		if (textName.getText() != null) {
			Log.d("FINAL", textName.getText().toString());
			aPhoneNumber = textName.getText().toString();
			// Toast.makeText(itsContext, "Contact Addded Successfully !",
			// Toast.LENGTH_LONG).show();

			// Cursor aContactCursor = itsContext.getContentResolver().query(
			// aContactUri, null, null, null, null);
			// if (aContactCursor.moveToFirst()) {
			// int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			// aContactCursorId = cursor.getString(idx);
			// int aNameIdx = aContactCursor
			// .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			// //
			// // aEmailId = cursor.getString(ContactsQuery.LOOKUP_KEY);
			// // aPhoneNumber = cursor.getString(ContactsQuery.ID);
			// // Commented by Niaz for Multiple phone number
			// }

			JSONObject aContactJSONObject;

			// Notifies the parent activity that the user selected a contact. In
			// a two-pane layout, the
			// parent activity loads a ContactDetailFragment that displays the
			// details for the selected
			// contact. In a single-pane layout, the parent activity starts a
			// new activity that
			// displays contact details in its own Fragment.
			// aEmailId = "qwe";
			// aPhoneNumber = "1234";

			if (checkedTvStates.contains(aHashMap)) {
				checkedTvStates.remove(aHashMap);
				aContactToBeRemoved = new Contact();
				aContactToBeRemoved.setEmail(aEmailId);
				aContactToBeRemoved.setName(aName);
				aContactToBeRemoved.setPhoneNumber(aPhoneNumber);
				aCheckedTV.setChecked(false);
			} else {
				checkedTvStates.add(aHashMap);
				aCheckedTV.setChecked(true);
				Contact aContact = new Contact();
				aContact.setName(aName);
				aContact.setEmail(aEmailId);
				aContact.setPhoneNumber(aPhoneNumber);

				itsContactsList.add(aContact);
			}

			if (aContactToBeRemoved != null) {
				itsContactsList.remove(aContactToBeRemoved);
			}
			// aCheckBox.setButtonDrawable(R.drawable.checkbox_notify);

			try {
				aContactJSONObject = new JSONObject();
				aMembersArray = new JSONArray();
				for (int index = 0; index < itsContactsList.size(); index++) {
					aContactJSONObject = new JSONObject();

					aContactJSONObject.put("emailId", itsContactsList
							.get(index).getEmail());
					aContactJSONObject.put("phone", itsContactsList.get(index)
							.getPhoneNumber());
					aContactJSONObject.put("name", itsContactsList.get(index)
							.getName());
					aMembersArray.put(aContactJSONObject);

				}
			} catch (JSONException e) {

				Log.e("JsonException", "Json Exception occured ", e);
			} catch (Exception e) {

				Log.e("Exception", "Exception occured ", e);
			}

			// If two-pane layout sets the selected item to checked so it
			// remains
			// highlighted. In a
			// single-pane layout a new activity is started so this is not
			// needed.
			if (mIsTwoPaneLayout) {
				getListView().setItemChecked(position, true);
			}
		} else {
			Toast.makeText(
					itsContext,
					"Contact Not Addded! The Contact you selected does not have a Phone Number",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Called when ListView selection is cleared, for example when search mode
	 * is finished and the currently selected contact should no longer be
	 * selected.
	 */
	private void onSelectionCleared() {
		// Uses callback to notify activity this contains this fragment
		mOnContactSelectedListener.onSelectionCleared();

		// Clears currently checked item
		getListView().clearChoices();
	}

	// This method uses APIs from newer OS versions than the minimum that this
	// app supports. This
	// annotation tells Android lint that they are properly guarded so they
	// won't run on older OS
	// versions and can be ignored by lint.
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu items
		inflater.inflate(R.menu.contact_list_menu, menu);
		// Locate the search item
		MenuItem searchItem = menu.findItem(R.id.menu_search);

		// In versions prior to Android 3.0, hides the search item to prevent
		// additional
		// searches. In Android 3.0 and later, searching is done via a
		// SearchView in the ActionBar.
		// Since the search doesn't create a new Activity to do the searching,
		// the menu item
		// doesn't need to be turned off.
		if (mIsSearchResultView) {
			searchItem.setVisible(false);
		}

		// In version 3.0 and later, sets up and configures the ActionBar
		// SearchView
		if (Util.hasHoneycomb()) {

			// Retrieves the system search manager service
			final SearchManager searchManager = (SearchManager) getActivity()
					.getSystemService(Context.SEARCH_SERVICE);

			// Retrieves the SearchView from the search menu item
			final SearchView searchView = (SearchView) searchItem
					.getActionView();

			// Assign searchable info to SearchView
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getActivity().getComponentName()));

			// Set listeners for SearchView
			searchView
					.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
						@Override
						public boolean onQueryTextSubmit(String queryText) {
							// Nothing needs to happen when the user submits the
							// search string
							return true;
						}

						@Override
						public boolean onQueryTextChange(String newText) {
							// Called when the action bar search text has
							// changed. Updates
							// the search filter, and restarts the loader to do
							// a new query
							// using the new search string.
							String newFilter = !TextUtils.isEmpty(newText) ? newText
									: null;

							// Don't do anything if the filter is empty
							if (mSearchTerm == null && newFilter == null) {
								return true;
							}

							// Don't do anything if the new filter is the same
							// as the current filter
							if (mSearchTerm != null
									&& mSearchTerm.equals(newFilter)) {
								return true;
							}

							// Updates current filter to new filter
							mSearchTerm = newFilter;

							// Restarts the loader. This triggers
							// onCreateLoader(), which builds the
							// necessary content Uri from mSearchTerm.
							mSearchQueryChanged = true;
							getLoaderManager().restartLoader(
									ContactsQuery.QUERY_ID, null,
									MemberAddContactsListFragment.this);
							return true;
						}
					});

			if (Util.hasICS()) {
				// This listener added in ICS
				searchItem
						.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
							@Override
							public boolean onMenuItemActionExpand(
									MenuItem menuItem) {
								// Nothing to do when the action item is
								// expanded
								return true;
							}

							@Override
							public boolean onMenuItemActionCollapse(
									MenuItem menuItem) {
								// When the user collapses the SearchView the
								// current search string is
								// cleared and the loader restarted.
								if (!TextUtils.isEmpty(mSearchTerm)) {
									onSelectionCleared();
								}
								mSearchTerm = null;
								getLoaderManager().restartLoader(
										ContactsQuery.QUERY_ID, null,
										MemberAddContactsListFragment.this);
								return true;
							}
						});
			}

			if (mSearchTerm != null) {
				// If search term is already set here then this fragment is
				// being restored from a saved state and the search menu item
				// needs to be expanded and populated again.

				// Stores the search term (as it will be wiped out by
				// onQueryTextChange() when the menu item is expanded).
				final String savedSearchTerm = mSearchTerm;

				// Expands the search menu item
				if (Util.hasICS()) {
					searchItem.expandActionView();
				}

				// Sets the SearchView to the previous search string
				searchView.setQuery(savedSearchTerm, false);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (!TextUtils.isEmpty(mSearchTerm)) {
			// Saves the current search string
			outState.putString(SearchManager.QUERY, mSearchTerm);

			// Saves the currently selected contact
			outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, getListView()
					.getCheckedItemPosition());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Sends a request to the People app to display the create contact
		// screen
		case R.id.menu_add_contact:
			// final Intent intent = new Intent(Intent.ACTION_INSERT,
			// Contacts.CONTENT_URI);
			// startActivity(intent);
			// For platforms earlier than Android 3.0, triggers the search
			// activity
			String processName = TeamMemberAddActivity.processName;
			if (processName.equals("Add Team"))
				addTeam();
			else
				addTeamMember();

			break;

		case R.id.menu_search:
			if (!Util.hasHoneycomb()) {
				getActivity().onSearchRequested();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addTeam() {
		// niaz
		TeamMemberAddActivity aContactsListActivity = (TeamMemberAddActivity) getActivity();
		final EditText aTeamNameEt = (EditText) aContactsListActivity
				.findViewById(R.id.etTeamName);
		String aTeamNameEtVal = aTeamNameEt.getText().toString();
		if (aTeamNameEtVal == null || aTeamNameEtVal.equals("")) {
			Toast.makeText(itsContext, "Please Enter a Team Name",
					Toast.LENGTH_LONG).show();
			return;
		}
		itsContactsList = new ArrayList<Contact>();
		itsContactListActivity = new TeamMemberAddActivity();
		try {
			itsContext = this.getActivity();
			itsSharedPreference = itsContext.getApplicationContext()
					.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							Context.MODE_PRIVATE);
			String aMemberId = itsSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");

			Log.d("editText", aTeamNameEt.getText().toString());
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.NAME, aTeamNameEtVal);
			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			aRequestJson.put(MyAppConstants.MEMBERS, aMembersArray);

			AppAsyncTask aAsyncTask = new AppAsyncTask(itsContext,
					MyAppConstants.CREATE_TEAM, MyAppConstants.API_POST_TYPE,
					MyAppConstants.LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				String aResponseStatus, aResponseMsg, aUserId;

				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String theResult) {
					try {
						// MyAppConstants.IS_MAP_SHOW_ALL = true;
						// MyAppConstants.IS_FOLLOW_ME = false;
						Log.d("Response", theResult);
						JSONObject aResponseJson = new JSONObject(theResult);

						aResponseStatus = aResponseJson
								.getString(MyAppConstants.STATUS);
						aResponseMsg = aResponseJson
								.getString(MyAppConstants.MESSAGE);

						Log.d("Response", theResult);
						if (aResponseStatus
								.equals(MyAppConstants.SUCCESS_STATUS)) {
							Toast.makeText(
									itsContext,
									"Team " + aTeamNameEt.getText().toString()
											+ " Created Successfully",
									Toast.LENGTH_LONG).show();

							((Activity) itsContext).finish();

						}
					} catch (JSONException theJSONException) {
						theJSONException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from login webservice");
						if (aResponseMsg == null || aResponseMsg.equals("")) {
							Toast.makeText(itsContext,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(itsContext, aResponseMsg,
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJSONException) {
			theJSONException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while constructing request for login webservice");
			Toast.makeText(itsContext, MyAppConstants.JSON_ERROR_REQ,
					Toast.LENGTH_LONG).show();
		}

	}

	public void addTeamMember() {

		if ((aMembersArray == null) || (aMembersArray.length() == 0)) {
			Toast.makeText(itsContext, "No Members Selected",
					Toast.LENGTH_SHORT).show();
			return;
		}
		TeamMemberAddActivity aContactsListActivity = (TeamMemberAddActivity) getActivity();
		final EditText aTeamNameEt = (EditText) aContactsListActivity
				.findViewById(R.id.etTeamName);
		String aTeamNameEtVal = aTeamNameEt.getText().toString();
		if (aTeamNameEtVal == null || aTeamNameEtVal.equals("")) {
			Toast.makeText(itsContext, "Please Enter a Team Name",
					Toast.LENGTH_LONG).show();
			return;
		}
		itsContactsList = new ArrayList<Contact>();
		itsContactListActivity = new TeamMemberAddActivity();
		try {
			itsContext = this.getActivity();
			itsSharedPreference = itsContext.getApplicationContext()
					.getSharedPreferences(MyAppConstants.APP_PREFERENCE,
							Context.MODE_PRIVATE);
			String aMemberId = itsSharedPreference.getString(
					MyAppConstants.MEMBER_ID, "");

			Log.d("editText", aTeamNameEt.getText().toString());
			JSONObject aRequestJson = new JSONObject();
			aRequestJson.put(MyAppConstants.NAME, aTeamNameEtVal);
			aRequestJson.put(MyAppConstants.MEMBER_ID, aMemberId);
			aRequestJson.put(MyAppConstants.PHONE,
					itsSharedPreference.getString(MyAppConstants.PHONE, ""));
			aRequestJson.put(MyAppConstants.MEMBERS, aMembersArray);

			AppAsyncTask aAsyncTask = new AppAsyncTask(itsContext,
					MyAppConstants.ADD_TEAM_MEMBER_NEW,
					MyAppConstants.API_POST_TYPE, MyAppConstants.LOADING);
			aAsyncTask.setListener(new AppAsyncTask.AsyncTaskListener() {
				String aResponseStatus, aResponseMsg, aUserId;

				@Override
				public void onPreExecuteConcluded() {
				}

				@Override
				public void onPostExecuteConcluded(String theResult) {
					try {
						Log.d("Response", theResult);
						JSONObject aResponseJson = new JSONObject(theResult);

						aResponseStatus = aResponseJson
								.getString(MyAppConstants.STATUS);
						aResponseMsg = aResponseJson
								.getString(MyAppConstants.MESSAGE);

						Log.d("Response", theResult);
						if (aResponseStatus
								.equals(MyAppConstants.SUCCESS_STATUS)) {
							Toast.makeText(itsContext,
									"Request has been sent successfully.",
									Toast.LENGTH_LONG).show();
							// MyAppConstants.IS_MAP_SHOW_ALL = true;
							// MyAppConstants.IS_FOLLOW_ME = false;
							((Activity) itsContext).finish();

						} else if (aResponseStatus
								.equals(MyAppConstants.FAILURE_STATUS))
							Toast.makeText(itsContext, aResponseMsg,
									Toast.LENGTH_LONG).show();
					} catch (JSONException theJSONException) {
						theJSONException.printStackTrace();
						Log.e(this.getClass().getName(),
								"JSON Exception while retrieving response from login webservice");
						if (aResponseMsg == null || aResponseMsg.equals("")) {
							Toast.makeText(itsContext,
									MyAppConstants.CONNECTION_ERROR,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(itsContext, aResponseMsg,
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			aAsyncTask.execute(aRequestJson.toString());
		} catch (JSONException theJSONException) {
			theJSONException.printStackTrace();
			Log.e(this.getClass().getName(),
					"JSON Exception while constructing request for login webservice");
			Toast.makeText(itsContext, MyAppConstants.JSON_ERROR_REQ,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// If this is the loader for finding contacts in the Contacts Provider
		// (the only one supported)
		if (id == ContactsQuery.QUERY_ID) {
			Uri contentUri;

			// There are two types of searches, one which displays all contacts
			// and
			// one which filters contacts by a search query. If mSearchTerm is
			// set
			// then a search query has been entered and the latter should be
			// used.

			if (mSearchTerm == null) {
				// Since there's no search string, use the content URI that
				// searches the entire
				// Contacts table
				contentUri = ContactsQuery.CONTENT_URI;
			} else {
				// Since there's a search string, use the special content Uri
				// that searches the
				// Contacts table. The URI consists of a base Uri and the search
				// string.
				contentUri = Uri.withAppendedPath(ContactsQuery.FILTER_URI,
						Uri.encode(mSearchTerm));
			}

			// Returns a new CursorLoader for querying the Contacts table. No
			// arguments are used
			// for the selection clause. The search string is either encoded
			// onto the content URI,
			// or no contacts search string is used. The other search criteria
			// are constants. See
			// the ContactsQuery interface.
			return new CursorLoader(getActivity(), contentUri,
					ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null,
					ContactsQuery.SORT_ORDER);
		}

		Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// This swaps the new cursor into the adapter.
		if (loader.getId() == ContactsQuery.QUERY_ID) {
			mAdapter.swapCursor(data);

			// If this is a two-pane layout and there is a search query then
			// there is some additional work to do around default selected
			// search item.
			if (mIsTwoPaneLayout && !TextUtils.isEmpty(mSearchTerm)
					&& mSearchQueryChanged) {
				// Selects the first item in results, unless this fragment has
				// been restored from a saved state (like orientation change)
				// in which case it selects the previously selected search item.
				if (data != null
						&& data.moveToPosition(mPreviouslySelectedSearchItem)) {
					// Creates the content Uri for the previously selected
					// contact by appending the
					// contact's ID to the Contacts table content Uri
					final Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI,
							String.valueOf(data.getLong(ContactsQuery.ID)));
					mOnContactSelectedListener.onContactSelected(uri);
					getListView().setItemChecked(mPreviouslySelectedSearchItem,
							true);
				} else {
					// No results, clear selection.
					onSelectionCleared();
				}
				// Only restore from saved state one time. Next time fall back
				// to selecting first item. If the fragment state is saved again
				// then the currently selected item will once again be saved.
				mPreviouslySelectedSearchItem = 0;
				mSearchQueryChanged = false;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == ContactsQuery.QUERY_ID) {
			// When the loader is being reset, clear the cursor from the
			// adapter. This allows the
			// cursor resources to be freed.
			mAdapter.swapCursor(null);
		}
	}

	/**
	 * Gets the preferred height for each item in the ListView, in pixels, after
	 * accounting for screen density. ImageLoader uses this value to resize
	 * thumbnail images to match the ListView item height.
	 * 
	 * @return The preferred height in pixels, based on the current theme.
	 */
	private int getListPreferredItemHeight() {
		final TypedValue typedValue = new TypedValue();

		// Resolve list item preferred height theme attribute into typedValue
		getActivity().getTheme().resolveAttribute(
				android.R.attr.listPreferredItemHeight, typedValue, true);

		// Create a new DisplayMetrics object
		final DisplayMetrics metrics = new android.util.DisplayMetrics();

		// Populate the DisplayMetrics
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		// Return theme value based on DisplayMetrics
		return (int) typedValue.getDimension(metrics);
	}

	/**
	 * Decodes and scales a contact's image from a file pointed to by a Uri in
	 * the contact's data, and returns the result as a Bitmap. The column that
	 * contains the Uri varies according to the platform version.
	 * 
	 * @param photoData
	 *            For platforms prior to Android 3.0, provide the Contact._ID
	 *            column value. For Android 3.0 and later, provide the
	 *            Contact.PHOTO_THUMBNAIL_URI value.
	 * @param imageSize
	 *            The desired target width and height of the output image in
	 *            pixels.
	 * @return A Bitmap containing the contact's image, resized to fit the
	 *         provided image size. If no thumbnail exists, returns null.
	 */
	private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

		// Ensures the Fragment is still added to an activity. As this method is
		// called in a
		// background thread, there's the possibility the Fragment is no longer
		// attached and
		// added to an activity. If so, no need to spend resources loading the
		// contact photo.
		if (!isAdded() || getActivity() == null) {
			return null;
		}

		// Instantiates an AssetFileDescriptor. Given a content Uri pointing to
		// an image file, the
		// ContentResolver can return an AssetFileDescriptor for the file.
		AssetFileDescriptor afd = null;

		// This "try" block catches an Exception if the file descriptor returned
		// from the Contacts
		// Provider doesn't point to an existing file.
		try {
			Uri thumbUri;
			// If Android 3.0 or later, converts the Uri passed as a string to a
			// Uri object.
			if (Util.hasHoneycomb()) {
				thumbUri = Uri.parse(photoData);
			} else {
				// For versions prior to Android 3.0, appends the string
				// argument to the content
				// Uri for the Contacts table.
				final Uri contactUri = Uri.withAppendedPath(
						Contacts.CONTENT_URI, photoData);

				// Appends the content Uri for the Contacts.Photo table to the
				// previously
				// constructed contact Uri to yield a content URI for the
				// thumbnail image
				thumbUri = Uri.withAppendedPath(contactUri,
						Photo.CONTENT_DIRECTORY);
			}
			// Retrieves a file descriptor from the Contacts Provider. To learn
			// more about this
			// feature, read the reference documentation for
			// ContentResolver#openAssetFileDescriptor.
			afd = getActivity().getContentResolver().openAssetFileDescriptor(
					thumbUri, "r");

			// Gets a FileDescriptor from the AssetFileDescriptor. A
			// BitmapFactory object can
			// decode the contents of a file pointed to by a FileDescriptor into
			// a Bitmap.
			FileDescriptor fileDescriptor = afd.getFileDescriptor();

			if (fileDescriptor != null) {
				// Decodes a Bitmap from the image pointed to by the
				// FileDescriptor, and scales it
				// to the specified width and height
				return ImageLoader.decodeSampledBitmapFromDescriptor(
						fileDescriptor, imageSize, imageSize);
			}
		} catch (FileNotFoundException e) {
			// If the file pointed to by the thumbnail URI doesn't exist, or the
			// file can't be
			// opened in "read" mode, ContentResolver.openAssetFileDescriptor
			// throws a
			// FileNotFoundException.
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Contact photo thumbnail not found for contact "
						+ photoData + ": " + e.toString());
			}
		} finally {
			// If an AssetFileDescriptor was returned, try to close it
			if (afd != null) {
				try {
					afd.close();
				} catch (IOException e) {
					// Closing a file descriptor might cause an IOException if
					// the file is
					// already closed. Nothing extra is needed to handle this.
				}
			}
		}

		// If the decoding failed, returns null
		return null;
	}

	/**
	 * This is a subclass of CursorAdapter that supports binding Cursor columns
	 * to a view layout. If those items are part of search results, the search
	 * string is marked by highlighting the query text. An
	 * {@link AlphabetIndexer} is used to allow quicker navigation up and down
	 * the ListView.
	 */
	private class ContactsAdapter extends CursorAdapter implements
			SectionIndexer {
		private LayoutInflater mInflater; // Stores the layout inflater
		private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer
													// instance
		private TextAppearanceSpan highlightTextSpan; // Stores the highlight
														// text appearance style
		HashMap<RadioGroup, ArrayList<RadioButton>> hmapRadiogroup = new HashMap<RadioGroup, ArrayList<RadioButton>>();

		/**
		 * Instantiates a new Contacts Adapter.
		 * 
		 * @param context
		 *            A context that has access to the app's layout.
		 */
		public ContactsAdapter(Context context) {
			super(context, null, 0);

			// Stores inflater for use later
			mInflater = LayoutInflater.from(context);

			// Loads a string containing the English alphabet. To fully localize
			// the app, provide a
			// strings.xml file in res/values-<x> directories, where <x> is a
			// locale. In the file,
			// define a string with android:name="alphabet" and contents set to
			// all of the
			// alphabetic characters in the language in their proper sort order,
			// in upper case if
			// applicable.
			final String alphabet = context.getString(R.string.alphabet);

			// Instantiates a new AlphabetIndexer bound to the column used to
			// sort contact names.
			// The cursor is left null, because it has not yet been retrieved.
			mAlphabetIndexer = new AlphabetIndexer(null,
					ContactsQuery.SORT_KEY, alphabet);

			// Defines a span for highlighting the part of a display name that
			// matches the search
			// string
			highlightTextSpan = new TextAppearanceSpan(getActivity(),
					R.style.searchTextHiglight);
		}

		/**
		 * Identifies the start of the search string in the display name column
		 * of a Cursor row. E.g. If displayName was "Adam" and search query
		 * (mSearchTerm) was "da" this would return 1.
		 * 
		 * @param displayName
		 *            The contact display name.
		 * @return The starting position of the search string in the display
		 *         name, 0-based. The method returns -1 if the string is not
		 *         found in the display name, or if the search string is empty
		 *         or null.
		 */
		private int indexOfSearchQuery(String displayName) {
			if (!TextUtils.isEmpty(mSearchTerm)) {
				return displayName.toLowerCase(Locale.getDefault()).indexOf(
						mSearchTerm.toLowerCase(Locale.getDefault()));
			}
			return -1;
		}

		/**
		 * Overrides newView() to inflate the list item views.
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			final int position = cursor.getPosition();
			// Inflates the list item layout.
			final View itemLayout = mInflater.inflate(
					R.layout.contact_list_item, viewGroup, false);

			// Creates a new ViewHolder in which to store handles to each view
			// resource. This
			// allows bindView() to retrieve stored references instead of
			// calling findViewById for
			// each instance of the layout.
			final ViewHolder holder = new ViewHolder();
			holder.text1 = (CheckedTextView) itemLayout
					.findViewById(android.R.id.text1);
			holder.text2 = (TextView) itemLayout
					.findViewById(android.R.id.text2);
			holder.textName = (TextView) itemLayout.findViewById(R.id.phone);
			holder.icon = (QuickContactBadge) itemLayout
					.findViewById(android.R.id.icon);
			holder.rg = (RadioGroup) itemLayout.findViewById(R.id.rg);

			// else {
			// // holder.rg.removeAllViews();
			// ArrayList<RadioButton> rdsLocal = hmapRadiogroup.get(holder.rg);
			// if (rdsLocal != null) {
			// Log.d("MCLF", "rdsLocal" + rdsLocal.size());
			// for (RadioButton rdb : rdsLocal) {
			// holder.rg.addView(rdb);
			// }
			// }
			// }
			// .findViewById(R.id.radioLinearLayout);

			// Stores the resourceHolder instance in itemLayout. This makes
			// resourceHolder
			// available to bindView and other methods that receive a handle to
			// the item view.
			itemLayout.setTag(holder);

			// Returns the item layout view
			return itemLayout;
		}

		/**
		 * Binds data from the Cursor to the provided view.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final int position = cursor.getPosition();
			// Gets handles to individual view resources
			final ViewHolder holder = (ViewHolder) view.getTag();
			// For Android 3.0 and later, gets the thumbnail image Uri from the
			// current Cursor row.
			// For platforms earlier than 3.0, this isn't necessary, because the
			// thumbnail is
			// generated from the other fields in the row.
			final String photoUri = cursor
					.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

			final String displayName = cursor
					.getString(ContactsQuery.DISPLAY_NAME);
			// holder.rg = hmapRadiogroup.get(holder.rg);
			// holder.rg.removeAllViews();
			// ArrayList<RadioButton> radioButtons =
			// hmapRadiogroup.get(holder.rg);
			// for (RadioButton child : radioButtons) {
			// holder.rg.addView(child);
			// }

			// niaz code for Listview
			// /niaz Testing iterate all phone numbers
			Uri aContactUri = Contacts.getLookupUri(
					cursor.getLong(ContactsQuery.ID),
					cursor.getString(ContactsQuery.LOOKUP_KEY));
			Cursor aContactCursor = itsContext.getContentResolver().query(
					aContactUri, null, null, null, null);
			if (aContactCursor.moveToFirst()) {
				int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
				String aContactCursorId = cursor.getString(idx);
				int aNameIdx = aContactCursor
						.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
				//
				// aEmailId = cursor.getString(ContactsQuery.LOOKUP_KEY);
				// aPhoneNumber = cursor.getString(ContactsQuery.ID);

				Cursor aPhoneNumberCursor = itsContext
						.getContentResolver()
						.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ " = " + aContactCursorId, null, null);
				// Testing all phone numbers

				int cnt = aPhoneNumberCursor.getCount();
				holder.rg.removeAllViews();
				holder.rg.setOnCheckedChangeListener(null);
				holder.textName.setText("");
				boolean isFirstRadioButton = true;
				ArrayList<RadioButton> rdbs = new ArrayList<RadioButton>();
				String phSel = hmapRadio.get(position);
				while (aPhoneNumberCursor.moveToNext()) {

					int colIdx = aPhoneNumberCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					String ph = aPhoneNumberCursor.getString(colIdx);
					Log.d("Contact Check..", "Contact Check=" + ph);
					RadioButton radiobutton = new RadioButton(itsContext);
					radiobutton.setId(UIUtil.getRandomNumber());
					radiobutton.setTextColor(Color.BLACK);
					radiobutton.setText(ph);
					if ((phSel != null) && (phSel.equals(ph))) {
						holder.textName.setText(ph);
						radiobutton.setChecked(true);
					} else if ((phSel == null) && isFirstRadioButton) {
						holder.textName.setText(ph);
						isFirstRadioButton = false;
					}
					holder.rg.addView(radiobutton);
					rdbs.add(radiobutton);
				}
				holder.rg
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								holder.rg.check(checkedId);
								String ph = ((RadioButton) holder.rg
										.findViewById(holder.rg
												.getCheckedRadioButtonId()))
										.getText().toString();
								hmapRadio.put(position, ph);
								holder.textName.setText(ph);
							}
						});

				// if (cnt == 1) {
				// holder.rg.setVisibility(View.GONE);
				// }

				aPhoneNumberCursor.close();
				// Log.d("TEST", "holder.rg.getChildAt(0).getId()="
				// + holder.rg.getChildAt(0).getId());
				// holder.rg.check(holder.rg.getChildAt(0).getId());

				hmapRadiogroup.put(holder.rg, rdbs);
			}

			final int startIndex = indexOfSearchQuery(displayName);
			// here miss code
			if (startIndex == -1) {
				// If the user didn't do a search, or the search string didn't
				// match a display
				// name, show the display name without highlighting
				holder.text1.setText(displayName);

				if (TextUtils.isEmpty(mSearchTerm)) {
					// If the search search is empty, hide the second line of
					// text
					holder.text2.setVisibility(View.GONE);
				} else {
					// Shows a second line of text that indicates the search
					// string matched
					// something other than the display name
					holder.text2.setVisibility(View.VISIBLE);
				}
			} else {
				// If the search string matched the display name, applies a
				// SpannableString to
				// highlight the search string with the displayed display name

				// Wraps the display name in the SpannableString
				final SpannableString highlightedName = new SpannableString(
						displayName);

				// Sets the span to start at the starting point of the match and
				// end at "length"
				// characters beyond the starting point
				highlightedName.setSpan(highlightTextSpan, startIndex,
						startIndex + mSearchTerm.length(), 0);

				// Binds the SpannableString to the display name View object
				holder.text1.setText(highlightedName);

				// Since the search string matched the name, this hides the
				// secondary message
				holder.text2.setVisibility(View.GONE);
			}

			Log.d("positionB", String.valueOf(position));
			HashMap<Integer, Boolean> aHashMap = new HashMap<Integer, Boolean>();
			aHashMap.put(position, true);
			if (checkedTvStates.contains(aHashMap)) {
				Log.i("true", "true");

				holder.text1.setChecked(true);
			} else {
				holder.text1.setChecked(false);
			}
			// Processes the QuickContactBadge. A QuickContactBadge first
			// appears as a contact's
			// thumbnail image with styling that indicates it can be touched for
			// additional
			// information. When the user clicks the image, the badge expands
			// into a dialog box
			// containing the contact's details and icons for the built-in apps
			// that can handle
			// each detail type.

			// Generates the contact lookup Uri
			final Uri contactUri = Contacts.getLookupUri(
					cursor.getLong(ContactsQuery.ID),
					cursor.getString(ContactsQuery.LOOKUP_KEY));

			// Binds the contact's lookup Uri to the QuickContactBadge
			holder.icon.assignContactUri(contactUri);

			// Loads the thumbnail image pointed to by photoUri into the
			// QuickContactBadge in a
			// background worker thread
			mImageLoader.loadImage(photoUri, holder.icon);
			// niaz
		}

		/**
		 * Overrides swapCursor to move the new Cursor into the AlphabetIndex as
		 * well as the CursorAdapter.
		 */
		@Override
		public Cursor swapCursor(Cursor newCursor) {
			// Update the AlphabetIndexer with new cursor as well
			mAlphabetIndexer.setCursor(newCursor);
			return super.swapCursor(newCursor);
		}

		/**
		 * An override of getCount that simplifies accessing the Cursor. If the
		 * Cursor is null, getCount returns zero. As a result, no test for
		 * Cursor == null is needed.
		 */
		@Override
		public int getCount() {
			if (getCursor() == null) {
				return 0;
			}
			return super.getCount();
		}

		/**
		 * Defines the SectionIndexer.getSections() interface.
		 */
		@Override
		public Object[] getSections() {
			return mAlphabetIndexer.getSections();
		}

		/**
		 * Defines the SectionIndexer.getPositionForSection() interface.
		 */
		@Override
		public int getPositionForSection(int i) {
			if (getCursor() == null) {
				return 0;
			}
			return mAlphabetIndexer.getPositionForSection(i);
		}

		/**
		 * Defines the SectionIndexer.getSectionForPosition() interface.
		 */
		@Override
		public int getSectionForPosition(int i) {
			if (getCursor() == null) {
				return 0;
			}
			return mAlphabetIndexer.getSectionForPosition(i);
		}

		/**
		 * A class that defines fields for each resource ID in the list item
		 * layout. This allows ContactsAdapter.newView() to store the IDs once,
		 * when it inflates the layout, instead of calling findViewById in each
		 * iteration of bindView.
		 */
		private class ViewHolder {
			CheckedTextView text1;
			TextView text2, textName;
			QuickContactBadge icon;
			RadioGroup rg;
		}
	}

	/**
	 * This interface must be implemented by any activity that loads this
	 * fragment. When an interaction occurs, such as touching an item from the
	 * ListView, these callbacks will be invoked to communicate the event back
	 * to the activity.
	 */
	public interface OnContactsInteractionListener {
		/**
		 * Called when a contact is selected from the ListView.
		 * 
		 * @param contactUri
		 *            The contact Uri.
		 */
		public void onContactSelected(Uri contactUri);

		/**
		 * Called when the ListView selection is cleared like when a contact
		 * search is taking place or is finishing.
		 */
		public void onSelectionCleared();
	}

	/**
	 * This interface defines constants for the Cursor and CursorLoader, based
	 * on constants defined in the
	 * {@link android.provider.ContactsContract.Contacts} class.
	 */
	public interface ContactsQuery {

		// An identifier for the loader
		final static int QUERY_ID = 1;

		// A content URI for the Contacts table
		final static Uri CONTENT_URI = Contacts.CONTENT_URI;

		// The search/filter query Uri
		final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

		// The selection clause for the CursorLoader query. The search criteria
		// defined here
		// restrict results to contacts that have a display name and are linked
		// to visible groups.
		// Notice that the search on the string provided by the user is
		// implemented by appending
		// the search string to CONTENT_FILTER_URI.
		@SuppressLint("InlinedApi")
		final static String SELECTION = (Util.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY
				: Contacts.DISPLAY_NAME)
				+ "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

		// The desired sort order for the returned Cursor. In Android 3.0 and
		// later, the primary
		// sort key allows for localization. In earlier versions. use the
		// display name as the sort
		// key.
		@SuppressLint("InlinedApi")
		final static String SORT_ORDER = Util.hasHoneycomb() ? Contacts.SORT_KEY_PRIMARY
				: Contacts.DISPLAY_NAME;

		// The projection for the CursorLoader query. This is a list of columns
		// that the Contacts
		// Provider should return in the Cursor.
		@SuppressLint("InlinedApi")
		final static String[] PROJECTION = {

				// The contact's row id
				Contacts._ID,

				// A pointer to the contact that is guaranteed to be more
				// permanent than _ID. Given
				// a contact's current _ID value and LOOKUP_KEY, the Contacts
				// Provider can generate
				// a "permanent" contact URI.
				Contacts.LOOKUP_KEY,

				// In platform version 3.0 and later, the Contacts table
				// contains
				// DISPLAY_NAME_PRIMARY, which either contains the contact's
				// displayable name or
				// some other useful identifier such as an email address. This
				// column isn't
				// available in earlier versions of Android, so you must use
				// Contacts.DISPLAY_NAME
				// instead.
				Util.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY
						: Contacts.DISPLAY_NAME,

				// In Android 3.0 and later, the thumbnail image is pointed to
				// by
				// PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct
				// pointer; instead,
				// you generate the pointer from the contact's ID value and
				// constants defined in
				// android.provider.ContactsContract.Contacts.
				Util.hasHoneycomb() ? Contacts.PHOTO_THUMBNAIL_URI
						: Contacts._ID,

				// The sort order column for the returned Cursor, used by the
				// AlphabetIndexer
				SORT_ORDER, };

		// The query column numbers which map to each value in the projection
		final static int ID = 0;
		final static int LOOKUP_KEY = 1;
		final static int DISPLAY_NAME = 2;
		final static int PHOTO_THUMBNAIL_DATA = 3;
		final static int SORT_KEY = 4;
	}
}
