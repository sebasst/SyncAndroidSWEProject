package com.york.cs.todolite2.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.util.Log;
import com.york.cs.services.activities.BaseActivity;
import com.york.cs.services.activities.EntityAdapter;
import com.york.cs.services.authentication.UserManager;
import com.york.cs.todolite2.R;
import com.york.cs.todolite2.document2.Application;
//import com.york.cs.todolite2.document.ListTasks;
//import com.york.cs.todolite2.document.Task;
//import com.york.cs.todolite2.document2.TodoDB;
import com.york.cs.todolite2.document2.ListTasks;
import com.york.cs.todolite2.document2.Task;
import com.york.cs.todolite2.document2.TodoDB;
import com.york.cs.todolite2.util.Utilities;

public class MainActivity extends BaseActivity implements
		ListDrawerFragment.ListSelectionCallback {

	private TodoDB todoDB;
	private ListDrawerFragment mDrawerFragment;

	private CharSequence mTitle;

	Application app;

	private String getCurrentListId() {
		String currentListId = app.getCurrentListId();

		if (currentListId == null) {

			Iterator<ListTasks> listIterator = todoDB.getListTasks().iterator();
			if (listIterator.hasNext()) {
				currentListId = listIterator.next().getId();
			}

		}

		return currentListId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (Application) this.getApplication();
		todoDB = ((Application) this.getApplication()).getTodoDB();
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

		
		Toast.makeText(getApplicationContext(),
				"Shared account: " + UserManager.getCurrentUserId(this),
				Toast.LENGTH_LONG).show();
		;
		

	}

	private void displayListContent(String listDocId) {

		ListTasks list = null;

		list = todoDB.getListTasks().findById(listDocId);
		if (list == null)
			return;
		if (list != null)
			Log.d("on create", "*******************listtype: " + "-title-"
					+ list.getTitle());

		getActionBar().setSubtitle(list.getTitle());

		FragmentManager fragmentManager = getFragmentManager();
		System.out
				.println("2 displayListContent*******************currentListId"
						+ listDocId);
		fragmentManager
				.beginTransaction()
				.replace(R.id.container, TasksFragment.newInstance(listDocId),
						"TaskFragment").commit();
		System.out
				.println("3 displayListContent*******************currentListId"
						+ listDocId);
		Application application = (Application) getApplication();
		application.setCurrentListId(listDocId);
		System.out
				.println("4 displayListContent*******************currentListId"
						+ listDocId);
	}

	public void cleanDisplay() {

		getActionBar().setSubtitle("");

		FragmentManager fragmentManager = getFragmentManager();
		/*
		 * fragmentManager.beginTransaction() .replace(R.id.container,
		 * TasksFragment.newInstance(listDocId)) .commit();
		 */

		Fragment fragment = fragmentManager.findFragmentByTag("TaskFragment");

		if (fragment != null) {

			fragmentManager.beginTransaction().remove(fragment).commit();
		} else {
			
		}


		Application application = (Application) getApplication();
		application.setCurrentListId(null);

	}

	@Override
	public void onListSelected(String id) {
		System.out.println("onlist selected");
		if (id != null) {
			displayListContent(id);
		} else
			cleanDisplay();
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
			addLoginButton(menu);
			// Add sync button button if the user has been logged in
		
			addSyncNowButton(menu);

			// add remocve account button
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
					@SuppressWarnings({ "unused", "static-access" })
					String currentUserId = UserManager
							.getCurrentUserId(((Application) getApplication())
									.getContext());
					
					ListTasks list = new ListTasks();
					list.setTitle(title);
					
					todoDB.getListTasks().add(list);
					todoDB.sync();

					displayListContent(list.getId());
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

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// // Tassk Fragment
	public static class TasksFragment extends Fragment {
		private static final String ARG_LIST_DOC_ID = "id";

		private static final int REQUEST_TAKE_PHOTO = 1;
		private static final int REQUEST_CHOOSE_PHOTO = 2;

		private static final int THUMBNAIL_SIZE_PX = 150;

		private TaskAdapter mAdapter;
		private String mImagePathToBeAttached;
		private Bitmap mImageToBeAttached;

		private Task currentTask;
		List<Task> tasks = new ArrayList<Task>();
		ListTasks listTasks;

		public static TasksFragment newInstance(String id) {

			TasksFragment fragment = new TasksFragment();

			// fragment.requestTasks(id);
			// fragment.mAdapter.notifyDataSetChanged();

			if (id != null) {

				Bundle args = new Bundle();
				args.putString(ARG_LIST_DOC_ID, id);
				fragment.setArguments(args);

			}
			return fragment;
		}

		public TasksFragment() {

		}

		private TodoDB getTodoDB() {
			Application application = (Application) getActivity()
					.getApplication();
			return application.getTodoDB();
		}

		@SuppressLint("SimpleDateFormat")
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

		private void attachImage(final Task task) {
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
						currentTask = task;
						// mCurrentTaskToAttachImage = task;
						dispatchTakePhotoIntent();
					} else if (item == 1) {
						currentTask = task;
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

			@SuppressWarnings("unused")
			long l = byteArray.length;

			Intent intent = new Intent(getActivity(), ImageViewActivity.class);
			intent.putExtra(ImageViewActivity.INTENT_IMAGE, byteArray);
			startActivity(intent);
		}

		private void updateTask(int position) {
			final Task task = (Task) mAdapter.getItem(position);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Title");
			String m_Text = task.getTitle();
			final EditText input = new EditText(getActivity());
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			input.setText(m_Text);
			builder.setView(input);

			// Set up the buttons
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String m_Text = input.getText().toString();
							task.setTitle(m_Text);
							getTodoDB().getTasks().add(task);

							try {
								getTodoDB().sync();
								requestTasks();
								// mAdapter.notifyDataSetChanged();
								mAdapter.update();
							} catch (CouchbaseLiteException e) {
								
								e.printStackTrace();
							}

						}
					});

			builder.show();

		}

		private void deleteTask(int position) {
			Task task = (Task) mAdapter.getItem(position);
			
			listTasks.getTasks().remove(task);
			try {
				getTodoDB().sync();
			} catch (CouchbaseLiteException e) {
				
				e.printStackTrace();
			}

			requestTasks();
			// mAdapter.notifyDataSetChanged();
			mAdapter.update();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final ListView listView = (ListView) inflater.inflate(
					R.layout.fragment_main, container, false);

			
			final String listId = getArguments().getString(ARG_LIST_DOC_ID);
			listTasks = getTodoDB().getListTasks().findById(listId);

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

							Log.d("task fragment create",
									"*-*-*-*-*-*-*-*-*-*-*-*-*listid " + listId);

							Task task = new Task();
							task.setTitle(inputText);

							getTodoDB().getTasks().add(task);
							listTasks.getTasks().add(task);

							try {
								
								if (mImageToBeAttached != null)
									task.setImage(mImageToBeAttached);
								getTodoDB().sync();
							} catch (CouchbaseLiteException e) {
								
								e.printStackTrace();
							}
							requestTasks();
							
							mAdapter.update();
						

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

					Task task = (Task) adapter.getItemAtPosition(position);

					boolean checked = task.getChecked();

					Log.d("*-*-*-*-*-setOnItemClickListener",
							" " + task.getChecked());

					try {

						task.setChecked(checked);

						getTodoDB().sync();

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
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {

							if (item.getItemId() == 2) {
								deleteTask(position - 1);
							} else if (item.getItemId() == 1) {
								updateTask(position - 1);
							}
							return true;
						}
					});
					popup.show();
					return true;
				}
			});

			requestTasks();
			mAdapter = new TaskAdapter(getActivity(), tasks);
			mAdapter.update();
			listView.setAdapter(mAdapter);

			return listView;
		}

		private void requestTasks() {
			try {

				/*
				 * 
				 * java.util.List<Object> startKeys = new ArrayList<Object>();
				 * startKeys.add(listId); startKeys.add(new HashMap<String,
				 * Object>());
				 * 
				 * java.util.List<Object> endKeys = new ArrayList<Object>();
				 * endKeys.add(listId);
				 * 
				 * tasks.clear();
				 * 
				 * Iterator<Task> taskIterator = getTodoDB() .getTasks()
				 * .findByInterval(Task.Task_listTask, 0, 0, listId,
				 * listId).iterator();
				 * 
				 * tasks.addAll(Utilities.copyIterator(taskIterator));
				 *//*
					 * tasks.clear(); Iterator<Task> taskIterator =
					 * listTasks.getTasks().iterator();
					 * tasks.addAll(Utilities.copyIterator(taskIterator));
					 */
			} catch (Exception e) {
			
				e.printStackTrace();
			}

			if (tasks == null)
				tasks = new ArrayList<Task>();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			if (resultCode != RESULT_OK) {
				if (currentTask != null) {
					currentTask = null;
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
				if (currentTask != null) {

					try {
						currentTask.setImage(mImageToBeAttached);

						// requestTasks(currentTask.getListId());
						// mAdapter.notifyDataSetChanged();

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else { // Attach an image for a new task
					Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
							mImageToBeAttached, THUMBNAIL_SIZE_PX,
							THUMBNAIL_SIZE_PX);

					ImageView imageView = (ImageView) getActivity()
							.findViewById(R.id.image);
					imageView.setImageBitmap(thumbnail);

				}

				try {

					Application application = (Application) getActivity()
							.getApplication();

					application.getTodoDB().getTasks().add(currentTask);
					application.getTodoDB().sync();

					// requestTasks(currentTask.getListId());
					// mAdapter.notifyDataSetChanged();
					mAdapter.update();

				} catch (CouchbaseLiteException e) {
					
					e.printStackTrace();
				}
			}

			// Ensure resetting the task to attach an image
			if (currentTask != null) {
				currentTask = null;
			}
		}

		// ///////////////////////////////////////////////////////////////
		private class TaskAdapter extends EntityAdapter<Task> {
			public TaskAdapter(Context context, List<Task> tasks) {

				super(context, tasks);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					// LayoutInflater inflater = (LayoutInflater) parent
					LayoutInflater inflater = (LayoutInflater) context
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.view_task, null);

				}

				final Task task = (Task) getItem(position);

				//System.out.println("-------task on view id" + task.getTitle());

				Bitmap image = null;

				try {
					if(task!=null)image = task.getImage();
				} catch (CouchbaseLiteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Bitmap thumbnail = null;
				if (image != null) {
					System.out.println("-------IMAGE NOT NULLL");
					try {

						thumbnail = ThumbnailUtils.extractThumbnail(image,
								THUMBNAIL_SIZE_PX, THUMBNAIL_SIZE_PX);
					} catch (Exception e) {
						Log.e(Application.TAG,
								"Cannot decode the attached image", e);
					}
				} else {
					System.out.println("-------IMAGE yes NULLL");
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
				text.setText((String) task.getTitle());

				final CheckBox checkBox = (CheckBox) convertView
						.findViewById(R.id.checked);
				Boolean checkedProperty = task.getChecked();
				boolean checked = checkedProperty != null ? checkedProperty
						.booleanValue() : false;
				checkBox.setChecked(checked);
				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						task.setChecked(checkBox.isChecked());

						Application application = (Application) getActivity()
								.getApplication();

						try {
							application.getTodoDB().sync();
						} catch (CouchbaseLiteException e) {
							
							e.printStackTrace();
						}

					}
				});

				return convertView;
			}

			@Override
			public void update() {
				System.out.println("-------- update in task");
				tasks.clear();

				TodoDB todoDB = ((Application) Application.getContext())
						.getTodoDB();

				if (listTasks != null && listTasks.getTasks() != null) {
					listTasks = todoDB.getListTasks().findById(
							listTasks.getId());
					if (listTasks != null) {
						Iterator<Task> taskIterator = listTasks.getTasks()
								.iterator();
						tasks.addAll(Utilities.copyIterator(taskIterator));
					}
					System.out.println("-------- update in task "
							+ tasks.size());
				}
				if (tasks == null)
					tasks = new ArrayList<Task>();
				notifyDataSetChanged();
			}
		}

	}

	@Override
	protected void showSyncProgress(boolean setActive) {

		setProgressBarIndeterminateVisibility(true);
		MainActivity.this.setProgressBarIndeterminateVisibility(setActive);

	}

}
