package com.york.cs.todolite2.activities;

import android.app.Activity;


public class ShareActivity extends Activity {
	/*
	public static final String SHARE_ACTIVITY_CURRENT_LIST_ID_EXTRA = "current_list_id";
	public static final String STATE_CURRENT_LIST_ID = "current_list_id";

	private UserAdapter mAdapter = null;
	private String mCurrentListId = null;
	private ListTasks mCurrentList = null;

	private Database getDatabase() {
		Application application = (Application) getApplication();
		return application.getDatabase();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState != null) {
			mCurrentListId = savedInstanceState
					.getString(STATE_CURRENT_LIST_ID);
		} else {
			Intent intent = getIntent();
			mCurrentListId = intent
					.getStringExtra(SHARE_ACTIVITY_CURRENT_LIST_ID_EXTRA);
		}

		mCurrentList = ListTasks.getById(getDatabase(), mCurrentListId);

		Application application = (Application) getApplication();
		// Query query=null;// = Profile.getQueryByName(getDatabase(),
		// application.getCurrentUserId());

		java.util.List<Profile> profiles = new ArrayList<Profile>();
		try {
			profiles = Profile.getProfilesByName(getDatabase(), null, null,
					null, false, 10);
		} catch (ConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAdapter = new UserAdapter(this, profiles);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(mAdapter);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(STATE_CURRENT_LIST_ID, mCurrentListId);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// mAdapter.invalidate();
		super.onDestroy();
	}

	private class UserAdapter extends EntityAdapter {
		public UserAdapter(Context context, java.util.List<Profile> profiles) {
			super(context, profiles);
		}

		private boolean isMemberOfTheCurrentList(Profile user) {
			java.util.List<String> members = (java.util.List<String>) mCurrentList
					.getMembers_id();
			return members != null ? members.contains(user.getUserId()) : false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.view_user, null);
			}

			final Task task = (Task) getItem(position);

			TextView text = (TextView) convertView.findViewById(R.id.text);
			text.setText((String) task.getTitle());

			final Profile user = (Profile) getItem(position);
			final CheckBox checkBox = (CheckBox) convertView
					.findViewById(R.id.checked);
			boolean checked = isMemberOfTheCurrentList(user);
			checkBox.setChecked(checked);
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					if (checkBox.isChecked()) {
						// List.addMemberToList(mCurrentList, user);
						mCurrentList.addMember(user.getUserId());
					} else {
						mCurrentList.removeMember(user.getUserId());
					}

					try {
						ListTasks.updateList(getDatabase(), mCurrentList);
					} catch (ConflictException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CouchbaseLiteException e) {
						// TODO Auto-generated catch blockCannot update a member
						// to a list"
						e.printStackTrace();
					}
				}
			});

			return convertView;
		}
	}
	*/
}
