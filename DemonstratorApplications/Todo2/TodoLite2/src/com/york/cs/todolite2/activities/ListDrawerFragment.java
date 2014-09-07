package com.york.cs.todolite2.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Database.ChangeEvent;
import com.york.cs.services.activities.EntityAdapter;
import com.york.cs.services.authentication.UserManager;
import com.york.cs.todolite2.R;
import com.york.cs.todolite2.document2.Application;
import com.york.cs.todolite2.document2.ListTasks;
import com.york.cs.todolite2.document2.TodoDB;
import com.york.cs.todolite2.util.Utilities;

public class ListDrawerFragment extends Fragment {

	private static final String TAG = "ListDrawerFragment";

	private static final String STATE_SELECTED_LIST_ID = "selected_list_id";

	private ListSelectionCallback mCallbacks;

	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = -1;

	private ListsAdapter mListsAdapter;

	private TodoDB todoDB;

	String m_Text = "";

	// A Query subclass that automatically refreshes the result rows every time
	// the database changes. All you need to do is use add a listener to observe
	// changes.
	// private LiveQuery mListsLiveQuery;

	java.util.List<ListTasks> lists;

	public ListDrawerFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "-------------------on create Lists");
		todoDB = ((Application) getActivity().getApplication()).getTodoDB();
		mListsAdapter = new ListsAdapter(getActivity(), lists);
		mListsAdapter.update();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_list_drawer, container, false);

		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view,
							int position, long id) {
						selectListItem(position, true);

						Log.d("----------", "itemselect" + position);
					}
				});

		mDrawerListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, final int position, long id) {
						PopupMenu popup = new PopupMenu(getActivity(), view);

						popup.getMenu().add(Menu.NONE, 1, 1, "Edit");
						popup.getMenu().add(Menu.NONE, 3, 3, "Share");

						popup.getMenu().add(
								Menu.NONE,
								2,
								2,
								getResources()
										.getString(R.string.action_delete));
						// popup.getMenu().add(
						// getResources()
						// .getString(R.string.action_delete));
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								final ListTasks list = (ListTasks) mListsAdapter
										.getItem(position);
								System.out.println("----***********"
										+ item.getItemId());
								if (item.getItemId() == 2) {
									todoDB.getListTasks().remove(list);
									try {
										todoDB.sync();

										mListsAdapter.update();
										if (mCurrentSelectedPosition == position) {
											mCurrentSelectedPosition = 0;

										}

										if (mCallbacks != null) {
											if (mListsAdapter.getCount() > 0)
												mCallbacks
														.onListSelected(((ListTasks) mListsAdapter
																.getItem(mCurrentSelectedPosition))
																.getId());
											else
												mCallbacks.onListSelected(null);
										}

									} catch (CouchbaseLiteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (item.getItemId() == 1) {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle("Title");
									m_Text = list.getTitle();
									final EditText input = new EditText(
											getActivity());
									input.setInputType(InputType.TYPE_CLASS_TEXT);
									input.setText(m_Text);
									builder.setView(input);

									// Set up the buttons
									builder.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													m_Text = input.getText()
															.toString();
													list.setTitle(m_Text);
													todoDB.getListTasks().add(
															list);

													try {
														todoDB.sync();
													} catch (CouchbaseLiteException e) {
														// TODO Auto-generated
														// catch block
														e.printStackTrace();
													}
													mListsAdapter.update();

												}
											});

									builder.show();

								}
								if (item.getItemId() == 3) {
									AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
									alert.setTitle("Share with:");

									final EditText input = new EditText(getActivity());
									input.setMaxLines(1);
									input.setSingleLine(true);
									input.setHint("Email of user to share with");
									alert.setView(input);

									alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String email = input.getText().toString();
											if (email.length() == 0) {
												// TODO: Show an error message.
												return;
											}
											try {
												
												// ListTasks list = new ListTasks(title, currentUserId);
												list.addMember(email);
												// TODO current user id
												todoDB.getListTasks().add(list);
												todoDB.sync();

												
											} catch (CouchbaseLiteException e) {
												Log.e(Application.TAG, "Cannot create a new list", e);
											}
										}
									});

									alert.setNegativeButton("Cancel",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int whichButton) {
												}
											});

									alert.show();

								}

								return true;
							}
						});

						popup.show();
						return true;
					}
				});

		mDrawerListView.setAdapter(mListsAdapter);
		if (mCurrentSelectedPosition > mListsAdapter.getCount()) {
			mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		}

		return mDrawerListView;
	}

	private Database getDatabase() {
		Application application = (Application) getActivity().getApplication();
		return application.getDatabase();
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
				R.drawable.ic_drawer, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded())
					return;

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded())
					return;

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * @param lists
	 *            An enumerator for Couchbase Lite View Query results.
	 * @return
	 */
	/*
	 * private int getCurrentSelectedPosition(java.util.List<List2> lists) { if
	 * (lists == null) return -1;
	 * 
	 * Application application = (Application) getActivity().getApplication();
	 * String currentListId = application.getCurrentListId();
	 * 
	 * if (currentListId == null) return lists.size() > 0 ? 0 : -1;
	 * 
	 * int position = 0;
	 * 
	 * for (List2 list : lists) { if (currentListId.equals(list.get_id()))
	 * break; ++position; }
	 * 
	 * return position; }
	 */
	/*
	 * public void refreshLists() {
	 * 
	 * /*lists = List2 .getByTitle(getDatabase(), null, null, null, false, 10);
	 * 
	 * Log.d(TAG, "-------------------on create Lists" + lists.size());
	 * mListsAdapter.update();
	 * 
	 * 
	 * }
	 */
	private void selectListItem(int position, boolean closeDrawer) {
		mCurrentSelectedPosition = position;

		if (mDrawerListView != null)
			mDrawerListView.setItemChecked(position, true);

		if (mDrawerLayout != null && closeDrawer)
			mDrawerLayout.closeDrawer(mFragmentContainerView);

		if (mListsAdapter.getCount() > position) {
			ListTasks list = (ListTasks) mListsAdapter.getItem(position);
			Application application = (Application) getActivity()
					.getApplication();
			application.setCurrentListId(list.getId());

			if (mCallbacks != null) {

				mCallbacks.onListSelected(list.getId());
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (ListSelectionCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_LIST_ID, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface ListSelectionCallback {
		void onListSelected(String id);
	}

	private class ListsAdapter extends EntityAdapter<ListTasks> {

		public ListsAdapter(Context context, List<ListTasks> lists) {
			super(context, lists);
/*
			Database db = getDatabase();
			db.addChangeListener(new Database.ChangeListener() {

				@Override
				public void changed(ChangeEvent arg0) {
					System.out.println("preruunable1");
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							System.out.println("ruunable1");
							update();

						}
					});

				}
			});*/
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {

				convertView = mLayoutInflater.inflate(
						R.layout.view_list_drawer, null);
			}

			ListTasks list = (ListTasks) getItem(position);
			TextView textView = (TextView) convertView;
			textView.setText((CharSequence) list.getTitle());

			return convertView;
		}

		public void update() {
			
			System.out.println("-------- update in lists" );
			java.util.List<ListTasks> listsnew;
			try {

				Iterator<ListTasks> listIterator = (Iterator<ListTasks>) todoDB
						.getListTasks()
						.findByKey(ListTasks.ListTasks_title, 0, 0, null)
						.iterator();

				listsnew = Utilities.copyIterator(listIterator);
				/*
				 * listsnew = ListTasks.getByTitle(getDatabase(), null, null,
				 * null, false, 10);
				 */
				if (lists == null) {
					lists = new ArrayList<ListTasks>();
				}

				lists.clear();
				lists.addAll(listsnew);

				ListsAdapter.this.list = lists;
				notifyDataSetChanged();
			} catch (CouchbaseLiteException e) {
				e.printStackTrace();
			}

		}

	}
}
