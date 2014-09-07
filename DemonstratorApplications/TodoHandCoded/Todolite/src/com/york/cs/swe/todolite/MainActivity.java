package com.york.cs.swe.todolite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.util.Log;
import com.york.cs.swe.R;
import com.york.cs.swe.activities.BaseActivity;
import com.york.cs.swe.todolite.document.List;
import com.york.cs.swe.todolite.document.Task;
import com.york.cs.swe.todolite.helper.LiveQueryAdapter;


public class MainActivity extends BaseActivity implements
		ListDrawerFragment.ListSelectionCallback {
	private ListDrawerFragment mDrawerFragment;

	private CharSequence mTitle;

	private Database getDatabase() {
		Application application = (Application) getApplication();
		return application.getDatabase();
	}

	private String getCurrentListId() {
		Application application = (Application) getApplication();
		String currentListId = application.getCurrentListId();
		if (currentListId == null) {
			try {
				QueryEnumerator enumerator = List.getQuery(getDatabase()).run();
				if (enumerator.getCount() > 0) {
					currentListId = enumerator.getRow(0).getDocument().getId();
				}
			} catch (CouchbaseLiteException e) {
			}
		}
		return currentListId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		mDrawerFragment = (ListDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		mDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		String currentListId = getCurrentListId();
		if (currentListId != null) {
			displayListContent(currentListId);
		}
/*
		// Log the current user in and start replication sync
		Application application = (Application) getApplication();
		application.getOnSyncProgressChangeObservable().addObserver(
				new Observer() {
					@Override
					public void update(final Observable observable,
							final Object data) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Application.SyncProgress progress = (Application.SyncProgress) data;
								if (progress.totalCount > 0
										&& progress.completedCount < progress.totalCount) {
									setProgressBarIndeterminateVisibility(true);
								} else {
									setProgressBarIndeterminateVisibility(false);
								}
							}
						});
					}
				});
		application.getOnSyncUnauthorizedObservable().addObserver(
				new Observer() {
					@Override
					public void update(Observable observable, Object data) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d(Application.TAG,
										"OnSyncUnauthorizedObservable called, show toast");

								// clear the saved user id, since our session is
								// no longer valid
								// and we want to show the login button
								Application application = (Application) getApplication();
								application.setCurrentUserId(null);
								invalidateOptionsMenu();

								String msg = "Sync unable to continue due to invalid session/login";
								Toast.makeText(getApplicationContext(), msg,
										Toast.LENGTH_LONG).show();

							}
						});

					}
				});
				*/

	}

	private void displayListContent(String listDocId) {
		Document document = getDatabase().getDocument(listDocId);

		getActionBar().setSubtitle((String) document.getProperty("title"));

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, TasksFragment.newInstance(listDocId))
				.commit();

		Application application = (Application) getApplication();
		application.setCurrentListId(listDocId);
	}

	@Override
	public void onListSelected(String id) {
		displayListContent(id);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);

			// Add Login button if the user has not been logged in.
			Application application = (Application) getApplication();

			// Add Login button if the user has not been logged in.
			addLoginButton(menu);
			// Add sync button button if the user has been logged in

			addSyncNowButton(menu);
			// add remove account button
			addRemoveaccountNowButton(menu);

			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void createNewList() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

		final EditText input = new EditText(this);
		input.setMaxLines(1);
		input.setSingleLine(true);
		input.setHint(getResources().getString(R.string.hint_new_list));
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String title = input.getText().toString();
				if (title.length() == 0) {
					
					return;
				}
				try {
					String currentUserId = ((Application) getApplication())
							.getCurrentUserId();
					
					System.out.println("Current user: "+currentUserId);
					Document document = List.createNewList(getDatabase(),
							title, currentUserId);
					displayListContent(document.getId());
					invalidateOptionsMenu();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_new_list) {
			createNewList();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);
	}

	/*
	 * private Session.OpenRequest getFacebookOpenRequest() {
	 * Session.OpenRequest request = new Session.OpenRequest(this)
	 * .setPermissions(Arrays.asList("email")).setCallback( statusCallback);
	 * return request; }
	 * 
	 * private void loginWithFacebookAndStartSync() { Session session =
	 * Session.getActiveSession();
	 * 
	 * if (session == null) { session = new Session(this);
	 * Session.setActiveSession(session); }
	 * 
	 * if (!session.isOpened() && !session.isClosed()) {
	 * session.openForRead(getFacebookOpenRequest()); } else {
	 * Session.openActiveSession(this, true, statusCallback); } }
	 */


	public static class TasksFragment extends Fragment {
		private static final String ARG_LIST_DOC_ID = "id";

		private static final int REQUEST_TAKE_PHOTO = 1;
		private static final int REQUEST_CHOOSE_PHOTO = 2;

		private static final int THUMBNAIL_SIZE_PX = 150;

		private TaskAdapter mAdapter;
		private String mImagePathToBeAttached;
		private Bitmap mImageToBeAttached;
		private Document mCurrentTaskToAttachImage;

		public static TasksFragment newInstance(String id) {
			TasksFragment fragment = new TasksFragment();

			Bundle args = new Bundle();
			args.putString(ARG_LIST_DOC_ID, id);
			fragment.setArguments(args);

			return fragment;
		}

		public TasksFragment() {
		}

		private Database getDatabase() {
			Application application = (Application) getActivity()
					.getApplication();
			return application.getDatabase();
		}

		private File createImageFile() throws IOException {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			String fileName = "TODO_LITE_" + timeStamp + "_";
			File storageDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File image = File.createTempFile(fileName, ".jpg", storageDir);
			mImagePathToBeAttached = image.getAbsolutePath();
			return image;
		}

		private void dispatchTakePhotoIntent() {
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getActivity()
					.getPackageManager()) != null) {
				File photoFile = null;
				try {
					photoFile = createImageFile();
				} catch (IOException e) {
					Log.e(Application.TAG, "Cannot create a temp image file", e);
				}

				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent,
							REQUEST_TAKE_PHOTO);
				}
			}
		}

		private void dispatchChoosePhotoIntent() {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "Select File"),
					REQUEST_CHOOSE_PHOTO);
		}

		private void deleteCurrentPhoto() {
			if (mImageToBeAttached != null) {
				mImageToBeAttached.recycle();
				mImageToBeAttached = null;

				ViewGroup createTaskPanel = (ViewGroup) getActivity()
						.findViewById(R.id.create_task);
				ImageView imageView = (ImageView) createTaskPanel
						.findViewById(R.id.image);
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_camera));
			}
		}

		private void attachImage(final Document task) {
			CharSequence[] items;
			if (mImageToBeAttached != null)
				items = new CharSequence[] { "Take photo", "Choose photo",
						"Delete photo" };
			else
				items = new CharSequence[] { "Take photo", "Choose photo" };

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Add picture");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						mCurrentTaskToAttachImage = task;
						dispatchTakePhotoIntent();
					} else if (item == 1) {
						mCurrentTaskToAttachImage = task;
						dispatchChoosePhotoIntent();
					} else {
						deleteCurrentPhoto();
					}
				}
			});
			builder.show();
		}

		private void dispatchImageViewIntent(Bitmap image) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 50, stream);
			byte[] byteArray = stream.toByteArray();

			long l = byteArray.length;

			Intent intent = new Intent(getActivity(), ImageViewActivity.class);
			intent.putExtra(ImageViewActivity.INTENT_IMAGE, byteArray);
			startActivity(intent);
		}

		private void deleteTask(int position) {
			Document task = (Document) mAdapter.getItem(position);
			try {
				Task.deleteTask(task);
			} catch (CouchbaseLiteException e) {
				Log.e(Application.TAG, "Cannot delete a task", e);
			}
		}

		private void updateTaskTitle(final int position) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Title");
			final Document task = (Document) mAdapter.getItem(position);
			String m_Text = (String) task.getProperty("title");
			final EditText input = new EditText(getActivity());
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			input.setText(m_Text);
			builder.setView(input);

			// Set up the buttons
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String m_title = input.getText().toString();
							
							try {
								
								
							
									Task.updateTitle(task, m_title);
						
								
								mAdapter.notifyDataSetChanged();
								
							} catch (CouchbaseLiteException e) {
								Log.e(Application.TAG, "Cannot edit a task", e);
								e.printStackTrace();
							}

						}
					});

			builder.show();
			
			
		}
		
		
		private void shareTaskWith(final int position) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Share with:");
			final Document task = (Document) mAdapter.getItem(position);
			
			final EditText input = new EditText(getActivity());
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			
			input.setHint("user email");
			builder.setView(input);

			// Set up the buttons
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String userId = input.getText().toString();
							
							try {
								
								
							
									Task.addMemberToTask(task, userId);
						
								
								mAdapter.notifyDataSetChanged();
								
							} catch (CouchbaseLiteException e) {
								Log.e(Application.TAG, "Cannot edit a task", e);
								e.printStackTrace();
							}

						}
					});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// no operation
					
				}
			});
			
			builder.show();
			
			
		}
		
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final ListView listView = (ListView) inflater.inflate(
					R.layout.fragment_main, container, false);

			final String listId = getArguments().getString(ARG_LIST_DOC_ID);

			ViewGroup header = (ViewGroup) inflater.inflate(
					R.layout.view_task_create, listView, false);

			final ImageView imageView = (ImageView) header
					.findViewById(R.id.image);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					attachImage(null);
				}
			});

			final EditText text = (EditText) header.findViewById(R.id.text);
			text.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View view, int i, KeyEvent keyEvent) {
					if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
							&& (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
						String inputText = text.getText().toString();
						if (inputText.length() > 0) {
							try {
								Task.createTask(getDatabase(), inputText,
										mImageToBeAttached, listId);
							} catch (CouchbaseLiteException e) {
								Log.e(Application.TAG,
										"Cannot create new task", e);
							}
						}

						// Reset text and current selected photo if available.
						text.setText("");
						deleteCurrentPhoto();

						return true;
					}
					return false;
				}
			});

			listView.addHeaderView(header);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					Document task = (Document) adapter
							.getItemAtPosition(position);
					boolean checked = ((Boolean) task.getProperty("checked"))
							.booleanValue();
					try {
						Task.updateCheckedStatus(task, checked);
					} catch (CouchbaseLiteException e) {
						Log.e(Application.TAG, "Cannot update checked status",
								e);
						e.printStackTrace();
					}
				}
			});

			listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, final int position, long id) {
				
					PopupMenu popup = new PopupMenu(getActivity(), view);
					popup.getMenu().add(Menu.NONE, 1, 1, "Edit");

					popup.getMenu().add(Menu.NONE, 2, 2,
							getResources().getString(R.string.action_delete));
					popup.getMenu().add(Menu.NONE, 3, 3,"Share");
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {

							if (item.getItemId() == 2) {
								deleteTask(position - 1);
								
							} else if (item.getItemId() == 1) {
								updateTaskTitle(position - 1);
							} else if (item.getItemId() == 3) {
								shareTaskWith(position-1);
								
							}
							
							return true;
						}
					});
					
					
					popup.show();
					return true;
				}
			});

			LiveQuery query = Task.getQuery(getDatabase(), listId)
					.toLiveQuery();
			mAdapter = new TaskAdapter(getActivity(), query);
			listView.setAdapter(mAdapter);

			return listView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			if (resultCode != RESULT_OK) {
				if (mCurrentTaskToAttachImage != null) {
					mCurrentTaskToAttachImage = null;
				}
				return;
			}

			if (requestCode == REQUEST_TAKE_PHOTO) {
				mImageToBeAttached = BitmapFactory
						.decodeFile(mImagePathToBeAttached);

				// Delete the temporary image file
				File file = new File(mImagePathToBeAttached);
				file.delete();
				mImagePathToBeAttached = null;
			} else if (requestCode == REQUEST_CHOOSE_PHOTO) {
				try {
					Uri uri = data.getData();
					mImageToBeAttached = MediaStore.Images.Media.getBitmap(
							getActivity().getContentResolver(), uri);
				} catch (IOException e) {
					Log.e(Application.TAG,
							"Cannot get a selected photo from the gallery.", e);
				}
			}

			if (mImageToBeAttached != null) {
				if (mCurrentTaskToAttachImage != null) {
					try {
						Task.attachImage(mCurrentTaskToAttachImage,
								mImageToBeAttached);
					} catch (CouchbaseLiteException e) {
						Log.e(Application.TAG,
								"Cannot attach an image to a task.", e);
					}
				} else { // Attach an image for a new task
					Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
							mImageToBeAttached, THUMBNAIL_SIZE_PX,
							THUMBNAIL_SIZE_PX);

					ImageView imageView = (ImageView) getActivity()
							.findViewById(R.id.image);
					imageView.setImageBitmap(thumbnail);
				}
			}

			// Ensure resetting the task to attach an image
			if (mCurrentTaskToAttachImage != null) {
				mCurrentTaskToAttachImage = null;
			}
			if(mImageToBeAttached!=null){
				mImageToBeAttached=null;
			}
		}

		private class TaskAdapter extends LiveQueryAdapter {
			public TaskAdapter(Context context, LiveQuery query) {
				super(context, query);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) parent
							.getContext().getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.view_task, null);
				}

				final Document task = (Document) getItem(position);

				Bitmap image = null;
				Bitmap thumbnail = null;
				java.util.List<Attachment> attachments = task
						.getCurrentRevision().getAttachments();
				if (attachments != null && attachments.size() > 0) {
					Attachment attachment = attachments.get(0);
					try {
						image = BitmapFactory.decodeStream(attachment
								.getContent());
						thumbnail = ThumbnailUtils.extractThumbnail(image,
								THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX);
					} catch (Exception e) {
						Log.e(Application.TAG,
								"Cannot decode the attached image", e);
					}
				}

				final Bitmap displayImage = image;
				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.image);
				if (thumbnail != null) {
					imageView.setImageBitmap(thumbnail);
				} else {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_camera_light));
				}
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (displayImage != null) {
							dispatchImageViewIntent(displayImage);
						} else {
							attachImage(task);
						}
					}
				});

				TextView text = (TextView) convertView.findViewById(R.id.text);
				text.setText((String) task.getProperty("title"));

				final CheckBox checkBox = (CheckBox) convertView
						.findViewById(R.id.checked);
				Boolean checkedProperty = (Boolean) task.getProperty("checked");
				boolean checked = checkedProperty != null ? checkedProperty
						.booleanValue() : false;
				checkBox.setChecked(checked);
				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							Task.updateCheckedStatus(task, checkBox.isChecked());
						} catch (CouchbaseLiteException e) {
							Log.e(Application.TAG,
									"Cannot update checked status", e);
						}
					}
				});

				return convertView;
			}
		}
	}

	@Override
	protected void showSyncProgress(boolean setActive) {
		// TODO Auto-generated method stub

	}
}
